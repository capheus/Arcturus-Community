package com.eu.habbo.core.consolecommands;

import com.eu.habbo.Emulator;

public class ShowRCONCommands extends ConsoleCommand
{
    public ShowRCONCommands()
    {
        super("rconcommands", "Show a list of all RCON commands");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        for (String command : Emulator.getRconServer().getCommands())
        {
            System.out.println(command);
        }
    }
}
