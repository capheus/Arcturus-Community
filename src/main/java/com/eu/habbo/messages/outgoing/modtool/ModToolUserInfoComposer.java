package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModToolUserInfoComposer extends MessageComposer
{
    private final ResultSet set;

    public ModToolUserInfoComposer(ResultSet set)
    {
        this.set = set;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolUserInfoComposer);
        try
        {
            this.response.appendInt(this.set.getInt("user_id"));
            this.response.appendString(this.set.getString("username"));
            this.response.appendString(this.set.getString("look"));
            this.response.appendInt((Emulator.getIntUnixTimestamp() - this.set.getInt("account_created")) / 60);
            this.response.appendInt((this.set.getInt("online") == 1 ? 0 : Emulator.getIntUnixTimestamp() - this.set.getInt("last_online")) / 60);
            this.response.appendBoolean(this.set.getInt("online") == 1);
            this.response.appendInt(this.set.getInt("cfh_send"));
            this.response.appendInt(this.set.getInt("cfh_abusive"));
            this.response.appendInt(this.set.getInt("cfh_warnings"));
            this.response.appendInt(this.set.getInt("cfh_bans"));
            this.response.appendInt(0); //Trading lock count
            this.response.appendString(""); //Trading lock expiry timestamp
            this.response.appendString(""); //Last Purchase Timestamp
            this.response.appendInt(this.set.getInt("user_id")); //Personal Identification #
            this.response.appendInt(0); //Number of bans
            this.response.appendString(this.set.getString("mail"));
            this.response.appendString("Rank (" + this.set.getInt("rank_id") + "): " + this.set.getString("rank_name")); //user_class_txt

            if (false) //has sanction
            {
                this.response.appendString("SOME SANCTION");
                this.response.appendInt(31);
            }
            return this.response;
        }
        catch (SQLException e)
        {
			Emulator.getLogging().logSQLException(e);
        }
        return null;
    }
}
