package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.incoming.MessageHandler;


public class GuildForumModerateThreadEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();
        int threadId = this.packet.readInt();
        int state = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if(guild == null || guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId())
            return;

        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);





//

    }
}