package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.incoming.MessageHandler;

public class GuildForumThreadsMessagesEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = packet.readInt();
        int threadId = packet.readInt();
        int index = packet.readInt(); //40
        int limit = packet.readInt(); //20

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if(guild == null)
            return;

        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);

//
//







//

    }
}