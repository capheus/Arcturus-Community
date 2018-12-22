package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionWaterItem extends InteractionDefault
{
    public InteractionWaterItem(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionWaterItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onPlace(Room room)
    {
        this.update();
    }

    @Override
    public void onPickUp(Room room)
    {
        this.setExtradata("0");
        this.needsUpdate(true);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation)
    {
        this.update();
    }

    public void update()
    {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room == null)
            return;

        Rectangle rectangle = RoomLayout.getRectangle(this.getX(), this.getY(), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation());

        boolean foundWater = true;
        for(short x = (short)rectangle.x; x < rectangle.getWidth() + rectangle.x && foundWater; x++)
        {
            for(short y = (short)rectangle.y; y < rectangle.getHeight() + rectangle.y && foundWater; y++)
            {
                boolean tile = false;
                THashSet<HabboItem> items = room.getItemsAt(room.getLayout().getTile(x, y));

                for(HabboItem item : items)
                {
                    if (item instanceof InteractionWater)
                    {
                        tile = true;
                        break;
                    }
                }

                if (!tile)
                {
                    foundWater = false;
                }
            }
        }

        if (foundWater)
        {
            this.setExtradata("1");
            this.needsUpdate(true);
            room.updateItem(this);
            return;
        }

        this.setExtradata("0");
        this.needsUpdate(true);
        room.updateItem(this);
    }

    @Override
    public boolean allowWiredResetState()
    {
        return false;
    }

    @Override
    public boolean canToggle(Habbo habbo, Room room)
    {
        return false;
    }
}
