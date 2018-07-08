package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

import java.net.SocketAddress;

public class UserLoginEvent extends UserEvent
{

    public final SocketAddress ip;


    public UserLoginEvent(Habbo habbo, SocketAddress ip)
    {
        super(habbo);

        this.ip = ip;
    }
}
