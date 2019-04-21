package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectGiveAchievement extends WiredEffectGiveBadge
{
    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;

    public static final String DEFAULT_CONTENT = "ach_achievement:points <- Points are optional";
    private String achievement = DEFAULT_CONTENT;

    public WiredEffectGiveAchievement(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectGiveAchievement(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        message.appendBoolean(true);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.achievement + "");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(type.code);
        message.appendInt(this.getDelay());

        if (this.requiresTriggeringUser())
        {
            List<Integer> invalidTriggers = new ArrayList<>();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>()
            {
                @Override
                public boolean execute(InteractionWiredTrigger object)
                {
                    if (!object.isTriggeredByRoomUnit())
                    {
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    }
                    return true;
                }
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers)
            {
                message.appendInt(i);
            }
        } else
        {
            message.appendInt(0);
        }
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        packet.readInt();

        try
        {
            this.achievement = packet.readString();

            int points = 1;
            String a = achievement;
            if (a.contains(":"))
            {
                a = achievement.split(":")[0];
                try
                {
                    points = Integer.valueOf(achievement.split(":")[1]);
                } catch (Exception e)
                {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("hotel.wired.giveachievement.invalid.points"), RoomChatMessageBubbles.WIRED);
                }
            }

            Achievement ach = Emulator.getGameEnvironment().getAchievementManager().getAchievement(a);

            if (ach == null)
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("hotel.wired.giveachievement.invalid.achievement").replace("%achievement%", a), RoomChatMessageBubbles.WIRED);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        packet.readInt();
        this.setDelay(packet.readInt());

        return true;
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        if (!this.achievement.equals(DEFAULT_CONTENT))
        {
            Habbo habbo = room.getHabbo(roomUnit);

            if (habbo != null)
            {
                int points = 1;
                String a = achievement;
                if (a.contains(":"))
                {
                    a = achievement.split(":")[0];
                    try
                    {
                        points = Integer.valueOf(achievement.split(":")[1]);
                    } catch (Exception e)
                    {
                        return false;
                    }
                }

                Achievement ach = Emulator.getGameEnvironment().getAchievementManager().getAchievement(a);

                if (ach != null)
                {
                    AchievementManager.progressAchievement(habbo, ach, points);
                }
            }
        }
        return true;
    }

    @Override
    public String getWiredData()
    {
        return this.getDelay() + "\t" + this.achievement;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String wireData = set.getString("wired_data");
        String[] data = wireData.split("\t");
        this.achievement = DEFAULT_CONTENT;

        if (data.length >= 2)
        {
            super.setDelay(Integer.valueOf(data[0]));

            this.achievement = data[1];
        }
    }

    @Override
    public void onPickUp()
    {
        this.achievement = DEFAULT_CONTENT;
        this.setDelay(0);
    }

    @Override
    public boolean requiresTriggeringUser()
    {
        return true;
    }
}