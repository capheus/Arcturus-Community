package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPressurePlate extends HabboItem
{
    public InteractionPressurePlate(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionPressurePlate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        Emulator.getThreading().run(new Runnable()
        {
            @Override
            public void run()
            {
                updateState(room);
            }
        }, 100);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);

        Emulator.getThreading().run(new Runnable()
        {
            @Override
            public void run()
            {
                updateState(room);
            }
        }, 100);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation)
    {
        super.onMove(room, oldLocation, newLocation);

        updateState(room);
    }

    public void updateState(Room room)
    {
        boolean occupied = false;

        for (RoomTile tile : room.getLayout().getTilesAt(room.getLayout().getTile(this.getX(), this.getY()), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()))
        {
            boolean hasHabbos = room.hasHabbosAt(tile.x, tile.y);
            if (!hasHabbos && this.requiresAllTilesOccupied())
            {
                occupied = false;
                break;
            }

            if (hasHabbos)
            {
                occupied = true;
            }
        }

        this.setExtradata(occupied ? "1" : "0");
        room.updateItem(this);
    }

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }

    public boolean requiresAllTilesOccupied()
    {
        return false;
    }

}
