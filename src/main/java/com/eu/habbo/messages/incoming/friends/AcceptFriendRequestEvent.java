package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class AcceptFriendRequestEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int count = this.packet.readInt();
        int userId;

        for(int i = 0; i < count; i++)
        {
            userId = this.packet.readInt();

            if(userId == 0)
                return;

            if(this.client.getHabbo().getMessenger().getFriends().containsKey(userId))
                continue;

            this.client.getHabbo().getMessenger().acceptFriendRequest(userId, this.client.getHabbo().getHabboInfo().getId());

            Messenger.checkFriendSizeProgress(this.client.getHabbo());

            Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if (target != null)
            {
                Messenger.checkFriendSizeProgress(target);
            }
        }
    }
}
