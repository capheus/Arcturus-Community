package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.util.Collection;
import java.util.Map;

public class RoomUserStatusComposer extends MessageComposer
{
    private Collection<Habbo> habbos;
    private THashSet<RoomUnit> roomUnits;

    public RoomUserStatusComposer(RoomUnit roomUnit)
    {
        this.roomUnits = new THashSet<>();
        this.roomUnits.add(roomUnit);
    }

    public RoomUserStatusComposer(THashSet<RoomUnit> roomUnits, boolean value)
    {
        this.roomUnits = roomUnits;
    }

    public RoomUserStatusComposer(Collection<Habbo> habbos)
    {
        this.habbos = habbos;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserStatusComposer);
        if(this.roomUnits != null)
        {
            this.response.appendInt(this.roomUnits.size());
            for(RoomUnit roomUnit : this.roomUnits)
            {
                this.response.appendInt(roomUnit.getId());
                this.response.appendInt(roomUnit.getPreviousLocation().x);
                this.response.appendInt(roomUnit.getPreviousLocation().y);
                this.response.appendString(roomUnit.getPreviousLocationZ() + "");








                this.response.appendInt(roomUnit.getHeadRotation().getValue());
                this.response.appendInt(roomUnit.getBodyRotation().getValue());

                String status = "/";
                for (Map.Entry<RoomUnitStatus, String> entry : roomUnit.getStatusMap().entrySet())
                {
                    status += entry.getKey() + " " + entry.getValue() + "/";
                }

                this.response.appendString(status);
                roomUnit.setPreviousLocation(roomUnit.getCurrentLocation());
            }
        }
        else {
            synchronized (this.habbos)
            {
                this.response.appendInt(this.habbos.size());
                for (Habbo habbo : this.habbos)
                {
                    this.response.appendInt(habbo.getRoomUnit().getId());
                    this.response.appendInt(habbo.getRoomUnit().getPreviousLocation().x);
                    this.response.appendInt(habbo.getRoomUnit().getPreviousLocation().y);
                    this.response.appendString(habbo.getRoomUnit().getPreviousLocationZ() + "");








                    this.response.appendInt(habbo.getRoomUnit().getHeadRotation().getValue());
                    this.response.appendInt(habbo.getRoomUnit().getBodyRotation().getValue());

                    String status = "/";

                    for (Map.Entry<RoomUnitStatus, String> entry : habbo.getRoomUnit().getStatusMap().entrySet())
                    {
                        status += entry.getKey() + " " + entry.getValue() + "/";
                    }
                    this.response.appendString(status);
                    habbo.getRoomUnit().setPreviousLocation(habbo.getRoomUnit().getCurrentLocation());
                }
            }
        }
        return this.response;
    }
}
