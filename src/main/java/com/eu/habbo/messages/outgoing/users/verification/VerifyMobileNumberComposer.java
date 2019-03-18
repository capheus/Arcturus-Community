package com.eu.habbo.messages.outgoing.users.verification;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class VerifyMobileNumberComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.VerifyMobileNumberComposer);
        return this.response;
    }
}