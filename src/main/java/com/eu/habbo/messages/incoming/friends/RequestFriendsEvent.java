package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendsComposer;
import com.eu.habbo.messages.outgoing.friends.MessengerInitComposer;

public class RequestFriendsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {


        //this.client.sendResponse(new FriendsComposer(this.client.getHabbo()));

        //this.client.sendResponse(new MessengerInitComposer(this.client.getHabbo()));
    }
}
