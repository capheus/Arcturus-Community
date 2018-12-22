package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionSwitch extends InteractionDefault
{
    public InteractionSwitch(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionSwitch(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canToggle(Habbo habbo, Room room)
    {
        return super.canToggle(habbo, room) || RoomLayout.tilesAdjecent(room.getLayout().getTile(this.getX(), this.getY()), habbo.getRoomUnit().getCurrentLocation());
    }

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }

    @Override
    public boolean isUsable()
    {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if (!this.canToggle(client.getHabbo(), room))
        {
            RoomTile closestTile = null;
            for (RoomTile tile : room.getLayout().getTilesAround(room.getLayout().getTile(this.getX(), this.getY())))
            {
                if (tile.isWalkable() && (closestTile == null || closestTile.distance(client.getHabbo().getRoomUnit().getCurrentLocation()) > tile.distance(client.getHabbo().getRoomUnit().getCurrentLocation())))
                {
                    closestTile = client.getHabbo().getRoomUnit().getCurrentLocation();
                }
            }

            if (closestTile != null)
            {
                client.getHabbo().getRoomUnit().setGoalLocation(closestTile);
            }
        }

        super.onClick(client, room, objects);
    }
}
