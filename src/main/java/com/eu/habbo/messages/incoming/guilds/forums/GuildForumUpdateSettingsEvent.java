package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.SettingsState;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;

public class GuildForumUpdateSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();
        int canRead = this.packet.readInt();
        int postMessages = this.packet.readInt();
        int postThreads = this.packet.readInt();
        int modForum = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if(guild == null || guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId())
            return;

        this.client.sendResponse(new GuildForumDataComposer(Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(1), this.client.getHabbo()));

        guild.setReadForum(SettingsState.fromValue(canRead));
        guild.setPostMessages(SettingsState.fromValue(postMessages));
        guild.setPostThreads(SettingsState.fromValue(postThreads));
        guild.setModForum(SettingsState.fromValue(modForum));
        guild.needsUpdate = true;
        Emulator.getThreading().run(guild);




        //TODO: DATABASE SAVING, PERMISSION CHECK
    }
}