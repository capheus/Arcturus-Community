package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.achievements.TalentTrackType;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.users.cache.HabboOfferPurchase;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.stack.array.TIntArrayStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HabboStats implements Runnable
{
    private final int timeLoggedIn = Emulator.getIntUnixTimestamp();

    private Habbo habbo;

    public int achievementScore;
    public int respectPointsReceived;
    public int respectPointsGiven;
    public int respectPointsToGive;

    public int petRespectPointsToGive;

    public boolean blockFollowing;
    public boolean blockFriendRequests;
    public boolean blockRoomInvites;
    public boolean blockStaffAlerts;
    private boolean allowTrade;
    public boolean preferOldChat;
    public boolean blockCameraFollow;
    public RoomChatMessageBubbles chatColor;

    private int clubExpireTimestamp;

    public int volumeSystem;
    public int volumeFurni;
    public int volumeTrax;

    public int guild;
    public List<Integer> guilds;

    public String[] tags;

    public TIntArrayStack votedRooms;
    public int loginStreak;
    public int rentedItemId;
    public int rentedTimeEnd;
    public int hofPoints;
    public boolean ignorePets;
    public boolean ignoreBots;

    private final THashMap<Achievement, Integer> achievementProgress;
    private final THashMap<Achievement, Integer> achievementCache;
    private final THashMap<Integer, CatalogItem> recentPurchases;
    private final TIntArrayList favoriteRooms;
    private final TIntArrayList ignoredUsers;
    public final TIntArrayList secretRecipes;

    public int citizenshipLevel;
    public int helpersLevel;
    public boolean perkTrade;

    public final HabboNavigatorWindowSettings navigatorWindowSettings;
    public final THashMap<String, Object> cache;

    public long roomEnterTimestamp;
    public int chatCounter;
    public long lastChat;
    public long lastUsersSearched;
    public boolean nux;
    public boolean nuxReward;
    public int nuxStep = 1;

    private int muteEndTime;
    public int mutedCount = 0;
    public boolean mutedBubbleTracker = false;

    public String changeNameChecked = "";
    public final TIntArrayList calendarRewardsClaimed;

    public boolean allowNameChange;
    public boolean isPurchasingFurniture = false;

    public THashMap<Integer, List<Integer>> ltdPurchaseLog = new THashMap<>(0);
    public long lastTradeTimestamp = Emulator.getIntUnixTimestamp();
    public long lastPurchaseTimestamp = Emulator.getIntUnixTimestamp();
    public long lastGiftTimestamp = Emulator.getIntUnixTimestamp();
    public final TIntObjectMap<HabboOfferPurchase> offerCache = new TIntObjectHashMap<>();

    private HabboStats(ResultSet set, Habbo habbo) throws SQLException
    {
        this.cache = new THashMap<>(0);
        this.achievementProgress = new THashMap<>(0);
        this.achievementCache = new THashMap<>(0);
        this.recentPurchases = new THashMap<>(0);
        this.favoriteRooms = new TIntArrayList(0);
        this.ignoredUsers = new TIntArrayList(0);
        this.secretRecipes = new TIntArrayList(0);
        this.calendarRewardsClaimed = new TIntArrayList(0);

        this.habbo = habbo;

        this.achievementScore = set.getInt("achievement_score");
        this.respectPointsReceived = set.getInt("respects_received");
        this.respectPointsGiven = set.getInt("respects_given");
        this.petRespectPointsToGive = set.getInt("daily_pet_respect_points");
        this.respectPointsToGive = set.getInt("daily_respect_points");
        this.blockFollowing = set.getString("block_following").equals("1");
        this.blockFriendRequests = set.getString("block_friendrequests").equals("1");
        this.blockRoomInvites = set.getString("block_roominvites").equals("1");
        this.preferOldChat = set.getString("old_chat").equals("1");
        this.blockCameraFollow = set.getString("block_camera_follow").equals("1");
        this.guild = set.getInt("guild_id");
        this.guilds = new ArrayList<>();
        this.tags = set.getString("tags").split(";");
        this.allowTrade = set.getString("can_trade").equals("1");
        this.votedRooms = new TIntArrayStack();
        this.clubExpireTimestamp = set.getInt("club_expire_timestamp");
        this.loginStreak = set.getInt("login_streak");
        this.rentedItemId = set.getInt("rent_space_id");
        this.rentedTimeEnd = set.getInt("rent_space_endtime");
        this.volumeSystem = set.getInt("volume_system");
        this.volumeFurni = set.getInt("volume_furni");
        this.volumeTrax = set.getInt("volume_trax");
        this.chatColor = RoomChatMessageBubbles.getBubble(set.getInt("chat_color"));
        this.hofPoints = set.getInt("hof_points");
        this.blockStaffAlerts = set.getString("block_alerts").equals("1");
        this.citizenshipLevel = set.getInt("talent_track_citizenship_level");
        this.helpersLevel = set.getInt("talent_track_helpers_level");
        this.ignoreBots = set.getString("ignore_bots").equalsIgnoreCase("1");
        this.ignorePets = set.getString("ignore_pets").equalsIgnoreCase("1");
        this.nux = set.getString("nux").equals("1");
        this.muteEndTime = set.getInt("mute_end_timestamp");
        this.allowNameChange = set.getString("allow_name_change").equalsIgnoreCase("1");
        this.perkTrade = set.getString("perk_trade").equalsIgnoreCase("1");
        this.nuxReward = this.nux;

        try (PreparedStatement statement = set.getStatement().getConnection().prepareStatement("SELECT * FROM user_window_settings WHERE user_id = ? LIMIT 1"))
        {
            statement.setInt(1, this.habbo.getHabboInfo().getId());
            try (ResultSet nSet = statement.executeQuery())
            {
                if (nSet.next())
                {
                    this.navigatorWindowSettings = new HabboNavigatorWindowSettings(nSet);
                }
                else
                {
                    try (PreparedStatement stmt = statement.getConnection().prepareStatement("INSERT INTO user_window_settings (user_id) VALUES (?)"))
                    {
                        stmt.setInt(1, this.habbo.getHabboInfo().getId());
                        stmt.executeUpdate();
                    }

                    this.navigatorWindowSettings = new HabboNavigatorWindowSettings(habbo.getHabboInfo().getId());
                }
            }
        }

        try (PreparedStatement statement = set.getStatement().getConnection().prepareStatement("SELECT * FROM users_navigator_settings WHERE user_id = ?"))
        {
            statement.setInt(1, this.habbo.getHabboInfo().getId());
            try (ResultSet nSet = statement.executeQuery())
            {
                while (nSet.next())
                {
                    this.navigatorWindowSettings.addDisplayMode(nSet.getString("caption"), new HabboNavigatorPersonalDisplayMode(nSet));
                }
            }
        }

        try (PreparedStatement favoriteRoomsStatement = set.getStatement().getConnection().prepareStatement("SELECT * FROM users_favorite_rooms WHERE user_id = ?"))
        {
            favoriteRoomsStatement.setInt(1, this.habbo.getHabboInfo().getId());
            try (ResultSet favoriteSet = favoriteRoomsStatement.executeQuery())
            {
                while (favoriteSet.next())
                {
                    this.favoriteRooms.add(favoriteSet.getInt("room_id"));
                }
            }

        }

        try (PreparedStatement recipesStatement = set.getStatement().getConnection().prepareStatement("SELECT * FROM users_recipes WHERE user_id = ?"))
        {
            recipesStatement.setInt(1, this.habbo.getHabboInfo().getId());
            try (ResultSet recipeSet = recipesStatement.executeQuery())
            {
                while (recipeSet.next())
                {
                    this.secretRecipes.add(recipeSet.getInt("recipe"));
                }
            }
        }

        try (PreparedStatement calendarRewardsStatement = set.getStatement().getConnection().prepareStatement("SELECT * FROM calendar_rewards_claimed WHERE user_id = ?"))
        {
            calendarRewardsStatement.setInt(1, this.habbo.getHabboInfo().getId());
            try (ResultSet rewardSet = calendarRewardsStatement.executeQuery())
            {
                while (rewardSet.next())
                {
                    this.calendarRewardsClaimed.add(rewardSet.getInt("reward_id"));
                }
            }
        }

        try (PreparedStatement ltdPurchaseLogStatement = set.getStatement().getConnection().prepareStatement("SELECT catalog_item_id, timestamp FROM catalog_items_limited WHERE user_id = ? AND timestamp > ?"))
        {
            ltdPurchaseLogStatement.setInt(1, this.habbo.getHabboInfo().getId());
            ltdPurchaseLogStatement.setInt(2, Emulator.getIntUnixTimestamp() - 86400);
            try (ResultSet ltdSet = ltdPurchaseLogStatement.executeQuery())
            {
                while (ltdSet.next())
                {
                    this.addLtdLog(ltdSet.getInt("catalog_item_id"), ltdSet.getInt("timestamp"));
                }
            }
        }

        try (PreparedStatement ignoredPlayersStatement = set.getStatement().getConnection().prepareStatement("SELECT target_id FROM users_ignored WHERE user_id = ?"))
        {
            ignoredPlayersStatement.setInt(1, this.habbo.getHabboInfo().getId());
            try (ResultSet ignoredSet = ignoredPlayersStatement.executeQuery())
            {
                while (ignoredSet.next())
                {
                    this.ignoredUsers.add(ignoredSet.getInt(1));
                }
            }
        }

        try (PreparedStatement loadOfferPurchaseStatement = set.getStatement().getConnection().prepareStatement("SELECT * FROM users_target_offer_purchases WHERE user_id = ?"))
        {
            loadOfferPurchaseStatement.setInt(1, this.habbo.getHabboInfo().getId());
            try (ResultSet offerSet = loadOfferPurchaseStatement.executeQuery())
            {
                while (offerSet.next())
                {
                    this.offerCache.put(offerSet.getInt("offer_id"), new HabboOfferPurchase(offerSet));
                }
            }
        }
    }

    @Override
    public void run()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE users_settings SET achievement_score = ?, respects_received = ?, respects_given = ?, daily_respect_points = ?, block_following = ?, block_friendrequests = ?, online_time = online_time + ?, guild_id = ?, daily_pet_respect_points = ?, club_expire_timestamp = ?, login_streak = ?, rent_space_id = ?, rent_space_endtime = ?, volume_system = ?, volume_furni = ?, volume_trax = ?, block_roominvites = ?, old_chat = ?, block_camera_follow = ?, chat_color = ?, hof_points = ?, block_alerts = ?, talent_track_citizenship_level = ?, talent_track_helpers_level = ?, ignore_bots = ?, ignore_pets = ?, nux = ?, mute_end_timestamp = ?, allow_name_change = ?, perk_trade = ?, can_trade = ? WHERE user_id = ? LIMIT 1"))
            {
                statement.setInt(1, this.achievementScore);
                statement.setInt(2, this.respectPointsReceived);
                statement.setInt(3, this.respectPointsGiven);
                statement.setInt(4, this.respectPointsToGive);
                statement.setString(5, this.blockFollowing ? "1" : "0");
                statement.setString(6, this.blockFriendRequests ? "1" : "0");
                statement.setInt(7, Emulator.getIntUnixTimestamp() - this.timeLoggedIn);
                statement.setInt(8, this.guild);
                statement.setInt(9, this.petRespectPointsToGive);
                statement.setInt(10, this.clubExpireTimestamp);
                statement.setInt(11, this.loginStreak);
                statement.setInt(12, this.rentedItemId);
                statement.setInt(13, this.rentedTimeEnd);
                statement.setInt(14, this.volumeSystem);
                statement.setInt(15, this.volumeFurni);
                statement.setInt(16, this.volumeTrax);
                statement.setString(17, this.blockRoomInvites ? "1" : "0");
                statement.setString(18, this.preferOldChat ? "1" : "0");
                statement.setString(19, this.blockCameraFollow ? "1" : "0");
                statement.setInt(20, this.chatColor.getType());
                statement.setInt(21, this.hofPoints);
                statement.setString(22, this.blockStaffAlerts ? "1" : "0");
                statement.setInt(23, this.citizenshipLevel);
                statement.setInt(24, this.helpersLevel);
                statement.setString(25, this.ignoreBots ? "1" : "0");
                statement.setString(26, this.ignorePets ? "1" : "0");
                statement.setString(27, this.nux ? "1" : "0");
                statement.setInt(28, this.muteEndTime);
                statement.setString(29, this.allowNameChange ? "1" : "0");
                statement.setString(30, this.perkTrade ? "1" : "0");
                statement.setString(31, this.allowTrade ? "1" : "0");
                statement.setInt(32, this.habbo.getHabboInfo().getId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement("UPDATE user_window_settings SET x = ?, y = ?, width = ?, height = ?, open_searches = ? WHERE user_id = ? LIMIT 1"))
            {
                statement.setInt(1, this.navigatorWindowSettings.x);
                statement.setInt(2, this.navigatorWindowSettings.y);
                statement.setInt(3, this.navigatorWindowSettings.width);
                statement.setInt(4, this.navigatorWindowSettings.height);
                statement.setString(5, this.navigatorWindowSettings.openSearches ? "1" : "0");
                statement.setInt(6, this.habbo.getHabboInfo().getId());
                statement.executeUpdate();
            }

            if (!this.offerCache.isEmpty())
            {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE users_target_offer_purchases SET state = ?, amount = ?, last_purchase = ? WHERE user_id = ? AND offer_id = ?"))
                {
                    for (HabboOfferPurchase purchase : this.offerCache.valueCollection())
                    {
                        if (!purchase.needsUpdate()) continue;

                        statement.setInt(1, purchase.getState());
                        statement.setInt(2, purchase.getAmount());
                        statement.setInt(3, purchase.getLastPurchaseTimestamp());
                        statement.setInt(4, this.habbo.getHabboInfo().getId());
                        statement.setInt(5, purchase.getOfferId());
                        statement.execute();
                    }
                }
            }

            this.navigatorWindowSettings.save(connection);
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void dispose()
    {
        this.run();
        this.habbo = null;
        this.recentPurchases.clear();
    }

    private static HabboStats createNewStats(Habbo habbo)
    {
        habbo.firstVisit = true;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users_settings (user_id) VALUES (?)"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return load(habbo);
    }

    public static HabboStats load(Habbo habbo)
    {
        HabboStats stats = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_settings WHERE user_id = ? LIMIT 1"))
            {
                statement.setInt(1, habbo.getHabboInfo().getId());
                try (ResultSet set = statement.executeQuery())
                {
                    set.first();
                    if (set.getRow() != 0)
                    {
                        stats = new HabboStats(set, habbo);
                    }
                    else
                    {
                        stats = createNewStats(habbo);
                    }
                }
            }

            if(stats != null)
            {
                try (PreparedStatement statement = connection.prepareStatement("SELECT guild_id FROM guilds_members WHERE user_id = ? AND level_id < 3 LIMIT 100"))
                {
                    statement.setInt(1, habbo.getHabboInfo().getId());
                    try (ResultSet set = statement.executeQuery())
                    {

                        int i = 0;
                        while (set.next())
                        {
                            stats.guilds.add(set.getInt("guild_id"));
                            i++;
                        }
                    }
                }

                Collections.sort(stats.guilds);

                try (PreparedStatement statement = connection.prepareStatement("SELECT room_id FROM room_votes WHERE user_id = ?"))
                {
                    statement.setInt(1, habbo.getHabboInfo().getId());
                    try (ResultSet set = statement.executeQuery())
                    {
                        while (set.next())
                        {
                            stats.votedRooms.push(set.getInt("room_id"));
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_achievements WHERE user_id = ?"))
                {
                    statement.setInt(1, habbo.getHabboInfo().getId());
                    try (ResultSet set = statement.executeQuery())
                    {
                        while (set.next())
                        {
                            Achievement achievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement(set.getString("achievement_name"));

                            if (achievement != null)
                            {
                                stats.achievementProgress.put(achievement, set.getInt("progress"));
                            }
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return stats;
    }

    public void addGuild(int guildId)
    {
        if (!this.guilds.contains(guildId))
        {
            this.guilds.add(guildId);
        }
    }

    public void removeGuild(int guildId)
    {
        this.guilds.remove((Integer)guildId);
    }

    public boolean hasGuild(int guildId)
    {
        for(int i : this.guilds)
        {
            if(i == guildId)
                return true;
        }

        return false;
    }

    public int getAchievementScore()
    {
        return this.achievementScore;
    }

    public void addAchievementScore(int achievementScore)
    {
        this.achievementScore += achievementScore;
    }

    public int getAchievementProgress(Achievement achievement)
    {
        if(this.achievementProgress.containsKey(achievement))
            return this.achievementProgress.get(achievement);

        return -1;
    }

    public void setProgress(Achievement achievement, int progress)
    {
        this.achievementProgress.put(achievement, progress);
    }

    public int getRentedTimeEnd()
    {
        return this.rentedTimeEnd;
    }

    public void setRentedTimeEnd(int rentedTimeEnd)
    {
        this.rentedTimeEnd = rentedTimeEnd;
    }

    public int getRentedItemId()
    {
        return this.rentedItemId;
    }

    public void setRentedItemId(int rentedItemId)
    {
        this.rentedItemId = rentedItemId;
    }

    public boolean isRentingSpace()
    {
        return this.rentedTimeEnd >= Emulator.getIntUnixTimestamp();
    }

    public int getClubExpireTimestamp()
    {
        return this.clubExpireTimestamp;
    }

    public void setClubExpireTimestamp(int clubExpireTimestamp)
    {
        this.clubExpireTimestamp = clubExpireTimestamp;
    }

    public boolean hasActiveClub()
    {
        return this.clubExpireTimestamp > Emulator.getIntUnixTimestamp();
    }

    public THashMap<Achievement, Integer> getAchievementProgress()
    {
        return this.achievementProgress;
    }

    public THashMap<Achievement, Integer> getAchievementCache()
    {
        return this.achievementCache;
    }

    public void addPurchase(CatalogItem item)
    {
        if(!this.recentPurchases.containsKey(item.getId()))
        {
            this.recentPurchases.put(item.getId(), item);
        }
    }

    public THashMap<Integer, CatalogItem> getRecentPurchases()
    {
        return this.recentPurchases;
    }

    public void disposeRecentPurchases()
    {
        this.recentPurchases.clear();
    }

    public boolean addFavoriteRoom(int roomId)
    {
        if (this.favoriteRooms.contains(roomId))
            return false;

        if (Emulator.getConfig().getInt("hotel.rooms.max.favorite") <= this.favoriteRooms.size())
            return false;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users_favorite_rooms (user_id, room_id) VALUES (?, ?)"))
        {
            statement.setInt(1, this.habbo.getHabboInfo().getId());
            statement.setInt(2, roomId);
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.favoriteRooms.add(roomId);
        return true;
    }

    public void removeFavoriteRoom(int roomId)
    {
        if (this.favoriteRooms.remove(roomId))
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM users_favorite_rooms WHERE user_id = ? AND room_id = ? LIMIT 1"))
            {
                statement.setInt(1, this.habbo.getHabboInfo().getId());
                statement.setInt(2, roomId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public boolean hasFavoriteRoom(int roomId)
    {
        return this.favoriteRooms.contains(roomId);
    }

    public TIntArrayList getFavoriteRooms()
    {
        return this.favoriteRooms;
    }

    public boolean hasRecipe(int id)
    {
        return this.secretRecipes.contains(id);
    }

    public boolean addRecipe(int id)
    {
        if (this.secretRecipes.contains(id))
            return false;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users_recipes (user_id, recipe) VALUES (?, ?)"))
        {
            statement.setInt(1, this.habbo.getHabboInfo().getId());
            statement.setInt(2, id);
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.secretRecipes.add(id);
        return true;
    }

    public int talentTrackLevel(TalentTrackType type)
    {
        if (type == TalentTrackType.CITIZENSHIP)
            return this.citizenshipLevel;
        else if (type == TalentTrackType.HELPER)
            return this.helpersLevel;

        return -1;
    }

    public void setTalentLevel(TalentTrackType type, int level)
    {
        if (type == TalentTrackType.CITIZENSHIP)
            this.citizenshipLevel = level;
        else if (type == TalentTrackType.HELPER)
            this.helpersLevel = level;
    }

    public int getMuteEndTime()
    {
        return this.muteEndTime;
    }

    public int addMuteTime(int seconds)
    {
        if (this.remainingMuteTime() == 0)
        {
            this.muteEndTime = Emulator.getIntUnixTimestamp();
        }

        this.mutedBubbleTracker = true;
        this.muteEndTime += seconds;

        return this.remainingMuteTime();
    }

    public int remainingMuteTime()
    {
        return Math.max(0, this.muteEndTime - Emulator.getIntUnixTimestamp());
    }

    public boolean allowTalk()
    {
        return this.remainingMuteTime() == 0;
    }

    public void unMute()
    {
        this.muteEndTime = 0;
        this.mutedBubbleTracker = false;
    }

    public void addLtdLog(int catalogItemId, int timestamp)
    {
        if (!this.ltdPurchaseLog.containsKey(catalogItemId))
        {
            this.ltdPurchaseLog.put(catalogItemId, new ArrayList<>(1));
        }

        this.ltdPurchaseLog.get(catalogItemId).add(timestamp);
    }

    public int totalLtds()
    {
        int total = 0;
        for (Map.Entry<Integer, List<Integer>> entry : this.ltdPurchaseLog.entrySet())
        {
            total += entry.getValue().size();
        }

        return total;
    }

    public int totalLtds(int catalogItemId)
    {
        if (this.ltdPurchaseLog.containsKey(catalogItemId))
        {
            return this.ltdPurchaseLog.get(catalogItemId).size();
        }

        return 0;
    }

    public void ignoreUser(int userId)
    {
        if (!this.userIgnored(userId))
        {
            this.ignoredUsers.add(userId);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO users_ignored (user_id, target_id) VALUES (?, ?)"))
            {
                statement.setInt(1, this.habbo.getHabboInfo().getId());
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public void unignoreUser(int userId)
    {
        if (this.userIgnored(userId))
        {
            this.ignoredUsers.remove(userId);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM users_ignored WHERE user_id = ? AND target_id = ?"))
            {
                statement.setInt(1, this.habbo.getHabboInfo().getId());
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public boolean userIgnored(int userId)
    {
        return this.ignoredUsers.contains(userId);
    }

    public boolean allowTrade()
    {
        if (AchievementManager.TALENTTRACK_ENABLED && RoomTrade.TRADING_REQUIRES_PERK)
            return this.perkTrade && this.allowTrade;
        else return this.allowTrade;
    }

    public void setAllowTrade(boolean allowTrade)
    {
        this.allowTrade = allowTrade;
    }

    public HabboOfferPurchase getHabboOfferPurchase(int offerId)
    {
        return this.offerCache.get(offerId);
    }

    public void addHabboOfferPurchase(HabboOfferPurchase offerPurchase)
    {
        this.offerCache.put(offerPurchase.getOfferId(), offerPurchase);
    }
}
