package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.messages.incoming.MessageHandler;


public class GuildForumPostThreadEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();
        int threadId = this.packet.readInt();
        String subject = this.packet.readString();
        String message = this.packet.readString();

        //TODO: Add check if user has guild
        //TODO: Add check if threads can be posted.


//






//



//






//










    }
}