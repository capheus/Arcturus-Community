package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.generic.alerts.BotErrorComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.inventory.AddBotComposer;
import com.eu.habbo.messages.outgoing.inventory.RemoveBotComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUsersComposer;
import com.eu.habbo.plugin.events.bots.BotPickUpEvent;
import com.eu.habbo.plugin.events.bots.BotPlacedEvent;
import gnu.trove.map.hash.THashMap;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.Map;

public class BotManager
{

    public static int MINIMUM_CHAT_SPEED = 7;


    public static int MAXIMUM_CHAT_SPEED = 604800;


    public static int MAXIMUM_CHAT_LENGTH = 120;


    public static int MAXIMUM_NAME_LENGTH = 15;

    final private static THashMap<String, Class<? extends Bot>> botDefenitions = new THashMap<>();


    public BotManager() throws Exception
    {
        long millis = System.currentTimeMillis();

        addBotDefinition("generic", Bot.class);
        addBotDefinition("bartender", ButlerBot.class);
        addBotDefinition("visitor_log", VisitorBot.class);

        this.reload();

        Emulator.getLogging().logStart("Bot Manager -> Loaded! ("+(System.currentTimeMillis() - millis)+" MS)");
    }


    public boolean reload()
    {
        for(Map.Entry<String, Class<? extends Bot>> set : botDefenitions.entrySet())
        {
            try
            {
                Method m = set.getValue().getMethod("initialise");
                m.setAccessible(true);
                m.invoke(null);
            }
            catch (NoSuchMethodException e)
            {
                Emulator.getLogging().logStart("Bot Manager -> Failed to execute initialise method upon bot type '" + set.getKey() + "'. No Such Method!");
                return false;
            }
            catch (Exception e)
            {
                Emulator.getLogging().logStart("Bot Manager -> Failed to execute initialise method upon bot type '" + set.getKey() + "'. Error: " + e.getMessage());
                return false;
            }
        }

        return true;
    }


    public Bot createBot(THashMap<String, String> data, String type)
    {
        Bot bot = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO bots (user_id, room_id, name, motto, figure, gender, type) VALUES (0, 0, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, data.get("name"));
            statement.setString(2, data.get("motto"));
            statement.setString(3, data.get("figure"));
            statement.setString(4, data.get("gender").toUpperCase());
            statement.setString(5, type);
            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys())
            {
                if (set.next())
                {
                    try (PreparedStatement stmt = connection.prepareStatement("SELECT users.username AS owner_name, bots.* FROM bots LEFT JOIN users ON bots.user_id = users.id WHERE bots.id = ? LIMIT 1"))
                    {
                        stmt.setInt(1, set.getInt(1));
                        try (ResultSet resultSet = stmt.executeQuery())
                        {
                            if (resultSet.next())
                            {
                                bot = this.loadBot(resultSet);
                            }
                        }
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return bot;
    }


    public void placeBot(Bot bot, Habbo habbo, Room room, RoomTile location)
    {
        BotPlacedEvent event = new BotPlacedEvent(bot, location, habbo);
        Emulator.getPluginManager().fireEvent(event);

        if(event.isCancelled())
            return;

        if(room != null && bot != null && habbo != null)
        {
            if (room.getOwnerId() == habbo.getHabboInfo().getId() || habbo.hasPermission(Permission.ACC_ANYROOMOWNER) || habbo.hasPermission("acc_placefurni"))
            {
                if (room.getCurrentBots().size() >= Room.MAXIMUM_BOTS && !habbo.hasPermission("acc_unlimited_bots"))
                {
                    habbo.getClient().sendResponse(new BotErrorComposer(BotErrorComposer.ROOM_ERROR_MAX_BOTS));
                    return;
                }

                if (!room.hasHabbosAt(location.x, location.y) && !location.isWalkable() && location.state != RoomTileState.SIT)
                    return;

                RoomUnit roomUnit = new RoomUnit();
                roomUnit.setRotation(RoomUserRotation.SOUTH);
                roomUnit.setLocation(location);
                HabboItem topItem = room.getTopItemAt(location.x, location.y);
                if (topItem != null)
                {
                    roomUnit.setZ(topItem.getZ() + Item.getCurrentHeight(topItem));
                }
                roomUnit.setPreviousLocationZ(roomUnit.getZ());
                roomUnit.setPathFinderRoom(room);
                roomUnit.setRoomUnitType(RoomUnitType.BOT);
                roomUnit.setCanWalk(room.isAllowBotsWalk());
                bot.setRoomUnit(roomUnit);
                bot.setRoom(room);
                bot.needsUpdate(true);
                room.addBot(bot);
                Emulator.getThreading().run(bot);
                room.sendComposer(new RoomUsersComposer(bot).compose());
                habbo.getInventory().getBotsComponent().removeBot(bot);
                habbo.getClient().sendResponse(new RemoveBotComposer(bot));
                bot.onPlace(habbo, room);

                if (topItem != null)
                {
                    try
                    {
                        topItem.onWalkOn(bot.getRoomUnit(), room, null);
                    } catch (Exception e)
                    {
                        Emulator.getLogging().logErrorLine(e);
                    }
                }
                bot.cycle(false);
            }
            else
            {
                habbo.getClient().sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNITURE_PLACEMENT_ERROR.key, FurnitureMovementError.NO_RIGHTS.errorCode));
            }
        }
    }


