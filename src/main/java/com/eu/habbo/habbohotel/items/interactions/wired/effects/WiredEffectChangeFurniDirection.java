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
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class WiredEffectChangeFurniDirection extends InteractionWiredEffect
{
    public static final int ACTION_WAIT = 0;
    public static final  int ACTION_TURN_RIGHT_45 = 1;
    public static final  int ACTION_TURN_RIGHT_90 = 2;
    public static final  int ACTION_TURN_LEFT_45 = 3;
    public static final  int ACTION_TURN_LEFT_90 = 4;
    public static final  int ACTION_TURN_BACK = 5;
    public static final  int ACTION_TURN_RANDOM = 6;

    public static final WiredEffectType type = WiredEffectType.MOVE_DIRECTION;

    private final THashMap<HabboItem, RoomUserRotation> items = new THashMap<>(0);
    private RoomUserRotation startRotation = RoomUserRotation.NORTH;
    private int rotateAction = 0;
    public WiredEffectChangeFurniDirection(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectChangeFurniDirection(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        THashSet<HabboItem> items = new THashSet<>();

        for (HabboItem item : this.items.keySet())
        {
            if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for (HabboItem item : items)
        {
            this.items.remove(item);
        }

        if (this.items.isEmpty()) return false;
        for (Map.Entry<HabboItem, RoomUserRotation> entry : this.items.entrySet())
        {
            RoomUserRotation currentRotation = entry.getValue();
            RoomTile targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(entry.getKey().getX(), entry.getKey().getY()), entry.getValue().getValue());

            int count = 1;
            while ((targetTile == null || !targetTile.getAllowStack() || targetTile.state == RoomTileState.INVALID) && count < 8)
            {
                entry.setValue(this.nextRotation(entry.getValue()));
                targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(entry.getKey().getX(), entry.getKey().getY()), entry.getValue().getValue());
                count++;
            }

            if (targetTile != null && targetTile.state == RoomTileState.OPEN)
            {
                boolean hasHabbos = false;
                for (Habbo habbo : room.getHabbosAt(targetTile))
                {
                    hasHabbos = true;
                    Emulator.getThreading().run(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            WiredHandler.handle(WiredTriggerType.COLLISION, habbo.getRoomUnit(), room, new Object[]{entry.getKey()});
                        }
                    });
                }

                if (!hasHabbos)
                {
                    THashSet<RoomTile> refreshTiles = room.getLayout().getTilesAt(room.getLayout().getTile(entry.getKey().getX(), entry.getKey().getY()), entry.getKey().getBaseItem().getWidth(), entry.getKey().getBaseItem().getLength(), entry.getKey().getRotation());
                    room.sendComposer(new FloorItemOnRollerComposer(entry.getKey(), null, targetTile, targetTile.getStackHeight() - entry.getKey().getZ(), room).compose());
                    room.getLayout().getTilesAt(room.getLayout().getTile(entry.getKey().getX(), entry.getKey().getY()), entry.getKey().getBaseItem().getWidth(), entry.getKey().getBaseItem().getLength(), entry.getKey().getRotation());
                    room.updateTiles(refreshTiles);
                }
            }
        }

        return false;
    }

    @Override
    public String getWiredData()
    {
        StringBuilder data = new StringBuilder(this.startRotation.getValue() + "\t" + this.rotateAction + "\t" + this.items.size());

        for (Map.Entry<HabboItem, RoomUserRotation> entry : this.items.entrySet())
        {
            data.append("\t").append(entry.getKey().getId()).append(":").append(entry.getValue().getValue());
        }

        return data.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split("\t");

        if (data.length >= 3)
        {
            this.startRotation = RoomUserRotation.fromValue(Integer.valueOf(data[0]));
            this.rotateAction = Integer.valueOf(data[1]);

            int itemCount = Integer.valueOf(data[2]);

            if (itemCount > 0)
            {
                for (int i = 3; i < data.length; i++)
                {
                    String[] subData = data[i].split(":");

                    if (subData.length == 2)
                    {
                        HabboItem item = room.getHabboItem(Integer.valueOf(subData[0]));

                        if (item != null)
                        {
                            this.items.put(item, RoomUserRotation.fromValue(Integer.valueOf(subData[1])));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.setDelay(0);
        this.items.clear();
        this.rotateAction = 0;
        this.startRotation = RoomUserRotation.NORTH;
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (Map.Entry<HabboItem, RoomUserRotation> item : this.items.entrySet())
        {
            message.appendInt(item.getKey().getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(2);
        message.appendInt(this.startRotation.getValue());
        message.appendInt(this.rotateAction);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        this.items.clear();
        packet.readInt();
        this.startRotation = RoomUserRotation.fromValue(packet.readInt());
        this.rotateAction = packet.readInt();
        packet.readString();

        int furniCount = packet.readInt();
        for (int i = 0; i < furniCount; i++)
        {
            HabboItem item = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(packet.readInt());

            if (item != null)
            {
                this.items.put(item, this.startRotation);
            }
        }
        return true;
    }

    private RoomUserRotation nextRotation(RoomUserRotation currentRotation)
    {
        switch (this.rotateAction)
        {
            case ACTION_TURN_BACK:
                return RoomUserRotation.fromValue(currentRotation.getValue() + 4);

            case ACTION_TURN_LEFT_45:
                return RoomUserRotation.counterClockwise(currentRotation);
            case ACTION_TURN_LEFT_90:
                return RoomUserRotation.counterClockwise(RoomUserRotation.counterClockwise(currentRotation));
            case ACTION_TURN_RIGHT_45:
                return RoomUserRotation.clockwise(currentRotation);
            case ACTION_TURN_RIGHT_90:
                return RoomUserRotation.clockwise(RoomUserRotation.clockwise(currentRotation));
            case ACTION_TURN_RANDOM:
                return RoomUserRotation.fromValue(Emulator.getRandom().nextInt(8));
            case ACTION_WAIT:
            default:
                return currentRotation;


        }
    }

    @Override
    protected long requiredCooldown()
    {
        return 495;
    }
}
