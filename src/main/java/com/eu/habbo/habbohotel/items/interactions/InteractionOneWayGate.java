package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.ItemIntStateComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionOneWayGate extends HabboItem
{
    private int roomUnitID = -1;
    public InteractionOneWayGate(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionOneWayGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return roomUnit.getId() == this.roomUnitID;
    }

    @Override
    public boolean isWalkable()
    {
        return this.roomUnitID != -1;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        if(this.getExtradata().length() == 0)
        {
            this.setExtradata("0");
            this.needsUpdate(true);
        }

        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onClick(final GameClient client, final Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if (client != null)
        {
            RoomTile tile = room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), this.getRotation());
            RoomTile gatePosition = room.getLayout().getTile(this.getX(), this.getY());

            if (tile != null && tile.equals(client.getHabbo().getRoomUnit().getCurrentLocation()))
            {
                InteractionOneWayGate gate = this;
                if (!room.hasHabbosAt(this.getX(), this.getY()) && gate.roomUnitID == -1)
                {
                    room.scheduledTasks.add(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            gate.roomUnitID = client.getHabbo().getRoomUnit().getId();
                            room.updateTile(gatePosition);
                            room.sendComposer(new ItemIntStateComposer(InteractionOneWayGate.this.getId(), 1).compose());
                            client.getHabbo().getRoomUnit().setGoalLocation(room.getLayout().getTileInFront(room.getLayout().getTile(InteractionOneWayGate.this.getX(), InteractionOneWayGate.this.getY()), InteractionOneWayGate.this.getRotation() + 4));
                        }
                    });
                }
            }
        }
    }

    private void refresh(Room room)
    {
        this.setExtradata("0");
        this.roomUnitID = -1;
        room.sendComposer(new ItemIntStateComposer(this.getId(), 0).compose());
        room.updateTile(room.getLayout().getTile(this.getX(), this.getY()));
    }
    @Override
    public void onPickUp(Room room)
    {
        this.setExtradata("0");
        this.refresh(room);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);
        this.refresh(room);
    }

    @Override
    public void onPlace(Room room)
    {
        super.onPlace(room);
        this.refresh(room);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation)
    {
        super.onMove(room, oldLocation, newLocation);
        this.refresh(room);
    }
}