    public void pickUpBot(int botId, Habbo habbo)
    {
        if(habbo.getHabboInfo().getCurrentRoom() != null)
        {
            this.pickUpBot(habbo.getHabboInfo().getCurrentRoom().getBot(Math.abs(botId)), habbo);
        }
    }


    public void pickUpBot(Bot bot, Habbo habbo)
    {
        if(bot != null && habbo != null)
        {
            BotPickUpEvent pickedUpEvent = new BotPickUpEvent(bot, habbo);
            Emulator.getPluginManager().fireEvent(pickedUpEvent);

            if(pickedUpEvent.isCancelled())
                return;

            if (bot.getOwnerId() == habbo.getHabboInfo().getId() || habbo.hasPermission(Permission.ACC_ANYROOMOWNER))
            {
                if (!habbo.hasPermission("acc_unlimited_bots") && habbo.getInventory().getBotsComponent().getBots().size() >= 15)
                    return;

                bot.onPickUp(habbo, habbo.getHabboInfo().getCurrentRoom());
                habbo.getHabboInfo().getCurrentRoom().removeBot(bot);
                bot.stopFollowingHabbo();
                bot.setOwnerId(habbo.getHabboInfo().getId());
                bot.setOwnerName(habbo.getHabboInfo().getUsername());
                bot.needsUpdate(true);
                Emulator.getThreading().run(bot);

                habbo.getInventory().getBotsComponent().addBot(bot);
                habbo.getClient().sendResponse(new AddBotComposer(bot));
            }
        }
    }


    public Bot loadBot(ResultSet set)
    {
        try
        {
            String type = set.getString("type");
            Class<? extends Bot> botClazz = botDefenitions.get(type);

            if(botClazz != null)
                return botClazz.getDeclaredConstructor(ResultSet.class).newInstance(set);
            else
                Emulator.getLogging().logErrorLine("Unknown Bot Type: " + type);
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return null;
    }


    public boolean deleteBot(Bot bot)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM bots WHERE id = ? LIMIT 1"))
        {
            statement.setInt(1, bot.getId());
            return statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return false;
    }


    public static void addBotDefinition(String type, Class<? extends Bot> botClazz) throws Exception
    {
        if(botClazz.getDeclaredConstructor(ResultSet.class) == null)
        {
            throw new Exception("Missing Bot(ResultSet) constructor!");
        }
        else
        {
            botClazz.getDeclaredConstructor(ResultSet.class).setAccessible(true);

            botDefenitions.put(type, botClazz);
        }
    }


    public void dispose()
    {
        for(Map.Entry<String, Class<? extends Bot>> set : botDefenitions.entrySet())
        {
            try
            {
                Method m = set.getValue().getMethod("dispose");
                m.setAccessible(true);
                m.invoke(null);
            }
            catch (NoSuchMethodException e)
            {
                Emulator.getLogging().logStart("Bot Manager -> Failed to execute dispose method upon bot type '" + set.getKey() + "'. No Such Method!");
            }
            catch (Exception e)
            {
                Emulator.getLogging().logStart("Bot Manager -> Failed to execute dispose method upon bot type '" + set.getKey() + "'. Error: " + e.getMessage());
            }
        }
    }
}
