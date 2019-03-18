package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildMembershipRequestedComposer extends MessageComposer
{
    private final int guildId;
    private final int requester;
    private final String username;
    private final String look;
    private final HabboGender gender;

    public GuildMembershipRequestedComposer(int guildId, int requester, String username, String look, HabboGender gender)
    {
        this.guildId = guildId;
        this.requester = requester;
        this.username = username;
        this.look = look;
        this.gender = gender;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildMembershipRequestedComposer);
        this.response.appendInt(this.guildId);
        this.response.appendInt(2);
        this.response.appendInt(this.requester);
        this.response.appendString(this.username);
        this.response.appendString(this.look);
        this.response.appendString(this.gender.name());
        return this.response;
    }
}