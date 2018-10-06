package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RoomUserGiveRespectEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int userId = this.packet.readInt();

        if(this.client.getHabbo().getHabboStats().respectPointsToGive > 0)
        {
            Habbo target = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(userId);

            this.client.getHabbo().respect(target);
        }
    }
}
