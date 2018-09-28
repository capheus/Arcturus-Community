package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotHabboHasCredits extends WiredConditionNotHabboHasEffect
{
    public WiredConditionNotHabboHasCredits(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredConditionNotHabboHasCredits(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null)
        {
            return habbo.getHabboInfo().getCredits() < this.effectId;
        }

        return false;
    }
}
