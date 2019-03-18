package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionDefault extends HabboItem
{
    public InteractionDefault(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionDefault(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean isWalkable()
    {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(room != null && (client == null || this.canToggle(client.getHabbo(), room) || (objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE)))
        {
            super.onClick(client, room, objects);

            if (objects != null && objects.length > 0)
            {
                if (objects[0] instanceof Integer)
                {
                    if (this.getExtradata().length() == 0)
                        this.setExtradata("0");

                    if (this.getBaseItem().getStateCount() > 0)
                    {
                        int currentState = 0;

                        try
                        {
                            currentState = Integer.valueOf(this.getExtradata());
                        }
                        catch (NumberFormatException e)
                        {
                            Emulator.getLogging().logErrorLine("Incorrect extradata (" + this.getExtradata() + ") for item ID (" + this.getId() + ") of type (" + this.getBaseItem().getName() + ")");
                        }

                        this.setExtradata("" + (currentState + 1) % this.getBaseItem().getStateCount());
                        this.needsUpdate(true);

                        room.updateItemState(this);
                    }
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects)  throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        if (roomUnit != null)
        {
            if (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0)
            {
                if (roomUnit.getRoomUnitType().equals(RoomUnitType.USER))
                {
                    Habbo habbo = room.getHabbo(roomUnit);

                    if (habbo != null)
                    {
                        if (habbo.getHabboInfo().getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0 && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectM())
                        {
                            room.giveEffect(habbo, this.getBaseItem().getEffectM(), -1);
                            return;
                        }

                        if (habbo.getHabboInfo().getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0 && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectF())
                        {
                            room.giveEffect(habbo, this.getBaseItem().getEffectF(), -1);
                        }
                    }
                }
                else if (roomUnit.getRoomUnitType().equals(RoomUnitType.BOT))
                {
                    Bot bot = room.getBot(roomUnit);

                    if (bot != null)
                    {
                        if (bot.getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0 && roomUnit.getEffectId() != this.getBaseItem().getEffectM())
                        {
                            room.giveEffect(bot.getRoomUnit(), this.getBaseItem().getEffectM(), -1);
                            return;
                        }
                        if (bot.getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0 && roomUnit.getEffectId() != this.getBaseItem().getEffectF())
                        {
                            room.giveEffect(bot.getRoomUnit(), this.getBaseItem().getEffectF(), -1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects)  throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);

        if (roomUnit != null)
        {
            if (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0)
            {
                if (objects != null && objects.length == 2)
                {
                    if (objects[0] instanceof RoomTile && objects[1] instanceof RoomTile)
                    {
                        RoomTile goalTile = (RoomTile) objects[1];
                        HabboItem topItem = room.getTopItemAt(goalTile.x, goalTile.y);

                        if (topItem != null && (topItem.getBaseItem().getEffectM() == this.getBaseItem().getEffectM() || topItem.getBaseItem().getEffectF() == this.getBaseItem().getEffectF()))
                        {
                            return;
                        }
                    }
                }

                if (roomUnit.getRoomUnitType().equals(RoomUnitType.USER))
                {
                    Habbo habbo = room.getHabbo(roomUnit);

                    if (habbo != null)
                    {

                        if (habbo.getHabboInfo().getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0)
                        {
                            room.giveEffect(habbo, 0, -1);
                            return;
                        }

                        if (habbo.getHabboInfo().getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0)
                        {
                            room.giveEffect(habbo, 0, -1);
                        }
                    }
                }
                else if (roomUnit.getRoomUnitType().equals(RoomUnitType.BOT))
                {
                    Bot bot = room.getBot(roomUnit);

                    if (bot != null)
                    {
                        if (bot.getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0)
                        {
                            room.giveEffect(roomUnit, 0, -1);
                            return;
                        }

                        if (bot.getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0)
                        {
                            room.giveEffect(roomUnit, 0, -1);
                        }
                    }
                }
            }
        }
    }

    public boolean canToggle(Habbo habbo, Room room)
    {
        return room.hasRights(habbo);
    }

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }
}
