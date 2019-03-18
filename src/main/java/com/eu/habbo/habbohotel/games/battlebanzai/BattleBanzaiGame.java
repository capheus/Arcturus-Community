package com.eu.habbo.habbohotel.games.battlebanzai;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.*;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTile;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGate;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboard;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.threading.runnables.BattleBanzaiTilesFlicker;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Collection;
import java.util.Map;

public class BattleBanzaiGame extends Game
{

    public static final int effectId = 33;


    public static final int POINTS_HIJACK_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.steal");


    public static final int POINTS_FILL_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.fill");


    public static final int POINTS_LOCK_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.lock");

    private int timeLeft;

    private int tileCount;

    private int countDown;


    private final THashMap<GameTeamColors, THashSet<HabboItem>> lockedTiles;

    private final THashMap<Integer, HabboItem> gameTiles;

    public BattleBanzaiGame(Room room)
    {
        super(BattleBanzaiGameTeam.class, BattleBanzaiGamePlayer.class, room, true);

        this.lockedTiles = new THashMap<>();
        this.gameTiles = new THashMap<>();

        room.setAllowEffects(true);
    }

    @Override
    public void initialise()
    {
        if(!this.state.equals(GameState.IDLE))
            return;

        int highestTime = 0;
        this.countDown = 3;

        this.resetMap();

        for (Map.Entry<Integer, InteractionBattleBanzaiTimer> set : this.room.getRoomSpecialTypes().getBattleBanzaiTimers().entrySet())
        {
            if(set.getValue().getExtradata().isEmpty())
                continue;

            if(highestTime < Integer.valueOf(set.getValue().getExtradata()))
            {
                highestTime = Integer.valueOf(set.getValue().getExtradata());
            }
        }

        synchronized (this.teams)
        {
            for (GameTeam t : this.teams.values())
            {
                t.initialise();
            }
        }

        for(HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
        {
            item.setExtradata("1");
            this.room.updateItemState(item);
        }

        this.timeLeft = highestTime;

        if (this.timeLeft == 0)
        {
            this.timeLeft = 30;
        }

        this.start();
    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor)
    {
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void start()
    {
        if(!this.state.equals(GameState.IDLE))
            return;

        super.start();

        this.refreshGates();

        Emulator.getThreading().run(this, 0);
    }

    @Override
    public void run()
    {
        try
        {
            if (this.state.equals(GameState.IDLE))
                return;

            if(this.countDown > 0)
            {
                this.countDown--;

                if(this.countDown == 0)
                {
                    for(HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
                    {
                        item.setExtradata("2");
                        this.room.updateItemState(item);
                    }
                }

                if(this.countDown > 1)
                {
                    Emulator.getThreading().run(this, 500);

                    return;
                }
            }

            if (this.timeLeft > 0)
            {
                Emulator.getThreading().run(this, 1000);

                if (this.state.equals(GameState.PAUSED)) return;

                this.timeLeft--;

                for (Map.Entry<Integer, InteractionBattleBanzaiTimer> set : this.room.getRoomSpecialTypes().getBattleBanzaiTimers().entrySet())
                {
                    set.getValue().setExtradata(this.timeLeft + "");
                    this.room.updateItemState(set.getValue());
                }

                int total = 0;
                synchronized (this.lockedTiles)
                {
                    for (Map.Entry<GameTeamColors, THashSet<HabboItem>> set : this.lockedTiles.entrySet())
                    {
                        total += set.getValue().size();
                    }
                }

                GameTeam highestScore = null;

                synchronized (this.teams)
                {
                    for (Map.Entry<GameTeamColors, GameTeam> set : this.teams.entrySet())
                    {
                        if (highestScore == null || highestScore.getTotalScore() < set.getValue().getTotalScore())
                        {
                            highestScore = set.getValue();
                        }
                    }
                }

                if(highestScore != null)
                {
                    for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
                    {
                        item.setExtradata((highestScore.teamColor.type + 3) + "");
                        this.room.updateItemState(item);
                    }
                }

                if(total >= this.tileCount && this.tileCount != 0)
                {
                    this.timeLeft = 0;
                }
            }
            else
            {

                GameTeam winningTeam = null;

                for (GameTeam team : this.teams.values())
                {
                    for(GamePlayer player : team.getMembers())
                    {
                        if (player.getScore() > 0)
                        {
                            AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallPlayer"));
                            AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallQuestCompleted"));
                        }
                    }

                    if (winningTeam == null || team.getTotalScore() > winningTeam.getTotalScore())
                    {
                        winningTeam = team;
                    }
                }

                if (winningTeam != null)
                {
                    for (GamePlayer player : winningTeam.getMembers())
                    {
                        if (player.getScore() > 0)
                        {
                            this.room.sendComposer(new RoomUserActionComposer(player.getHabbo().getRoomUnit(), RoomUserAction.WAVE).compose());
                            AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallWinner"));
                        }
                    }

                    for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
                    {
                        item.setExtradata((7 + winningTeam.teamColor.type) + "");
                        this.room.updateItemState(item);
                    }

                    Emulator.getThreading().run(new BattleBanzaiTilesFlicker(this.lockedTiles.get(winningTeam.teamColor), winningTeam.teamColor, this.room));
                }
                
                this.stop();
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    @Override
    public void stop()
    {
        super.stop();

        this.timeLeft = 0;

        this.refreshGates();

        for (HabboItem tile : this.gameTiles.values())
        {
            if (tile.getExtradata().equals("1"))
            {
                tile.setExtradata("0");
                this.room.updateItemState(tile);
            }
        }
        this.lockedTiles.clear();
    }


    private synchronized void resetMap()
    {
        for (HabboItem item : this.room.getFloorItems())
        {
            if (item instanceof InteractionBattleBanzaiTile)
            {
                item.setExtradata("1");
                this.room.updateItemState(item);
                this.tileCount++;
                this.gameTiles.put(item.getId(), item);
            }

            if (item instanceof InteractionBattleBanzaiScoreboard)
            {
                item.setExtradata("0");
                this.room.updateItemState(item);
            }
        }
    }

    public void addPositionToGate(GameTeamColors teamColor)
    {
        for (InteractionBattleBanzaiGate gate : this.room.getRoomSpecialTypes().getBattleBanzaiGates().values())
        {
            if (gate.teamColor != teamColor)
                continue;

            if (gate.getExtradata().isEmpty() || gate.getExtradata().equals("0"))
                continue;

            gate.setExtradata(Integer.valueOf(gate.getExtradata()) - 1 + "");
            this.room.updateItemState(gate);
            break;
        }
    }


    public void tileLocked(GameTeamColors teamColor, HabboItem item, Habbo habbo)
    {
        if(item instanceof InteractionBattleBanzaiTile)
        {
            if(!this.lockedTiles.containsKey(teamColor))
            {
                this.lockedTiles.put(teamColor, new THashSet<>());
            }

            this.lockedTiles.get(teamColor).add(item);
        }

        if(habbo != null)
        {
            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallTilesLocked"));
        }
    }


    public void refreshCounters()
    {
        for(GameTeam team : this.teams.values())
        {
            if(team.getMembers().isEmpty())
                continue;

            this.refreshCounters(team.teamColor);
        }
    }


    public void refreshCounters(GameTeamColors teamColors)
    {
        int totalScore = this.teams.get(teamColors).getTotalScore();

        THashMap<Integer, InteractionBattleBanzaiScoreboard> scoreBoards = this.room.getRoomSpecialTypes().getBattleBanzaiScoreboards(teamColors);

        for (InteractionBattleBanzaiScoreboard scoreboard : scoreBoards.values())
        {
            if(scoreboard.getExtradata().isEmpty())
            {
                scoreboard.setExtradata("0");
            }

            int oldScore = Integer.valueOf(scoreboard.getExtradata());

            if(oldScore == totalScore)
                continue;

            scoreboard.setExtradata(totalScore + "");
            this.room.updateItemState(scoreboard);
        }
    }

    private void refreshGates()
    {
        Collection<InteractionBattleBanzaiGate> gates = this.room.getRoomSpecialTypes().getBattleBanzaiGates().values();
        THashSet<RoomTile> tilesToUpdate = new THashSet<>(gates.size());
        for (HabboItem item : gates)
        {
            tilesToUpdate.add(this.room.getLayout().getTile(item.getX(), item.getY()));
        }

        this.room.updateTiles(tilesToUpdate);
    }

    public void markTile(Habbo habbo, InteractionBattleBanzaiTile tile, int state)
    {
        if (!this.gameTiles.contains(tile.getId())) return;

        int check = state - (habbo.getHabboInfo().getGamePlayer().getTeamColor().type * 3);
        if(check == 3 || check == 4)
        {
            state++;

            if(state % 3 == 2)
            {
                habbo.getHabboInfo().getGamePlayer().addScore(BattleBanzaiGame.POINTS_LOCK_TILE);
                this.tileLocked(habbo.getHabboInfo().getGamePlayer().getTeamColor(), tile, habbo);
            }
            else
            {
                habbo.getHabboInfo().getGamePlayer().addScore(BattleBanzaiGame.POINTS_FILL_TILE);
            }
        }
        else
        {
            state = (habbo.getHabboInfo().getGamePlayer().getTeamColor().type * 3) + 3;

            habbo.getHabboInfo().getGamePlayer().addScore(BattleBanzaiGame.POINTS_HIJACK_TILE);
        }

        this.refreshCounters(habbo.getHabboInfo().getGamePlayer().getTeamColor());
        tile.setExtradata(state + "");
        this.room.updateItem(tile);
    }
}
