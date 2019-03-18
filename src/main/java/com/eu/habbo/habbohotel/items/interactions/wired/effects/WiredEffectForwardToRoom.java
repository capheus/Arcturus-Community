package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectForwardToRoom extends WiredEffectWhisper
{
    public WiredEffectForwardToRoom(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectForwardToRoom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo == null)
            return false;

        int roomId;

        try
        {
            roomId = Integer.valueOf(this.message);
        }
        catch (Exception e)
        {
            return false;
        }

        if(roomId > 0)
            habbo.getClient().sendResponse(new ForwardToRoomComposer(roomId));

        return true;
    }
}
