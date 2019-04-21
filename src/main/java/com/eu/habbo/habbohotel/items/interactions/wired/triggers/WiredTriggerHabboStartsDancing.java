package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerHabboStartsDancing extends WiredTriggerCollision
{
    public WiredTriggerHabboStartsDancing(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredTriggerHabboStartsDancing(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public WiredTriggerType getType()
    {
        return WiredTriggerType.STARTS_DANCING;
    }
    
    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        return true;
    }
}