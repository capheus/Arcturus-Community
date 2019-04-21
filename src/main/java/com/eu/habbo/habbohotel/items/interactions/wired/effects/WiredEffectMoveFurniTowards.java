package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.util.pathfinding.Rotation;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMoveFurniTowards extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.CHASE;

    private THashSet<HabboItem> items;

    public WiredEffectMoveFurniTowards(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.items = new THashSet<>();
    }

    public WiredEffectMoveFurniTowards(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        THashSet<HabboItem> items = new THashSet<>();

        for(HabboItem item : this.items)
        {
            if (item.getRoomId() == 0)
                items.add(item);
        }

        this.items.removeAll(items);

        for(HabboItem item : this.items)
        {
            RoomTile furniLocation = room.getLayout().getTile(item.getX(), item.getY());

            boolean collided = false;
            double shortest = 3 + Math.max(item.getBaseItem().getWidth(), item.getBaseItem().getLength());
            Habbo target = null;
            THashSet<RoomTile> tiles = room.getLayout().getTilesAt(furniLocation, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            for (Habbo habbo : room.getHabbos())
            {
                RoomTile currentLocation = habbo.getRoomUnit().getCurrentLocation();
                for (RoomTile t : tiles)
                {
                    if (currentLocation.x == t.x || currentLocation.y == t.y)
                    {
                        double distance = t.distance(habbo.getRoomUnit().getCurrentLocation());
                        if (distance == 1)
                        {
                            Emulator.getThreading().run(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    WiredHandler.handle(WiredTriggerType.COLLISION, habbo.getRoomUnit(), room, new Object[]{item});
                                }
                            });

                            collided = true;
                            break;
                        }

                        boolean valid = true;
                        for (RoomTile tile : room.getLayout().getTilesInFront(habbo.getRoomUnit().getCurrentLocation(), Rotation.Calculate(currentLocation.x, currentLocation.y, t.x, t.y), (int) Math.ceil(distance)))
                        {
                            if (tile.state == RoomTileState.INVALID)
                            {
                                valid = false;
                                break;
                            }

                            if (tile == t)
                            {
                                //Do not look further in  case of rounding error.
                                break;
                            }
                        }

                        if (valid && distance <= shortest)
                        {
                            target = habbo;
                            shortest = distance;
                        }
                    }
                }
            }

            if (collided)
            {
                break;
            }

            int x = 0;
            int y = 0;
            if(target != null)
            {
                for (RoomTile tile : tiles)
                {
                    if (target.getRoomUnit().getX() == tile.x)
                    {
                        if (tile.y < target.getRoomUnit().getY())
                            y++;
                        else
                            y--;

                        break;
                    } else if (target.getRoomUnit().getY() == tile.y)
                    {
                        if (tile.x < target.getRoomUnit().getX())
                            x++;
                        else
                            x--;
                        break;
                    } else if (target.getRoomUnit().getX() - tile.x > target.getRoomUnit().getY() - tile.y)
                    {
                        if (target.getRoomUnit().getX() - tile.x > 0)
                            x++;
                        else
                            x--;
                        break;
                    } else
                    {
                        if (target.getRoomUnit().getY() - tile.y > 0)
                            y++;
                        else
                            y--;
                        break;
                    }
                }
            }
            else
            {
                switch (Emulator.getRandom().nextInt(4))
                {
                    case 0: x--; break;
                    case 1: x++; break;
                    case 2: y--; break;
                    case 3: y++; break;
                }
            }

            RoomTile newTile = room.getLayout().getTile((short) (item.getX() + x), (short) (item.getY() + y));

            if (newTile != null && ((newTile.state == RoomTileState.OPEN && newTile.isWalkable()) || newTile.state == RoomTileState.BLOCKED && room.getTopItemAt(newTile.x, newTile.y) == item) && room.furnitureFitsAt(newTile, item, item.getRotation()) == FurnitureMovementError.NONE)
            {
                if (room.getLayout().tileExists(newTile.x, newTile.y))
                {
                    room.slideFurniTo(item, newTile, item.getRotation());
                }
            }
        }

        return true;
    }

    @Override
    public String getWiredData()
    {
        StringBuilder wiredData = new StringBuilder(this.getDelay() + "\t");

        if(this.items != null && !this.items.isEmpty())
        {
            for (HabboItem item : this.items)
            {
                wiredData.append(item.getId()).append(";");
            }
        }

        return wiredData.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.items = new THashSet<>();
        String[] wiredData = set.getString("wired_data").split("\t");

        if (wiredData.length >= 1)
        {
            this.setDelay(Integer.valueOf(wiredData[0]));
        }
        if (wiredData.length == 2)
        {
            if (wiredData[1].contains(";"))
            {
                for (String s : wiredData[1].split(";"))
                {
                    HabboItem item = room.getHabboItem(Integer.valueOf(s));

                    if (item != null)
                        this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        THashSet<HabboItem> items = new THashSet<>();

        for(HabboItem item : this.items)
        {
            if(item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for(HabboItem item : this.items)
            message.appendInt(item.getId());

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        packet.readInt();
        packet.readString();

        this.items.clear();

        int count = packet.readInt();

        for(int i = 0; i < count; i++)
        {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt()));
        }

        this.setDelay(packet.readInt());

        return true;
    }

    @Override
    protected long requiredCooldown()
    {
        return 495;
    }
}
