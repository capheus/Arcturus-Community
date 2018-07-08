package com.eu.habbo.messages.outgoing;

import com.eu.habbo.messages.ServerMessage;

public class TestComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {

        this.response.init(3662);
        this.response.appendBoolean(true);
        this.response.appendString("LOL");

        return this.response;

    }
}
