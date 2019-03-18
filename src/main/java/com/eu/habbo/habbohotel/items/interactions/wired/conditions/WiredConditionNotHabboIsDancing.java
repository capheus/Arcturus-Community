package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.wired.WiredConditionOperator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotHabboIsDancing extends WiredConditionGroupMember
{
    public WiredConditionNotHabboIsDancing(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredConditionNotHabboIsDancing(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        return roomUnit.getDanceType() == DanceType.NONE;
    }

    @Override
    public WiredConditionOperator operator()
    {
        return WiredConditionOperator.OR;
    }
}