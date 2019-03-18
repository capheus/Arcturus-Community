package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Message;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FriendChatMessageComposer extends MessageComposer
{
    private final Message message;

    public FriendChatMessageComposer(Message message)
    {
        this.message = message;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FriendChatMessageComposer);

        if (this.message.getToId() == -1)
        {
            this.response.appendInt(-1);
        }
        else
        {
            this.response.appendInt(this.message.getFromId());
        }
        this.response.appendString(this.message.getMessage());
        this.response.appendInt(Emulator.getIntUnixTimestamp() - this.message.getTimestamp());

        if (this.message.getToId() == -1) //TO Staff Chat
        {
            String name = "AUTO_MODERATOR";
            String look = "lg-5635282-1193.hd-3091-1.sh-3089-73.cc-156282-64.hr-831-34.ha-1012-1186.ch-3050-62-62";
            if (this.message.getFromId() != -1)
            {
                Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.message.getFromId());

                if (habbo != null)
                {
                    name = habbo.getHabboInfo().getUsername();
                    look = habbo.getHabboInfo().getLook();
                }
                else
                {
                    name = "UNKNOWN";
                }
            }
            this.response.appendString(name + "/" + look + "/" + this.message.getFromId());
        }

        return this.response;
    }
}
