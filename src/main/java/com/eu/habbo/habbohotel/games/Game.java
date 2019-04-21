package com.eu.habbo.habbohotel.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredBlob;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerTeamLoses;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerTeamWins;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.games.GameHabboJoinEvent;
import com.eu.habbo.plugin.events.games.GameHabboLeaveEvent;
import com.eu.habbo.plugin.events.games.GameStartedEvent;
import com.eu.habbo.plugin.events.games.GameStoppedEvent;
import com.eu.habbo.threading.runnables.SaveScoreForTeam;
import gnu.trove.map.hash.THashMap;

import java.util.Map;

public abstract class Game implements Runnable
{

    public final Class<? extends GameTeam> gameTeamClazz;


    public final Class<? extends GamePlayer> gamePlayerClazz;


    protected final THashMap<GameTeamColors, GameTeam> teams = new THashMap<>();


    protected final Room room;


    protected final boolean countsAchievements;


    protected int startTime;


    protected int pauseTime;


    protected int endTime;


    public GameState state = GameState.IDLE;

    public Game(Class<? extends GameTeam> gameTeamClazz, Class<? extends GamePlayer> gamePlayerClazz, Room room, boolean countsAchievements)
    {
        this.gameTeamClazz = gameTeamClazz;
        this.gamePlayerClazz = gamePlayerClazz;
        this.room = room;
        this.countsAchievements = countsAchievements;
    }


    public abstract void initialise();


    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor)
    {
        try
        {
            if (habbo != null)
            {
                if(Emulator.getPluginManager().isRegistered(GameHabboJoinEvent.class, true))
                {
                    Event gameHabboJoinEvent = new GameHabboJoinEvent(this, habbo);
                    Emulator.getPluginManager().fireEvent(gameHabboJoinEvent);
                    if(gameHabboJoinEvent.isCancelled())
                        return false;
                }

                synchronized (this.teams)
                {
                    GameTeam team = this.getTeam(teamColor);
                    if (team == null)
                    {
                        team = this.gameTeamClazz.getDeclaredConstructor(GameTeamColors.class).newInstance(teamColor);
                        this.addTeam(team);
                    }

                    GamePlayer player = this.gamePlayerClazz.getDeclaredConstructor(Habbo.class, GameTeamColors.class).newInstance(habbo, teamColor);
                    team.addMember(player);
                    habbo.getHabboInfo().setCurrentGame(this.getClass());
                    habbo.getHabboInfo().setGamePlayer(player);
                }

                return true;
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return false;
    }


    public void removeHabbo(Habbo habbo)
    {
        if (habbo != null)
        {
            if(Emulator.getPluginManager().isRegistered(GameHabboLeaveEvent.class, true))
            {
                Event gameHabboLeaveEvent = new GameHabboLeaveEvent(this, habbo);
                Emulator.getPluginManager().fireEvent(gameHabboLeaveEvent);
                if(gameHabboLeaveEvent.isCancelled())
                    return;
            }

            GameTeam team = this.getTeamForHabbo(habbo);
            if (team != null && team.isMember(habbo))
            {
                team.removeMember(habbo.getHabboInfo().getGamePlayer());
                habbo.getHabboInfo().getGamePlayer().reset();
                habbo.getHabboInfo().setCurrentGame(null);
                habbo.getHabboInfo().setGamePlayer(null);

                if(this.countsAchievements && this.endTime > this.startTime)
                {
                    AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GamePlayed"));
                }
            }
        }

        boolean deleteGame = true;
        for (GameTeam team : this.teams.values())
        {
            if (team.getMembers().size() > 0 )
            {
                deleteGame = false;
                break;
            }
        }

        if (deleteGame)
        {
            this.room.deleteGame(this);
        }
    }


    public void start()
    {
        this.state = GameState.RUNNING;
        this.startTime = Emulator.getIntUnixTimestamp();

        if(Emulator.getPluginManager().isRegistered(GameStartedEvent.class, true))
        {
            Event gameStartedEvent = new GameStartedEvent(this);
            Emulator.getPluginManager().fireEvent(gameStartedEvent);
        }

        WiredHandler.handle(WiredTriggerType.GAME_STARTS, null, this.room, new Object[]{this});

        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(WiredBlob.class))
        {
            item.setExtradata("0");
            this.room.updateItem(item);
        }
    }


    public abstract void run();

    public void pause()
    {
        if (this.state.equals(GameState.RUNNING))
        {
            this.state = GameState.PAUSED;
            this.pauseTime = Emulator.getIntUnixTimestamp();
        }
    }

    public void unpause()
    {
        if (this.state.equals(GameState.PAUSED))
        {
            this.state = GameState.RUNNING;
            this.endTime = Emulator.getIntUnixTimestamp() + (this.endTime - this.pauseTime);
        }
    }

    public void stop()
    {
        this.state = GameState.IDLE;
        this.endTime = Emulator.getIntUnixTimestamp();

        this.saveScores();

        GameTeam winningTeam = null;
        for (GameTeam team : this.teams.values())
        {
            if (winningTeam == null || team.getTotalScore() > winningTeam.getTotalScore())
            {
                winningTeam = team;
            }
        }

        if (winningTeam != null)
        {
            for (GamePlayer player : winningTeam.getMembers())
            {
                WiredHandler.handleCustomTrigger(WiredTriggerTeamWins.class, player.getHabbo().getRoomUnit(), this.room, new Object[]{this});
            }

            for (GameTeam team : this.teams.values())
            {
                if (team == winningTeam) continue;

                for (GamePlayer player : winningTeam.getMembers())
                {
                    WiredHandler.handleCustomTrigger(WiredTriggerTeamLoses.class, player.getHabbo().getRoomUnit(), this.room, new Object[]{this});
                }
            }
        }

        if(Emulator.getPluginManager().isRegistered(GameStoppedEvent.class, true))
        {
            Event gameStoppedEvent = new GameStoppedEvent(this);
            Emulator.getPluginManager().fireEvent(gameStoppedEvent);
        }

        WiredHandler.handle(WiredTriggerType.GAME_ENDS, null, this.room, new Object[]{this});

        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class))
        {
            this.room.updateItem(item);
        }
    }


    private void saveScores()
    {
        if(this.room == null)
            return;

        for(Map.Entry<GameTeamColors, GameTeam> teamEntry : this.teams.entrySet())
        {
            Emulator.getThreading().run(new SaveScoreForTeam(teamEntry.getValue(), this));
        }
    }


    public GameTeam getTeamForHabbo(Habbo habbo)
    {
        if(habbo != null)
        {
            synchronized (this.teams)
            {
                for (GameTeam team : this.teams.values())
                {
                    if (team.isMember(habbo))
                    {
                        return team;
                    }
                }
            }
        }

        return null;
    }


    public GameTeam getTeam(GameTeamColors teamColor)
    {
        synchronized (this.teams)
        {
            return this.teams.get(teamColor);
        }
    }


    public void addTeam(GameTeam team)
    {
        synchronized (this.teams)
        {
            this.teams.put(team.teamColor, team);
        }
    }


    public Room getRoom()
    {
        return this.room;
    }


    public int getStartTime()
    {
        return this.startTime;
    }


    public int getEndTime()
    {
        return this.endTime;
    }


    public void addTime(int time)
    {
        this.endTime += time;
    }
}
