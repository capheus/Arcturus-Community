package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BotSettingsComposer extends MessageComposer
{
    private final Bot bot;
    private final int settingId;

    public BotSettingsComposer(Bot bot, int settingId)
    {
        this.bot = bot;
        this.settingId = settingId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BotSettingsComposer);
        this.response.appendInt(-this.bot.getId());
        this.response.appendInt(this.settingId);

        switch(this.settingId)
        {
            case 1: this.response.appendString(""); break;
            case 2:
                String data = "";

                if (this.bot.hasChat())
                {
                    for (String s : this.bot.getChatLines())
                    {
                        data += s + "\r";
                    }
                }
                else
                {
                    data += Bot.NO_CHAT_SET;
                }


                data += ";#;" + (this.bot.isChatAuto() ? "true" : "false");
                data += ";#;" + this.bot.getChatDelay();
                data += ";#;" + (this.bot.isChatRandom() ? "true" : "false");
                this.response.appendString(data);
                break;
            case 3: this.response.appendString(""); break;
            case 4: this.response.appendString(""); break;
            case 5:
                this.response.appendString(bot.getName());
                break;
            case 6: this.response.appendString(""); break;
            case 9:
                this.response.appendString(bot.getMotto());
                break;
        }
        return this.response;
    }
}
