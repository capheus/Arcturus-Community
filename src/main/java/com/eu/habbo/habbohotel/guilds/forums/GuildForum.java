package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;

import java.sql.*;

public class GuildForum
{
    private final int guild;
    private int lastRequested = Emulator.getIntUnixTimestamp();
    private GuildForumComment lastComment = null;

    public GuildForum(int guild)
    {
        this.guild = guild;
    }

    public GuildForumThread createThread(Habbo habbo, String subject, String message)
    {
        int timestamp = Emulator.getIntUnixTimestamp();
        GuildForumThread thread = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guilds_forums (guild_id, user_id, subject, message, timestamp) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, this.guild);
            statement.setInt(2, habbo.getClient().getHabbo().getHabboInfo().getId());
            statement.setString(3, subject);
            statement.setString(4, message);
            statement.setInt(5, timestamp);
            statement.execute();

            try (ResultSet set = statement.getGeneratedKeys())
            {
                if (set.next())
                {
                    return thread = new GuildForumThread(habbo, set.getInt(1), this.guild, subject, message, timestamp);
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return thread;
    }

    public int getGuild()
    {
        return this.guild;
    }

    public int getLastRequestedTime()
    {
        return this.lastRequested;
    }

    public void serializeThreads(final ServerMessage message)
    {





    }

    public int threadSize()
    {





        return 0;
    }

    public void updateLastRequested()
    {
        this.lastRequested = Emulator.getIntUnixTimestamp();
    }

    public enum ThreadState
    {


        OPEN(0),
        CLOSED(1),
        HIDDEN_BY_ADMIN(10), //DELETED
        HIDDEN_BY_STAFF(20);

        public final int state;

        ThreadState(int state)
        {
            this.state = state;
        }

        public static ThreadState fromValue(int state)
        {
            switch (state)
            {
                case 0: return OPEN;
                case 1: return CLOSED;
                case 10: return HIDDEN_BY_ADMIN;
                case 20: return HIDDEN_BY_STAFF;
            }

            return OPEN;
        }
    }

    public void serializeUserForum(ServerMessage response, Habbo habbo)
    {
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild);
        response.appendInt(guild.getId()); //k._SafeStr_6864 = _arg_2._SafeStr_5878();   = guild_id
        response.appendString(guild.getName()); //k._name = _arg_2.readString();            = name
        response.appendString(guild.getDescription()); //k._SafeStr_5790 = _arg_2.readString();    = description
        response.appendString(guild.getBadge()); //k._icon = _arg_2.readString();            = icon
        response.appendInt(0); //k._SafeStr_11338 = _arg_2._SafeStr_5878(); (?)
        response.appendInt(0); //k._SafeStr_19191 = _arg_2._SafeStr_5878();  = rating
        response.appendInt(0); //k._SafeStr_11328 = _arg_2._SafeStr_5878();  = total_messages
        response.appendInt(0); //k._SafeStr_19192 = _arg_2._SafeStr_5878();  = new_messages

        if (this.lastComment != null)
        {
            response.appendInt(this.lastComment.getThreadId()); //k._SafeStr_19193 = _arg_2._SafeStr_5878(); (?)
            response.appendInt(this.lastComment.getUserId()); //k._SafeStr_19194 = _arg_2._SafeStr_5878();  = last_author_id
            response.appendString(this.lastComment.getUserName()); //k._SafeStr_19195 = _arg_2.readString();   = last_author_name
            response.appendInt(this.lastComment.getTimestamp()); //k._SafeStr_19196 = _arg_2._SafeStr_5878();  = update_time
        }
    }
}