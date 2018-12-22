package com.eu.habbo.core.consolecommands;


import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

public class ConsoleTestCommand extends ConsoleCommand
{
    public ConsoleTestCommand()
    {
        super("test", "This is just a test.");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        if (Emulator.debugging)
        {
            System.out.println("This is a test command for live debugging.");




            //AchievementManager.progressAchievement(4, Emulator.getGameEnvironment().getAchievementManager().getAchievement("AllTimeHotelPresence"), 30);
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(1);
            habbo.getHabboInfo().getMachineID();
        }
    }
}