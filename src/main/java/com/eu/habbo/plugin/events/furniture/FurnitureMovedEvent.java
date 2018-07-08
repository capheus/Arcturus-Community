package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurnitureMovedEvent extends FurnitureUserEvent
{

    public final RoomTile oldPosition;


    public final RoomTile newPosition;


    public FurnitureMovedEvent(HabboItem furniture, Habbo habbo, RoomTile oldPosition, RoomTile newPosition)
    {
        super(furniture, habbo);

        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }
}
