package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUnitOnRollerComposer extends MessageComposer
{
    private final RoomUnit roomUnit;
    private final HabboItem roller;
    private final RoomTile oldLocation;
    private final double oldZ;
    private final RoomTile newLocation;
    private final double newZ;
    private final Room room;

    public RoomUnitOnRollerComposer(RoomUnit roomUnit, HabboItem roller, RoomTile oldLocation, double oldZ, RoomTile newLocation, double newZ, Room room)
    {
        this.roomUnit = roomUnit;
        this.roller = roller;
        this.oldLocation = oldLocation;
        this.oldZ = oldZ;
        this.newLocation = newLocation;
        this.newZ = newZ;
        this.room = room;
    }

    public RoomUnitOnRollerComposer(RoomUnit roomUnit, RoomTile newLocation, Room room)
    {
        this.roomUnit = roomUnit;
        this.roller = null;
        this.oldLocation = this.roomUnit.getCurrentLocation();
        this.oldZ = this.roomUnit.getZ();
        this.newLocation = newLocation;
        this.newZ = this.newLocation.getStackHeight();
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        if(!this.room.isLoaded())
            return null;

        if (!this.room.isAllowWalkthrough() && this.roller != null)
        {
            if (this.room.hasHabbosAt(this.newLocation.x, this.newLocation.y))
            {
                return null;
            }
        }

        this.response.init(Outgoing.ObjectOnRollerComposer);
        this.response.appendInt(this.oldLocation.x);
        this.response.appendInt(this.oldLocation.y);
        this.response.appendInt(this.newLocation.x);
        this.response.appendInt(this.newLocation.y);
        this.response.appendInt(0);
        this.response.appendInt(this.roller == null ? 0 : this.roller.getId());
        this.response.appendInt(2);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendString(this.oldZ + "");
        this.response.appendString(this.newZ + "");

        if (this.roller != null)
        {
            RoomTile rollerTile = room.getLayout().getTile(this.roller.getX(), this.roller.getY());





                    if (RoomUnitOnRollerComposer.this.oldLocation == rollerTile && RoomUnitOnRollerComposer.this.roomUnit.getGoal() == rollerTile)
                    {
                        RoomUnitOnRollerComposer.this.roomUnit.setLocation(room.getLayout().getTile(newLocation.x, newLocation.y));
                        RoomUnitOnRollerComposer.this.roomUnit.setPreviousLocationZ(RoomUnitOnRollerComposer.this.newLocation.getStackHeight());
                        RoomUnitOnRollerComposer.this.roomUnit.setZ(RoomUnitOnRollerComposer.this.newLocation.getStackHeight());
                        RoomUnitOnRollerComposer.this.roomUnit.sitUpdate = true;
                    }

            //});
        }
        else
        {
            this.roomUnit.setLocation(this.newLocation);
            this.roomUnit.setZ(this.newZ);
        }

        return this.response;
    }
}
