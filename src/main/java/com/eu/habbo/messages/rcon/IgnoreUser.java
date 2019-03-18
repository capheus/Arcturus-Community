package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IgnoreUser extends RCONMessage<IgnoreUser.JSONIgnoreUser>
{
    public IgnoreUser()
    {
        super(JSONIgnoreUser.class);
    }

    @Override
    public void handle(Gson gson, JSONIgnoreUser object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);

        if (habbo != null)
        {
            habbo.getHabboStats().ignoreUser(object.target_id);
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users_ignored (user_id, target_id) VALUES (?, ?)"))
            {
                statement.setInt(1, object.user_id);
                statement.setInt(2, object.target_id);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            this.message = "offline";
        }
    }

    static class JSONIgnoreUser
    {

        public int user_id;

        public int target_id;
    }
}