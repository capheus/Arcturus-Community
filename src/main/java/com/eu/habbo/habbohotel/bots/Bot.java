package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.plugin.events.bots.BotChatEvent;
import com.eu.habbo.plugin.events.bots.BotShoutEvent;
import com.eu.habbo.plugin.events.bots.BotTalkEvent;
import com.eu.habbo.plugin.events.bots.BotWhisperEvent;
import com.eu.habbo.threading.runnables.BotFollowHabbo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Bot implements Runnable
{

    private int id;


    private String name;


    private String motto;


    private String figure;


    private HabboGender gender;


    private int ownerId;


    private String ownerName;


    private Room room;


    private RoomUnit roomUnit;


    private boolean chatAuto;


    private boolean chatRandom;


    private short chatDelay;


    private int chatTimeOut;


    private final ArrayList<String> chatLines;


    private short lastChatIndex;


    private String type;


    private int effect;


    private boolean needsUpdate;


    private int followingHabboId;

    public Bot(int id, String name, String motto, String figure, HabboGender gender, int ownerId, String ownerName)
    {
        this.id = id;
        this.name = name;
        this.motto = motto;
        this.figure = figure;
        this.gender = gender;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.chatAuto = false;
        this.chatRandom = false;
        this.chatDelay = 1000;
        this.chatLines = new ArrayList<String>();
        this.type = "generic_bot";
        this.room = null;
    }

    public Bot(ResultSet set) throws SQLException
    {
        this.id             = set.getInt("id");
        this.name           = set.getString("name");
        this.motto          = set.getString("motto");
        this.figure         = set.getString("figure");
        this.gender         = HabboGender.valueOf(set.getString("gender"));
        this.ownerId        = set.getInt("user_id");
        this.ownerName      = set.getString("owner_name");
        this.chatAuto       = set.getString("chat_auto").equals("1");
        this.chatRandom     = set.getString("chat_random").equals("1");
        this.chatDelay      = set.getShort("chat_delay");
        this.chatLines      = new ArrayList<String>(Arrays.asList(set.getString("chat_lines").split("\r")));
        this.type           = set.getString("type");
        this.effect         = set.getInt("effect");
        this.room           = null;
        this.roomUnit       = null;
        this.chatTimeOut    = Emulator.getIntUnixTimestamp() + this.chatDelay;
        this.needsUpdate    = false;
        this.lastChatIndex  = 0;
    }

    public Bot(Bot bot)
    {
        this.name           = bot.getName();
        this.motto          = bot.getMotto();
        this.figure         = bot.getFigure();
        this.gender         = bot.getGender();
        this.ownerId        = bot.getOwnerId();
        this.ownerName      = bot.getOwnerName();
        this.chatAuto       = true;
        this.chatRandom     = false;
        this.chatDelay      = 10;
        this.chatTimeOut    = Emulator.getIntUnixTimestamp() + this.chatDelay;
        this.chatLines      = new ArrayList<String>(Arrays.asList(new String[] {"Default Message :D"}));
        this.type           = bot.getType();
        this.effect         = bot.getEffect();
        this.room           = null;
        this.roomUnit       = null;
        this.lastChatIndex  = 0;

        this.needsUpdate = false;
    }


    public void needsUpdate(boolean needsUpdate)
    {
        this.needsUpdate = needsUpdate;
    }


    public boolean needsUpdate()
    {
        return this.needsUpdate;
    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE bots SET name = ?, motto = ?, figure = ?, gender = ?, user_id = ?, room_id = ?, x = ?, y = ?, z = ?, rot = ?, dance = ?, freeroam = ?, chat_lines = ?, chat_auto = ?, chat_random = ?, chat_delay = ? WHERE id = ?"))
            {
                statement.setString(1, this.name);
                statement.setString(2, this.motto);
                statement.setString(3, this.figure);
                statement.setString(4, this.gender.toString());
                statement.setInt(5, this.ownerId);
                statement.setInt(6, this.room == null ? 0 : this.room.getId());
                statement.setInt(7, this.roomUnit == null ? 0 : this.roomUnit.getX());
                statement.setInt(8, this.roomUnit == null ? 0 : this.roomUnit.getY());
                statement.setDouble(9, this.roomUnit == null ? 0 : this.roomUnit.getZ());
                statement.setInt(10, this.roomUnit == null ? 0 : this.roomUnit.getBodyRotation().getValue());
                statement.setInt(11, this.roomUnit == null ? 0 : this.roomUnit.getDanceType().getType());
                statement.setString(12, this.roomUnit == null ? "0" : this.roomUnit.canWalk() ? "1" : "0");
                String text = "";
                for(String s : this.chatLines)
                {
                    text += s + "\r";
                }
                statement.setString(13, text);
                statement.setString(14, this.chatAuto ? "1" : "0");
                statement.setString(15, this.chatRandom ? "1" : "0");
                statement.setInt(16, this.chatDelay);
                statement.setInt(17, this.id);
                statement.execute();
                this.needsUpdate = false;
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }


    public void cycle(boolean canWalk)
    {
        if(this.roomUnit != null)
        {
            if(canWalk && this.getRoomUnit().canWalk())
            {
                if (!this.roomUnit.isWalking())
                {
                    if (this.roomUnit.getWalkTimeOut() < Emulator.getIntUnixTimestamp() && this.followingHabboId == 0)
                    {
                        this.roomUnit.setGoalLocation(this.room.getRandomWalkableTile());
                        int timeOut = Emulator.getRandom().nextInt(20) * 2;
                        this.roomUnit.setWalkTimeOut((timeOut < 10 ? 5 : timeOut) + Emulator.getIntUnixTimestamp());
                    }
                } else
                {
                    for (RoomTile t : this.room.getLayout().getTilesAround(this.room.getLayout().getTile(this.getRoomUnit().getX(), this.getRoomUnit().getY())))
                    {
                        WiredHandler.handle(WiredTriggerType.BOT_REACHED_STF, this.roomUnit, this.room, room.getItemsAt(t).toArray());
                    }
                }
            }

            if(!this.chatLines.isEmpty() && this.chatTimeOut <= Emulator.getIntUnixTimestamp() && this.chatAuto)
            {
                if(this.room != null)
                {
                    this.lastChatIndex = (this.chatRandom ? (short)Emulator.getRandom().nextInt(this.chatLines.size()) : (this.lastChatIndex == (this.chatLines.size() - 1) ? 0 : this.lastChatIndex++));
                    this.talk(this.chatLines.get(this.lastChatIndex)
                            .replace("%owner%", this.room.getOwnerName())
                            .replace("%item_count%", this.room.itemCount() + "")
                            .replace("%name%", this.name)
                            .replace("%roomname%", this.room.getName())
                            .replace("%user_count%", this.room.getUserCount() + ""));
                    this.chatTimeOut = Emulator.getIntUnixTimestamp() + this.chatDelay;
                }
            }
        }
    }


    public void talk(String message)
    {
        if(this.room != null)
        {
            BotChatEvent event = new BotTalkEvent(this, message);
            if(Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.room.botChat(new RoomUserTalkComposer(new RoomChatMessage(event.message, this.roomUnit, RoomChatMessageBubbles.BOT)).compose());
        }
    }


    public void shout(String message)
    {
        if(this.room != null)
        {
            BotChatEvent event = new BotShoutEvent(this, message);
            if(Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.room.botChat(new RoomUserShoutComposer(new RoomChatMessage(event.message, this.roomUnit, RoomChatMessageBubbles.BOT)).compose());
        }
    }


    public void whisper(String message, Habbo habbo)
    {
        if(this.room != null && habbo != null)
        {
            BotWhisperEvent event = new BotWhisperEvent(this, message, habbo);
            if(Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            event.target.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(event.message, this.roomUnit, RoomChatMessageBubbles.BOT)));
        }
    }


    public void onPlace(Habbo habbo, Room room)
    {

    }


    public void onPickUp(Habbo habbo, Room room)
    {

    }


    public void onUserSay(final RoomChatMessage message)
    {

    }


    public int getId()
    {
        return this.id;
    }


    public void setId(int id)
    {
        this.id = id;
    }


    public String getName()
    {
        return this.name;
    }


    public void setName(String name)
    {
        this.name        = name;
        this.needsUpdate = true;

        //if(this.room != null)
            //this.room.sendComposer(new ChangeNameUpdatedComposer(this.getRoomUnit(), this.getName()).compose());
    }


    public String getMotto()
    {
        return this.motto;
    }


    public void setMotto(String motto)
    {
        this.motto       = motto;
        this.needsUpdate = true;
    }


    public String getFigure()
    {
        return this.figure;
    }


    public void setFigure(String figure)
    {
        this.figure      = figure;
        this.needsUpdate = true;

        if(this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }


    public HabboGender getGender()
    {
        return this.gender;
    }


    public void setGender(HabboGender gender)
    {
        this.gender      = gender;
        this.needsUpdate = true;

        if(this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }


    public int getOwnerId()
    {
        return this.ownerId;
    }


    public void setOwnerId(int ownerId)
    {
        this.ownerId     = ownerId;
        this.needsUpdate = true;

        if(this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }


    public String getOwnerName()
    {
        return this.ownerName;
    }


    public void setOwnerName(String ownerName)
    {
        this.ownerName   = ownerName;
        this.needsUpdate = true;

        if(this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }


    public Room getRoom()
    {
        return this.room;
    }


    public void setRoom(Room room)
    {
        this.room = room;
    }


    public RoomUnit getRoomUnit()
    {
        return this.roomUnit;
    }


    public void setRoomUnit(RoomUnit roomUnit)
    {
        this.roomUnit = roomUnit;
    }


    public boolean isChatAuto()
    {
        return this.chatAuto;
    }


    public void setChatAuto(boolean chatAuto)
    {
        this.chatAuto    = chatAuto;
        this.needsUpdate = true;
    }


    public boolean isChatRandom()
    {
        return this.chatRandom;
    }


    public void setChatRandom(boolean chatRandom)
    {
        this.chatRandom  = chatRandom;
        this.needsUpdate = true;
    }


    public int getChatDelay()
    {
        return this.chatDelay;
    }


    public void setChatDelay(short chatDelay)
    {
        this.chatDelay   = chatDelay;
        this.needsUpdate = true;
    }


    public void clearChat()
    {
        synchronized (this.chatLines)
        {
            this.chatLines.clear();
            this.needsUpdate = true;
        }
    }


    public String getType()
    {
        return this.type;
    }


    public int getEffect()
    {
        return this.effect;
    }


    public void setEffect(int effect)
    {
        this.effect      = effect;
        this.needsUpdate = true;

        if (this.roomUnit != null)
        {
            this.roomUnit.setEffectId(this.effect);

            if (this.room != null)
            {
                this.room.sendComposer(new RoomUserEffectComposer(this.roomUnit).compose());
            }
        }
    }


    public void addChatLines(ArrayList<String> chatLines)
    {
        synchronized (this.chatLines)
        {
            this.chatLines.addAll(chatLines);
            this.needsUpdate = true;
        }
    }


    public void addChatLine(String chatLine)
    {
        synchronized (this.chatLines)
        {
            this.chatLines.add(chatLine);
            this.needsUpdate = true;
        }
    }


    public ArrayList<String> getChatLines()
    {
        return this.chatLines;
    }


    public int getFollowingHabboId()
    {
        return this.followingHabboId;
    }


    public void startFollowingHabbo(Habbo habbo)
    {
        this.followingHabboId = habbo.getHabboInfo().getId();

        Emulator.getThreading().run(new BotFollowHabbo(this, habbo, habbo.getHabboInfo().getCurrentRoom()));
    }

    public void stopFollowingHabbo()
    {
        this.followingHabboId = 0;
    }


    public static void initialise()
    {

    }


    public static void dispose()
    {

    }
}
