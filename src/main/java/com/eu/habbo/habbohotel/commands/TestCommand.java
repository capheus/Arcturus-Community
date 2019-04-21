package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterRequestAccountStatusEvent;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterRequestGamesEvent;
import com.eu.habbo.messages.incoming.rooms.pets.MovePetEvent;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.MessagesForYouComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomQueueStatusMessage;
import com.eu.habbo.messages.outgoing.rooms.pets.PetInformationComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.PetStatusUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.messages.outgoing.users.UserDataComposer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestCommand extends Command
{
    public static boolean stopThreads = true;

    public TestCommand()
    {
        super("acc_debug", new String[]{ "test" });
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if (true) return true;
        if (params[1].equalsIgnoreCase("ut"))
        {
            RoomTile tile = gameClient.getHabbo().getRoomUnit().getCurrentLocation();
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().updateTile(tile);
            return true;
        }

        if (params[1].equalsIgnoreCase("clients"))
        {
            System.out.println(Emulator.getGameServer().getGameClientManager().getSessions().size());
        }

        if (params[1].equalsIgnoreCase("queue"))
        {
            gameClient.sendResponse(new RoomQueueStatusMessage());
            return true;
        }
        if (params[1].equalsIgnoreCase("public"))
        {
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().setPublicRoom(true);
            return true;
        }

        if (params[1].equalsIgnoreCase("randtel"))
        {
            RoomTile tile = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getRandomWalkableTile();
            gameClient.getHabbo().getRoomUnit().setCurrentLocation(tile);
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().updateHabbo(gameClient.getHabbo());
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserStatusComposer(gameClient.getHabbo().getRoomUnit()).compose());
            return true;
        }

        if (params[1].equals("ach"))
        {
            AchievementManager.progressAchievement(gameClient.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("Jogger"), 1000);
            return true;
        }

        if (params[1].equals("asddsa"))
        {
            gameClient.getHabbo().getHabboStats().addAchievementScore(1000);
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDataComposer(gameClient.getHabbo()).compose());
            return true;
        }

        if (params[1].equals("gc"))
        {

            Emulator.getGameServer().getPacketManager().registerHandler(Incoming.GameCenterRequestGamesEvent, GameCenterRequestGamesEvent.class);
            Emulator.getGameServer().getPacketManager().registerHandler(Incoming.GameCenterRequestAccountStatusEvent, GameCenterRequestAccountStatusEvent.class);
            return true;
        }

        if (params[1].equals("namechange"))
        {
            gameClient.sendResponse(new UserDataComposer(gameClient.getHabbo()));
            return true;
        }
        //Emulator.getGameEnvironment().getRoomManager().clearInactiveRooms();
        //gameClient.sendResponse(new RoomDataComposer(gameClient.getHabbo().getHabboInfo().getCurrentRoom(), gameClient.getHabbo(), true, false));

        if (params[1].equals("uach"))
        {
            Emulator.getGameEnvironment().getAchievementManager().reload();
        }

        if(params[1].equals("units"))
        {
            StringBuilder s = new StringBuilder();

            for(Habbo habbo : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos())
            {
                s.append("Habbo ID: ").append(habbo.getHabboInfo().getId()).append(", RoomUnit ID: ").append(habbo.getRoomUnit().getId()).append("\r");
            }

            for (Pet pet : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentPets().valueCollection())
            {
                s.append("Pet ID: ").append(pet.getId()).append(", RoomUnit ID: ").append(pet.getRoomUnit().getId()).append(", Name: ").append(pet.getName());

                if (pet instanceof MonsterplantPet)
                {
                    s.append(", B:").append(((MonsterplantPet) pet).canBreed() ? "Y" : "N").append(", PB: ").append(((MonsterplantPet) pet).isPubliclyBreedable() ? "Y" : "N").append(", D: ").append(((MonsterplantPet) pet).isDead() ? "Y" : "N");
                }

                s.append("\r");
            }

            gameClient.sendResponse(new MessagesForYouComposer(new String[]{s.toString()}));
            return true;
        }

        if (params[1].equalsIgnoreCase("rebr"))
        {
            for (Pet pet : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentPets().valueCollection())
            {
                if (pet instanceof MonsterplantPet)
                {
                    ((MonsterplantPet) pet).setPubliclyBreedable(false);
                    ((MonsterplantPet) pet).setCanBreed(true);
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new PetStatusUpdateComposer(pet).compose());
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new PetInformationComposer(pet, gameClient.getHabbo().getHabboInfo().getCurrentRoom()).compose());
                }
            }

            return true;
        }

        if (params[1].equalsIgnoreCase("bots"))
        {
            StringBuilder message = new StringBuilder();

            for (Bot bot : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentBots().valueCollection())
            {
                message.append("Name: ").append(bot.getName()).append(", ID: ").append(bot.getId()).append(", RID: ").append(bot.getRoomUnit().getId()).append(", Rot: ").append(bot.getRoomUnit().getBodyRotation()).append("\r");
            }

            gameClient.sendResponse(new MessagesForYouComposer(new String[]{message.toString()}));
            return true;
        }

        if (params[1].equalsIgnoreCase("packu"))
        {
            Emulator.getGameServer().getPacketManager().registerHandler(Incoming.MovePetEvent, MovePetEvent.class);
            return true;
        }

        if(params[1].equals("a"))
        {
            int count = Integer.valueOf(params[2]);

            for(int i = 0; i < count; i++)
            {
                gameClient.getHabbo().whisper("" + i, RoomChatMessageBubbles.getBubble(i));
            }

            return true;
        }
        else if(params[1].equals("b"))
        {
            try
            {
                int itemId = Integer.valueOf(params[2]);

                HabboItem item = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

                if(item != null)
                {
                    item.setExtradata(params[3]);
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().updateItem(item);
                }
            }
            catch (Exception e)
            {

            }
            return true;
        }
        else if(params[1].equalsIgnoreCase("pet"))
        {
            Pet pet = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getPet(Integer.valueOf(params[2]));

            if(pet != null)
            {
                String a;
                String b = "";
                String c = "";
                if(params[3] != null)
                {
                    a = params[3];
                    if(params.length > 4)
                    {
                        b = params[4];
                    }
                    if(params.length > 5)
                    {
                        c = params[5];
                    }
                    pet.getRoomUnit().setStatus(RoomUnitStatus.fromString(a), b + " " + c);
                    gameClient.sendResponse(new RoomUserStatusComposer(pet.getRoomUnit()));
                }
            }
        }
        else if (params[1].equalsIgnoreCase("petc"))
        {
            Pet pet = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getPet(Integer.valueOf(params[2]));

            if (pet != null)
            {
                pet.getRoomUnit().clearStatus();
                gameClient.sendResponse(new RoomUserStatusComposer(pet.getRoomUnit()));
            }
        }
        else if (params[1].equalsIgnoreCase("rand"))
        {
            Map<Integer, Integer> results = new HashMap<>();

            for (int i = 0; i < Integer.valueOf(params[2]); i++)
            {
                int random = PetManager.random(0, 12, Double.valueOf(params[3]));

                if (!results.containsKey(random))
                {
                    results.put(random, 0);
                }

                results.put(random, results.get(random) + 1);
            }

            StringBuilder result = new StringBuilder("Results : " + params[2] + "<br/><br/>");

            for (Map.Entry<Integer, Integer> set : results.entrySet())
            {
                result.append(set.getKey()).append(" -> ").append(set.getValue()).append("<br/>");
            }

            gameClient.sendResponse(new GenericAlertComposer(result.toString()));
        }
        else if (params[1].equalsIgnoreCase("threads"))
        {
            if (stopThreads)
            {
                stopThreads = false;
                for (int i = 0; i < 30; i++)
                {
                    final int finalI = i;
                    Emulator.getThreading().run(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(500);
                                Emulator.getLogging().logStart("Started " + finalI + " on " + Thread.currentThread().getName());
                                if (!TestCommand.stopThreads)
                                {
                                    Emulator.getThreading().run(this);
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }, i * 10);
                }
            }
            else
            {
                stopThreads = true;
            }
        }
        else if (params[1].equalsIgnoreCase("pethere"))
        {
            Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
            List<RoomTile> tiles = room.getLayout().getTilesAround(gameClient.getHabbo().getRoomUnit().getCurrentLocation());

            room.getCurrentPets().forEachValue(new TObjectProcedure<Pet>()
            {
                @Override
                public boolean execute(Pet object)
                {
                    Emulator.getThreading().run(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            object.getRoomUnit().setGoalLocation(tiles.get(Emulator.getRandom().nextInt(tiles.size())));
                        }
                    });
                    return true;
                }
            });
        }
        else if(params[1].equalsIgnoreCase("st"))
        {
            gameClient.getHabbo().getRoomUnit().setStatus(RoomUnitStatus.fromString(params[2]), params[3]);
            gameClient.sendResponse(new RoomUserStatusComposer(gameClient.getHabbo().getRoomUnit()));
        }
        else if (params[1].equalsIgnoreCase("filt"))
        {
            gameClient.sendResponse(new GenericAlertComposer(Normalizer.normalize(params[2], Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("\\p{M}", "")));
        }
        else if (params[1].equalsIgnoreCase("nux"))
        {
            gameClient.sendResponse(new MessageComposer()
            {
                @Override
                public ServerMessage compose()
                {
                    this.response.init(Outgoing.NewUserGiftComposer);

                    this.response.appendInt(1); //?
                    this.response.appendInt(1);
                    this.response.appendInt(2);
                    this.response.appendInt(6);

                    String[] gift1 = {"throne.png", "throne"}; //Emulator.getConfig().getValue("nux.gift.1").split(";");
                    String[] gift2 = {"throne.png", "throne"}; //Emulator.getConfig().getValue("nux.gift.2").split(";");
                    String[] gift3 = {"throne.png", "throne"}; //Emulator.getConfig().getValue("nux.gift.3").split(";");

                    this.response.appendString(gift1[0]);
                    this.response.appendInt(2);
                    this.response.appendString(gift1[1]);
                    this.response.appendString("");
                    this.response.appendString("typewriter");
                    this.response.appendString("");

                    this.response.appendString(gift2[0]);
                    this.response.appendInt(1);
                    this.response.appendString(gift2[1]);
                    this.response.appendString("");

                    this.response.appendString(gift3[0]);
                    this.response.appendInt(1);
                    this.response.appendString(gift3[1]);
                    this.response.appendString("");

                    this.response.appendString(gift1[0]);
                    this.response.appendInt(2);
                    this.response.appendString(gift1[1]);
                    this.response.appendString("");
                    this.response.appendString("typewriter");
                    this.response.appendString("");

                    this.response.appendString(gift2[0]);
                    this.response.appendInt(1);
                    this.response.appendString(gift2[1]);
                    this.response.appendString("");

                    this.response.appendString(gift3[0]);
                    this.response.appendInt(1);
                    this.response.appendString(gift3[1]);
                    this.response.appendString("");

                    return this.response;
                }
            });
        }
        else if (params[1].equals("adv"))
        {
        }
        else if (params[1].equals("datb"))
        {
                    long millis;
                    long diff = 1;
                    try(Connection conn = Emulator.getDatabase().getDataSource().getConnection())
                    {
                        millis = System.currentTimeMillis();
                        for (long i = 0; i < 1000000; i++)
                        {
                            try (PreparedStatement stmt = conn.prepareStatement("SELECT 1"))
                            {
                                //PreparedStatement stmt2 = conn.prepareStatement("SELECT 2");
                                stmt.close();
                            }
                            //stmt2.close();
                        }
                        diff = System.currentTimeMillis() - millis;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println("Difference " + (diff) + "MS. ops: "  + ((long)1000000 / diff) + " ops/MS");

        }
        else
        {
            try
            {
                int header = Integer.valueOf(params[1]);

                ServerMessage message = new ServerMessage(header);

                for (int i = 1; i < params.length; i++)
                {
                    String[] data = params[i].split(":");

                    if (data[0].equalsIgnoreCase("b"))
                    {
                        message.appendBoolean(data[1].equalsIgnoreCase("1"));
                    } else if (data[0].equalsIgnoreCase("s"))
                    {
                        if (data.length > 1)
                        {
                            message.appendString(data[1].replace("%http%", "http://"));
                        } else
                        {
                            message.appendString("");
                        }
                    } else if (data[0].equals("i"))
                    {
                        message.appendInt(Integer.valueOf(data[1]));
                    } else if (data[0].equalsIgnoreCase("by"))
                    {
                        message.appendByte(Integer.valueOf(data[1]));
                    } else if (data[0].equalsIgnoreCase("sh"))
                    {
                        message.appendShort(Integer.valueOf(data[1]));
                    }
                }

                Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo("Admin");

                if(habbo != null)
                {
                    habbo.getClient().sendResponse(message);
                }
            } catch (Exception e)
            {
                gameClient.sendResponse(new GenericAlertComposer("Hey, what u doing m8."));

                return false;
            }
        }



        //if(params.length >= 2)
        //{

        //}


        //}

        //int header = Integer.valueOf(params[1]);

        //2823
        //913
        //1604
        //gameClient.sendResponse(new SnowWarsCompose1(913));
        //gameClient.sendResponse(new SnowWarsStartLobbyCounter());
        //gameClient.sendResponse(new SnowWarsQuePositionComposer());
        //gameClient.sendResponse(new SnowWarsCompose1(1604));
        //gameClient.sendResponse(new SnowWarsLevelDataComposer());


        return true;
    }

    public void testConcurrentClose() throws Exception
    {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");

        try (HikariDataSource ds = new HikariDataSource(config);
             final Connection connection = ds.getConnection()) {

            ExecutorService executorService = Executors.newFixedThreadPool(10);

            List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < 500; i++) {
                final PreparedStatement preparedStatement =
                        connection.prepareStatement("");

                futures.add(executorService.submit(new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        preparedStatement.close();

                        return null;
                    }

                }));
            }

            executorService.shutdown();

            for (Future<?> future : futures) {
                future.get();
            }
        }
    }
}
