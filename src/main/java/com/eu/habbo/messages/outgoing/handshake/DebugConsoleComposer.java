package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class DebugConsoleComposer extends MessageComposer
{
    private final boolean debugging;

    public DebugConsoleComposer(boolean debugging)
    {
        this.debugging = debugging;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.DebugConsoleComposer);
        this.response.appendBoolean(this.debugging);
        return this.response;
    }
}
