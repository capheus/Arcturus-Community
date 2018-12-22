package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.events.calendar.AdventCalendarDataComposer;
import com.eu.habbo.messages.outgoing.habboway.nux.NuxAlertComposer;

public class CalendarCommand extends Command
{
    public CalendarCommand()
    {
        super("cmd_calendar", Emulator.getTexts().getValue("commands.keys.cmd_calendar").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if (Emulator.getConfig().getBoolean("hotel.calendar.enabled"))
        {
            gameClient.sendResponse(new AdventCalendarDataComposer("xmas11", Emulator.getGameEnvironment().getCatalogManager().calendarRewards.size(), (int)Math.floor((Emulator.getIntUnixTimestamp() - Emulator.getConfig().getInt("hotel.calendar.starttimestamp")) / 86400) , gameClient.getHabbo().getHabboStats().calendarRewardsClaimed, true));
            gameClient.sendResponse(new NuxAlertComposer("openView/calendar"));
        }

        return true;
    }
}
