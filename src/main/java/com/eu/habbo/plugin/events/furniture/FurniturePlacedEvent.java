package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurniturePlacedEvent extends FurnitureUserEvent
{

    public final RoomTile location;


    public FurniturePlacedEvent(HabboItem furniture, Habbo habbo, RoomTile location)
    {
        super(furniture, habbo);

        this.location = location;
    }
}
