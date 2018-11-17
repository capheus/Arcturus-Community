package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserNameChangedEvent extends UserEvent
{
    private final String oldName;

    public UserNameChangedEvent(Habbo habbo, String oldName)
    {
        super(habbo);

        this.oldName = oldName;
    }
}