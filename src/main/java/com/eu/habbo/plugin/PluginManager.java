package com.eu.habbo.plugin;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Easter;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.BotManager;
import com.eu.habbo.habbohotel.catalog.CatalogManager;
import com.eu.habbo.habbohotel.catalog.TargetOffer;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.tag.TagGame;
import com.eu.habbo.habbohotel.items.ItemManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootballGate;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.modtool.WordFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorManager;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorSaveEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewRequestLTDAvailabilityEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.users.*;
import com.eu.habbo.threading.runnables.RoomTrashing;
import com.eu.habbo.threading.runnables.ShutdownEmulator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.NoSuchElementException;
import java.util.Objects;

public class PluginManager
{
    private final THashSet<HabboPlugin> plugins = new THashSet<>();
    private final THashSet<Method>      methods = new THashSet<>();

    public void loadPlugins()
    {
        this.disposePlugins();

        File loc = new File("plugins");

        if (!loc.exists())
        {
            if (loc.mkdirs())
            {
                Emulator.getLogging().logStart("Created plugins directory!");
            }
        }

        for (File file : Objects.requireNonNull(loc.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"))))
        {
            URLClassLoader urlClassLoader;
            InputStream stream;
            try
            {
                urlClassLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
                stream         = urlClassLoader.getResourceAsStream("plugin.json");

                if (stream == null)
                {
                    throw new RuntimeException("Invalid Jar! Missing plugin.json in: " + file.getName());
                }

                byte[] content = new byte[stream.available()];

                if (stream.read(content) > 0)
                {
                    String body = new String(content);

                    Gson gson = new GsonBuilder().create();
                    HabboPluginConfiguration pluginConfigurtion = gson.fromJson(body, HabboPluginConfiguration.class);

                    try
                    {
                        Class<?> clazz = urlClassLoader.loadClass(pluginConfigurtion.main);
                        Class<? extends HabboPlugin> stackClazz = clazz.asSubclass(HabboPlugin.class);
                        Constructor<? extends HabboPlugin> constructor = stackClazz.getConstructor();
                        HabboPlugin plugin = constructor.newInstance();
                        plugin.configuration = pluginConfigurtion;
                        plugin.classLoader = urlClassLoader;
                        plugin.stream = stream;
                        this.plugins.add(plugin);
                        plugin.onEnable();
                    }
                    catch (Exception e)
                    {
                        Emulator.getLogging().logErrorLine("Could not load plugin " + pluginConfigurtion.name + "!");
                        Emulator.getLogging().logErrorLine(e);
                    }
                }
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }


    public void registerEvents(HabboPlugin plugin, EventListener listener)
    {
        synchronized (plugin.registeredEvents)
        {
            Method[] methods = listener.getClass().getMethods();

            for (Method method : methods)
            {
                if (method.getAnnotation(EventHandler.class) != null)
                {
                    if (method.getParameterTypes().length == 1)
                    {
                        if(Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                        {
                            final Class<?> eventClass = method.getParameterTypes()[0];

                            if (!plugin.registeredEvents.containsKey(eventClass.asSubclass(Event.class)))
                            {
                                plugin.registeredEvents.put(eventClass.asSubclass(Event.class), new THashSet<>());
                            }

                            plugin.registeredEvents.get(eventClass.asSubclass(Event.class)).add(method);
                        }
                    }
                }
            }
        }
    }


    public Event fireEvent(Event event)
    {
        for (Method method : this.methods)
        {
            if(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
            {
                try
                {
                    method.invoke(null, event);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine("Could not pass default event " + event.getClass().getName() + " to " + method.getClass().getName() + ":" + method.getName());
                    Emulator.getLogging().logErrorLine(e);
                }
            }
        }

        TObjectHashIterator<HabboPlugin> iterator = this.plugins.iterator();
        while (iterator.hasNext())
        {
            try
            {
                HabboPlugin plugin = iterator.next();

                if (plugin != null)
                {
                    THashSet<Method> methods = plugin.registeredEvents.get(event.getClass().asSubclass(Event.class));

                    if(methods != null)
                    {
                        for(Method method : methods)
                        {
                            try
                            {
                                method.invoke(plugin, event);
                            }
                            catch (Exception e)
                            {
                                Emulator.getLogging().logErrorLine("Could not pass event " + event.getClass().getName() + " to " + plugin.configuration.name);
                                Emulator.getLogging().logErrorLine(e);
                            }
                        }
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                break;
            }
        }

        return event;
    }


    public boolean isRegistered(Class<? extends Event> clazz, boolean pluginsOnly)
    {
        TObjectHashIterator<HabboPlugin> iterator = this.plugins.iterator();
        while (iterator.hasNext())
        {
            try
            {
                HabboPlugin plugin = iterator.next();
                if(plugin.isRegistered(clazz))
                    return true;
            }
            catch (NoSuchElementException e)
            {
                break;
            }
        }

        if(!pluginsOnly)
        {
            for (Method method : this.methods)
            {
                if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(clazz))
                {
                    return true;
                }
            }
        }

        return false;
    }


    public void dispose()
    {
        this.disposePlugins();

        Emulator.getLogging().logShutdownLine("Disposed Plugin Manager!");
    }

    private void disposePlugins()
    {
        TObjectHashIterator<HabboPlugin> iterator = this.plugins.iterator();
        while (iterator.hasNext())
        {
            try
            {
                HabboPlugin p = iterator.next();

                if (p != null)
                {

                    try
                    {
                        p.onDisable();
                        p.stream.close();
                        p.classLoader.close();
                    }
                    catch (IOException e)
                    {
                        Emulator.getLogging().logErrorLine(e);
                    }
                    catch (Exception ex)
                    {
                        Emulator.getLogging().logErrorLine("[CRITICAL][PLUGIN] Failed to disable " + p.configuration.name + " caused by: " + ex.getLocalizedMessage());
                        Emulator.getLogging().logErrorLine(ex);
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                break;
            }
        }
        this.plugins.clear();
    }

    public void reload()
    {
        long millis = System.currentTimeMillis();

        this.methods.clear();

        this.loadPlugins();

        Emulator.getLogging().logStart("Plugin Manager -> Loaded! " + this.plugins.size() + " plugins! (" + (System.currentTimeMillis() - millis) + " MS)");

        this.registerDefaultEvents();
    }

    private void registerDefaultEvents()
    {
        try
        {
            this.methods.add(RoomTrashing.class.getMethod("onUserWalkEvent", UserTakeStepEvent.class));
            this.methods.add(Easter.class.getMethod("onUserChangeMotto", UserSavedMottoEvent.class));
            this.methods.add(TagGame.class.getMethod("onUserLookAtPoint", RoomUnitLookAtPointEvent.class));
            this.methods.add(TagGame.class.getMethod("onUserWalkEvent", UserTakeStepEvent.class));
            this.methods.add(FreezeGame.class.getMethod("onConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(PacketManager.class.getMethod("onConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserDisconnectEvent", UserDisconnectEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserExitRoomEvent", UserExitRoomEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserSavedLookEvent", UserSavedLookEvent.class));
            this.methods.add(PluginManager.class.getMethod("globalOnConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
        }
        catch (NoSuchMethodException e)
        {
            Emulator.getLogging().logStart("Failed to define default events!");
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public THashSet<HabboPlugin> getPlugins()
    {
        return this.plugins;
    }

    @EventHandler
    public static void globalOnConfigurationUpdated(EmulatorConfigUpdatedEvent event)
    {
        ItemManager.RECYCLER_ENABLED = Emulator.getConfig().getBoolean("hotel.catalog.recycler.enabled");
        MarketPlace.MARKETPLACE_ENABLED = Emulator.getConfig().getBoolean("hotel.marketplace.enabled");
        MarketPlace.MARKETPLACE_CURRENCY = Emulator.getConfig().getInt("hotel.marketplace.currency");
        Messenger.SAVE_PRIVATE_CHATS = Emulator.getConfig().getBoolean("save.private.chats", false);
        PacketManager.DEBUG_SHOW_PACKETS = Emulator.getConfig().getBoolean("debug.show.packets");
        PacketManager.MULTI_THREADED_PACKET_HANDLING = Emulator.getConfig().getBoolean("io.client.multithreaded.handler");
        Room.HABBO_CHAT_DELAY = Emulator.getConfig().getBoolean("room.chat.delay", false);
        RoomChatMessage.SAVE_ROOM_CHATS = Emulator.getConfig().getBoolean("save.room.chats", false);
        RoomLayout.MAXIMUM_STEP_HEIGHT = Emulator.getConfig().getDouble("pathfinder.step.maximum.height", 1.1);
        RoomLayout.ALLOW_FALLING = Emulator.getConfig().getBoolean("pathfinder.step.allow.falling", true);
        RoomTrade.TRADING_ENABLED = Emulator.getConfig().getBoolean("hotel.trading.enabled") && !ShutdownEmulator.instantiated;
        RoomTrade.TRADING_REQUIRES_PERK = Emulator.getConfig().getBoolean("hotel.trading.requires.perk");
        WordFilter.ENABLED_FRIENDCHAT = Emulator.getConfig().getBoolean("hotel.wordfilter.messenger");

        BotManager.MINIMUM_CHAT_SPEED = Emulator.getConfig().getInt("hotel.bot.chat.minimum.interval");
        BotManager.MAXIMUM_CHAT_LENGTH = Emulator.getConfig().getInt("hotel.bot.max.chatlength");
        BotManager.MAXIMUM_NAME_LENGTH = Emulator.getConfig().getInt("hotel.bot.max.namelength");
        BotManager.MAXIMUM_CHAT_SPEED = Emulator.getConfig().getInt("hotel.bot.max.chatdelay");
        HabboInventory.MAXIMUM_ITEMS = Emulator.getConfig().getInt("hotel.inventory.max.items");
        Messenger.MAXIMUM_FRIENDS = Emulator.getConfig().getInt("hotel.max.friends");
        Messenger.MAXIMUM_FRIENDS_HC = Emulator.getConfig().getInt("hotel.max.friends.hc");
        Room.MAXIMUM_BOTS = Emulator.getConfig().getInt("hotel.max.bots.room");
        Room.MAXIMUM_PETS = Emulator.getConfig().getInt("hotel.pets.max.room");
        Room.HAND_ITEM_TIME = Emulator.getConfig().getInt("hotel.rooms.handitem.time");
        Room.IDLE_CYCLES = Emulator.getConfig().getInt("hotel.roomuser.idle.cycles", 240);
        Room.IDLE_CYCLES_KICK = Emulator.getConfig().getInt("hotel.roomuser.idle.cycles.kick", 480);
        RoomManager.MAXIMUM_ROOMS_VIP = Emulator.getConfig().getInt("hotel.max.rooms.vip");
        RoomManager.MAXIMUM_ROOMS_USER = Emulator.getConfig().getInt("hotel.max.rooms.user");
        RoomManager.HOME_ROOM_ID = Emulator.getConfig().getInt("hotel.home.room");
        WiredHandler.MAXIMUM_FURNI_SELECTION = Emulator.getConfig().getInt("hotel.wired.furni.selection.count");
        WiredHandler.TELEPORT_DELAY = Emulator.getConfig().getInt("wired.effect.teleport.delay", 500);
        NavigatorManager.MAXIMUM_RESULTS_PER_PAGE = Emulator.getConfig().getInt("hotel.navigator.search.maxresults");
        NavigatorManager.CATEGORY_SORT_USING_ORDER_NUM = Emulator.getConfig().getBoolean("hotel.navigator.sort.ordernum");
        RoomChatMessage.MAXIMUM_LENGTH = Emulator.getConfig().getInt("hotel.chat.max.length");

        String[] bannedBubbles = Emulator.getConfig().getValue("commands.cmd_chatcolor.banned_numbers").split(";");
        RoomChatMessage.BANNED_BUBBLES = new int[bannedBubbles.length];
        for (int i = 0; i < RoomChatMessage.BANNED_BUBBLES.length; i++)
        {
            try
            {
                RoomChatMessage.BANNED_BUBBLES[i] = Integer.valueOf(bannedBubbles[i]);
            }
            catch (Exception e)
            {}
        }

        HabboManager.WELCOME_MESSAGE = Emulator.getConfig().getValue("hotel.welcome.alert.message").replace("<br>", "<br/>").replace("<br />", "<br/>").replace("\\r", "\r").replace("\\n", "\n").replace("\\t", "\t");
        Room.PREFIX_FORMAT = Emulator.getConfig().getValue("room.chat.prefix.format");
        FloorPlanEditorSaveEvent.MAXIMUM_FLOORPLAN_WIDTH_LENGTH = Emulator.getConfig().getInt("hotel.floorplan.max.widthlength");
        FloorPlanEditorSaveEvent.MAXIMUM_FLOORPLAN_SIZE = Emulator.getConfig().getInt("hotel.floorplan.max.totalarea");

        HotelViewRequestLTDAvailabilityEvent.ENABLED = Emulator.getConfig().getBoolean("hotel.view.ltdcountdown.enabled");
        HotelViewRequestLTDAvailabilityEvent.TIMESTAMP =  Emulator.getConfig().getInt("hotel.view.ltdcountdown.timestamp");
        HotelViewRequestLTDAvailabilityEvent.ITEM_ID = Emulator.getConfig().getInt("hotel.view.ltdcountdown.itemid");
        HotelViewRequestLTDAvailabilityEvent.PAGE_ID = Emulator.getConfig().getInt("hotel.view.ltdcountdown.pageid");
        HotelViewRequestLTDAvailabilityEvent.ITEM_NAME = Emulator.getConfig().getValue("hotel.view.ltdcountdown.itemname");
        InteractionPostIt.STICKYPOLE_PREFIX_TEXT = Emulator.getConfig().getValue("hotel.room.stickypole.prefix");
        TargetOffer.ACTIVE_TARGET_OFFER_ID = Emulator.getConfig().getInt("hotel.targetoffer.id");
        WordFilter.DEFAULT_REPLACEMENT = Emulator.getConfig().getValue("hotel.wordfilter.replacement");
        CatalogManager.PURCHASE_COOLDOWN = Emulator.getConfig().getInt("hotel.catalog.purchase.cooldown");
        CatalogManager.SORT_USING_ORDERNUM = Emulator.getConfig().getBoolean("hotel.catalog.items.display.ordernum");
        AchievementManager.TALENTTRACK_ENABLED = Emulator.getConfig().getBoolean("hotel.talenttrack.enabled");
        InteractionRoller.NO_RULES = Emulator.getConfig().getBoolean("hotel.room.rollers.norules");
        RoomManager.SHOW_PUBLIC_IN_POPULAR_TAB = Emulator.getConfig().getBoolean("hotel.navigator.populartab.publics");
    }
}
