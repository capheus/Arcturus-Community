package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.IEventTriggers;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.math3.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class HabboItem implements Runnable, IEventTriggers
{
    private int id;
    private int userId;
    private int roomId;
    private Item baseItem;
    private String wallPosition;
    private short x;
    private short y;
    private double z;
    private int rotation;
    private String extradata;
    private int limitedStack;
    private int limitedSells;
    private boolean needsUpdate = false;
    private boolean needsDelete = false;

    public HabboItem(ResultSet set, Item baseItem) throws SQLException
    {
        this.id = set.getInt("id");
        this.userId = set.getInt("user_id");
        this.roomId = set.getInt("room_id");
        this.baseItem = baseItem;
        this.wallPosition = set.getString("wall_pos");
        this.x = set.getShort("x");
        this.y = set.getShort("y");
        this.z = set.getDouble("z");
        this.rotation = set.getInt("rot");
        this.extradata = set.getString("extra_data");

        String ltdData = set.getString("limited_data");
        if (!ltdData.isEmpty())
        {
            this.limitedStack = Integer.parseInt(set.getString("limited_data").split(":")[0]);
            this.limitedSells = Integer.parseInt(set.getString("limited_data").split(":")[1]);
        }
    }

    public HabboItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        this.id = id;
        this.userId = userId;
        this.roomId = 0;
        this.baseItem = item;
        this.wallPosition = "";
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.rotation = 0;
        this.extradata = extradata;
        this.limitedSells = limitedSells;
        this.limitedStack = limitedStack;
    }

    public void serializeFloorData(ServerMessage serverMessage)
    {
        try
        {
            serverMessage.appendInt(this.getId());
            serverMessage.appendInt(this.baseItem.getSpriteId());
            serverMessage.appendInt(this.x);
            serverMessage.appendInt(this.y);
            serverMessage.appendInt(this.getRotation());
            serverMessage.appendString(Double.toString(this.z));

            serverMessage.appendString((this.getBaseItem().getInteractionType().getType() == InteractionTrophy.class || this.getBaseItem().getInteractionType().getType() == InteractionCrackable.class || this.getBaseItem().getName().toLowerCase().equals("gnome_box")) ? "1.0" : ((this.getBaseItem().allowWalk() || this.getBaseItem().allowSit() && this.roomId != 0) ? Item.getCurrentHeight(this) + "" : ""));
            //serverMessage.appendString( ? "1.0" : ((this.getBaseItem().allowWalk() || this.getBaseItem().allowSit() && this.roomId != 0) ? Item.getCurrentHeight(this) : ""));

        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public void serializeExtradata(ServerMessage serverMessage)
    {
        if(this.isLimited())
        {
            serverMessage.appendInt(this.getLimitedSells());
            serverMessage.appendInt(this.getLimitedStack());
        }
    }

    public void serializeWallData(ServerMessage serverMessage)
    {
        serverMessage.appendString(this.getId() + "");
        serverMessage.appendInt(this.baseItem.getSpriteId());
        serverMessage.appendString(this.wallPosition);

        if(this instanceof InteractionPostIt)
            serverMessage.appendString(this.extradata.split(" ")[0]);
        else
            serverMessage.appendString(this.extradata);
        serverMessage.appendInt(-1);
        serverMessage.appendInt(this.isUsable());
        serverMessage.appendInt(this.getUserId());
    }

    public int getId()
    {
        return this.id;
    }

    public int getUserId()
    {
        return this.userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public int getRoomId()
    {
        return this.roomId;
    }

    public void setRoomId(int roomId)
    {
        this.roomId = roomId;
    }

    public Item getBaseItem()
    {
        return this.baseItem;
    }

    public String getWallPosition()
    {
        return this.wallPosition;
    }

    public void setWallPosition(String wallPosition)
    {
        this.wallPosition = wallPosition;
    }

    public short getX()
    {
        return this.x;
    }

    public void setX(short x)
    {
        this.x = x;
    }

    public short getY()
    {
        return this.y;
    }

    public void setY(short y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return this.z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public int getRotation()
    {
        return this.rotation;
    }

    public void setRotation(int rotation)
    {
        this.rotation = (byte)(rotation % 8);
    }

    public String getExtradata()
    {
        return this.extradata;
    }

    public void setExtradata(String extradata)
    {
        this.extradata = extradata;
    }

    public boolean needsUpdate()
    {
        return this.needsUpdate;
    }

    public void needsUpdate(boolean value)
    {
        this.needsUpdate = value;
    }

    public void needsDelete(boolean value)
    {
        this.needsDelete = value;
    }

    public boolean isLimited()
    {
        return this.limitedStack > 0;
    }

    public int getLimitedStack()
    {
        return this.limitedStack;
    }

    public int getLimitedSells()
    {
        return this.limitedSells;
    }

    @Override
    public void run()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            if (this.needsDelete)
            {
                this.needsUpdate = false;
                this.needsDelete = false;

                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM items WHERE id = ?"))
                {
                    statement.setInt(1, this.getId());
                    statement.execute();
                }
            }
            else if (this.needsUpdate)
            {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE items SET user_id = ?, room_id = ?, wall_pos = ?, x = ?, y = ?, z = ?, rot = ?, extra_data = ?, limited_data = ? WHERE id = ?"))
                {
                    statement.setInt(1, this.userId);
                    statement.setInt(2, this.roomId);
                    statement.setString(3, this.wallPosition);
                    statement.setInt(4, this.x);
                    statement.setInt(5, this.y);
                    statement.setDouble(6, this.z);
                    statement.setInt(7, this.rotation);
                    statement.setString(8, this instanceof InteractionGuildGate ? "" : this.getDatabaseExtraData());
                    statement.setString(9, this.limitedStack + ":" + this.limitedSells);
                    statement.setInt(10, this.id);
                    statement.execute();
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                    Emulator.getLogging().logErrorLine("SQLException trying to save HabboItem: " + this.toString());
                }

                this.needsUpdate = false;
            }

        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public static RoomTile getSquareInFront(RoomLayout roomLayout, HabboItem item)
    {
        return roomLayout.getTileInFront(roomLayout.getTile(item.getX(), item.getY()), item.getRotation());
    }

    public abstract boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects);

    public abstract boolean isWalkable();

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(client != null && this.getBaseItem().getType() == FurnitureType.FLOOR)
        {
            if (objects != null && objects.length >= 2)
            {
                if (objects[1] instanceof WiredEffectType)
                {
                    return;
                }
            }


            WiredHandler.handle(WiredTriggerType.STATE_CHANGED, client.getHabbo().getRoomUnit(), room, new Object[]{this});
        }
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        if (objects != null && objects.length >= 1 && objects[0] instanceof InteractionWired)
            return;

        WiredHandler.handle(WiredTriggerType.WALKS_ON_FURNI, roomUnit, room, new Object[]{this});
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        WiredHandler.handle(WiredTriggerType.WALKS_OFF_FURNI, roomUnit, room, new Object[]{this});
    }

    public abstract void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception;


    public void onPlace(Room room)
    {
        //TODO: IMPORTANT: MAKE THIS GENERIC. (HOLES, ICE SKATE PATCHES, BLACK HOLE, BUNNY RUN FIELD, FOOTBALL FIELD)
        Achievement roomDecoAchievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoFurniCount");
        Habbo owner = room.getHabbo(this.getUserId());

        int furniCollecterProgress;
        if (owner == null)
        {
            furniCollecterProgress = AchievementManager.getAchievementProgressForHabbo(this.getUserId(), roomDecoAchievement);
        }
        else
        {
            furniCollecterProgress = owner.getHabboStats().getAchievementProgress(roomDecoAchievement);
        }

        int difference = room.getUserFurniCount(this.getUserId()) - furniCollecterProgress;
        if (difference > 0)
        {
            if (owner != null)
            {
                AchievementManager.progressAchievement(owner, roomDecoAchievement, difference);
            }
            else
            {
                AchievementManager.progressAchievement(this.getUserId(), roomDecoAchievement, difference);
            }
        }
    }

    public void onPickUp(Room room)
    {
        if(this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0)
        {
            for (Habbo habbo : room.getHabbosOnItem(this))
            {
                if (this.getBaseItem().getEffectM() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.M) && habbo.getRoomUnit().getEffectId() == this.getBaseItem().getEffectM())
                {
                    room.giveEffect(habbo, 0, -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.F) && habbo.getRoomUnit().getEffectId() == this.getBaseItem().getEffectF())
                {
                    room.giveEffect(habbo, 0, -1);
                }
            }

            for (Bot bot : room.getBotsAt(room.getLayout().getTile(this.getX(), this.getY())))
            {
                if (this.getBaseItem().getEffectM() > 0 && bot.getGender().equals(HabboGender.M) && bot.getRoomUnit().getEffectId() == this.getBaseItem().getEffectM())
                {
                    room.giveEffect(bot.getRoomUnit(), 0, -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && bot.getGender().equals(HabboGender.F) && bot.getRoomUnit().getEffectId() == this.getBaseItem().getEffectF())
                {
                    room.giveEffect(bot.getRoomUnit(), 0, -1);
                }
            }
        }
    }

    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation)
    {
        if(this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0)
        {
            List<Habbo> oldHabbos = new ArrayList<>();
            List<Habbo> newHabbos = new ArrayList<>();
            List<Bot> oldBots = new ArrayList<>();
            List<Bot> newBots = new ArrayList<>();

            for (RoomTile tile : room.getLayout().getTilesAt(oldLocation, this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()))
            {
                oldHabbos.addAll(room.getHabbosAt(tile));
                oldBots.addAll(room.getBotsAt(tile));
            }

            for (RoomTile tile : room.getLayout().getTilesAt(oldLocation, this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()))
            {
                newHabbos.addAll(room.getHabbosAt(tile));
                newBots.addAll(room.getBotsAt(tile));
            }

            oldHabbos.removeAll(newHabbos);
            oldBots.removeAll(newBots);

            for (Habbo habbo : oldHabbos)
            {
                if (this.getBaseItem().getEffectM() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.M) && habbo.getRoomUnit().getEffectId() == this.getBaseItem().getEffectM())
                {
                    room.giveEffect(habbo, 0, -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.F) && habbo.getRoomUnit().getEffectId() == this.getBaseItem().getEffectF())
                {
                    room.giveEffect(habbo, 0, -1);
                }
            }

            for (Habbo habbo : newHabbos)
            {
                if (this.getBaseItem().getEffectM() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.M) && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectM())
                {
                    room.giveEffect(habbo, this.getBaseItem().getEffectM(), -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && habbo.getHabboInfo().getGender().equals(HabboGender.F) && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectF())
                {
                    room.giveEffect(habbo, this.getBaseItem().getEffectF(), -1);
                }
            }

            for (Bot bot : oldBots)
            {
                if (this.getBaseItem().getEffectM() > 0 && bot.getGender().equals(HabboGender.M) && bot.getRoomUnit().getEffectId() == this.getBaseItem().getEffectM())
                {
                    room.giveEffect(bot.getRoomUnit(), 0, -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && bot.getGender().equals(HabboGender.F) && bot.getRoomUnit().getEffectId() == this.getBaseItem().getEffectF())
                {
                    room.giveEffect(bot.getRoomUnit(), 0, -1);
                }
            }

            for (Bot bot : newBots)
            {
                if (this.getBaseItem().getEffectM() > 0 && bot.getGender().equals(HabboGender.M) && bot.getRoomUnit().getEffectId() != this.getBaseItem().getEffectM())
                {
                    room.giveEffect(bot.getRoomUnit(), this.getBaseItem().getEffectM(), -1);
                }

                if (this.getBaseItem().getEffectF() > 0 && bot.getGender().equals(HabboGender.F) && bot.getRoomUnit().getEffectId() != this.getBaseItem().getEffectF())
                {
                    room.giveEffect(bot.getRoomUnit(), this.getBaseItem().getEffectF(), -1);
                }
            }
        }
    }

    public String getDatabaseExtraData()
    {
        return this.getExtradata();
    }

    @Override
    public String toString()
    {
        return "ID: " + this.id + ", BaseID: " + this.getBaseItem().getId() + ", X: " + this.x + ", Y: " + this.y + ", Z: " + this.z + ", Extradata: " + this.extradata;
    }

    public boolean allowWiredResetState()
    {
        return false;
    }

    public boolean isUsable()
    {
        return this.baseItem.getStateCount() > 1;
    }

    public boolean canStackAt(Room room, List<Pair<RoomTile, THashSet<HabboItem>>> itemsAtLocation)
    {
        return true;
    }
}
