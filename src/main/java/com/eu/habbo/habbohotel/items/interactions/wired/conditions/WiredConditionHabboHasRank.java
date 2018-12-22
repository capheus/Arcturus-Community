package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredConditionOperator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionHabboHasRank extends WiredConditionHabboWearsBadge
{
    public WiredConditionHabboHasRank(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredConditionHabboHasRank(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null)
        {
            try
            {
                return habbo.getHabboInfo().getRank().getId() == Integer.valueOf(this.badge);
            }
            catch (Exception e)
            {
                return false;
            }
        }

        return false;
    }

    @Override
    public WiredConditionOperator operator()
    {
        return WiredConditionOperator.OR;
    }
}
