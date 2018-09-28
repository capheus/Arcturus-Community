package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTrap extends InteractionPressurePlate
{
    public InteractionTrap(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionTrap(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        int delay = Emulator.getConfig().getInt("hotel.item.trap." + this.getBaseItem().getName());

        if (delay == 0)
        {
            Emulator.getConfig().register("hotel.item.trap." + this.getBaseItem().getName(), "3000");
            delay = 3000;
        }

        roomUnit.setCanWalk(false);
        Emulator.getThreading().run(new Runnable()
        {
            @Override
            public void run()
            {
                room.giveEffect(roomUnit, 0);
                roomUnit.setCanWalk(true);
            }
        }, delay);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
    }
}
