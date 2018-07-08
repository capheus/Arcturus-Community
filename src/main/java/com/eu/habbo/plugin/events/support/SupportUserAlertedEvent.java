package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.users.Habbo;

public class SupportUserAlertedEvent extends SupportEvent
{

    public Habbo target;


    public String message;


    public SupportUserAlertedEvent(Habbo moderator, Habbo target, String message)
    {
        super(moderator);

        this.message = message;
        this.target  = target;
    }
}