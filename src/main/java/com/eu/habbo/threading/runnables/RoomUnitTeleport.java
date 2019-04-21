package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;

import java.util.LinkedList;

public class RoomUnitTeleport implements Runnable
{
    private RoomUnit roomUnit;
    private Room room;
    private int x;
    private int y;
    private double z;

    private int newEffect;

    public RoomUnitTeleport(RoomUnit roomUnit, Room room, int x, int y, double z, int newEffect)
    {
        this.roomUnit = roomUnit;
        this.room = room;
        this.x = x;
        this.y = y;
        this.z = z;
        this.newEffect = newEffect;
    }

    @Override
    public void run()
    {
        RoomTile t = this.room.getLayout().getTile((short) this.x, (short) this.y);

        HabboItem topItem = this.room.getTopItemAt(this.roomUnit.getCurrentLocation().x, this.roomUnit.getCurrentLocation().y);
        if (topItem != null)
        {
            try
            {
                topItem.onWalkOff(this.roomUnit, this.room, new Object[]{this});
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
        this.roomUnit.setPath(new LinkedList<>());
        this.roomUnit.setCurrentLocation(t);
        this.roomUnit.setPreviousLocation(t);
        this.roomUnit.setZ(this.z);
        this.roomUnit.setPreviousLocationZ(this.z);
        this.roomUnit.removeStatus(RoomUnitStatus.MOVE);
        ServerMessage teleportMessage = new RoomUnitOnRollerComposer(this.roomUnit, t, this.room).compose();
        this.roomUnit.setLocation(t);
        this.room.sendComposer(teleportMessage);








        this.room.updateHabbosAt(t.x, t.y);
    }
}
