package com.eu.habbo.habbohotel.items.interactions.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionGameTimer extends HabboItem
{
    private int baseTime = 0;
    private int lastToggle = 0;

    public InteractionGameTimer(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);

        String[] data = set.getString("extra_data").split("\t");

        if (data.length >= 2)
        {
            this.baseTime = Integer.valueOf(data[1]);
        }

        if (data.length >= 1)
        {
            this.setExtradata(data[0]);
        }
    }

    public InteractionGameTimer(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onPickUp(Room room)
    {
        this.setExtradata("0");
    }

    @Override
    public void onPlace(Room room)
    {
        this.baseTime = 30;
        this.setExtradata("30");
        room.updateItem(this);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return false;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if (client != null)
        {
            if (!(room.hasRights(client.getHabbo()) || client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER)))
                return;
        }

        if (client == null)
        {
            int now = Emulator.getIntUnixTimestamp();
            if (now - this.lastToggle < 3) return;
            this.lastToggle = now;
        }

        if(this.getExtradata().isEmpty())
        {
            this.setExtradata("0");
        }

        Game game = this.getOrCreateGame(room);

        if ((objects.length >= 2 && objects[1] instanceof WiredEffectType))
        {
            if (game.state.equals(GameState.RUNNING))
                return;
        }

        if(objects.length >= 1 && objects[0] instanceof Integer && client != null)
        {
            int state = (Integer)objects[0];

            switch (state)
            {
                case 1:
                {
                    this.startGame(room);
                    break;
                }

                case 2:
                {
                    this.increaseTimer(room);
                }
                break;

                case 3:
                {
                    this.stopGame(room);
                }
                break;
            }
        }
        else
        {

            if (game != null && game.state.equals(GameState.IDLE))
            {
                this.startGame(room);
            }
        }

        super.onClick(client, room, objects);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    private Game getOrCreateGame(Room room)
    {
        Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

        if (game == null)
        {
            try
            {
                game = this.getGameType().getDeclaredConstructor(Room.class).newInstance(room);
                room.addGame(game);
            }
            catch (Exception e)
            {

            }
        }

        return game;
    }

    private void startGame(Room room)
    {
        this.needsUpdate(true);
        try
        {

            room.updateItem(this);

            Game game = this.getOrCreateGame(room);

            if (game.state.equals(GameState.IDLE))
            {
                this.setExtradata(this.baseTime + "");
                game.initialise();
            }
            else if (game.state.equals(GameState.PAUSED))
            {
                game.unpause();
            }
            else if (game.state.equals(GameState.RUNNING))
            {
                game.pause();
            }

            //}
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    private void stopGame(Room room)
    {
        this.setExtradata(this.baseTime + "");
        this.needsUpdate(true);
        Game game = this.getOrCreateGame(room);

        if(game != null && game.state != GameState.IDLE)
        {
            game.stop();
        }

        room.updateItem(this);
    }

    private void increaseTimer(Room room)
    {
        Game game = this.getOrCreateGame(room);

        if (game == null) return;
        if (game.state.equals(GameState.PAUSED))
        {
            stopGame(room);
            return;
        }
        if (game.state.equals(GameState.RUNNING)) return;

        this.needsUpdate(true);
        switch(this.baseTime)
        {
            case 0:     this.baseTime = 30; break;
            case 30:    this.baseTime = 60; break;
            case 60:    this.baseTime = 120; break;
            case 120:   this.baseTime = 180; break;
            case 180:   this.baseTime = 300; break;
            case 300:   this.baseTime = 600; break;
            //case 600:   this.baseTime = 0; break;

            default:
                this.baseTime = 30;
        }

        this.setExtradata(this.baseTime + "");

        room.updateItem(this);
    }

    @Override
    public String getDatabaseExtraData()
    {
        return this.getExtradata() + "\t" + this.baseTime;
    }

    public Class<? extends Game> getGameType()
    {
        return WiredGame.class;
    }

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }
}
