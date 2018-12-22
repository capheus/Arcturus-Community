package com.eu.habbo.core.consolecommands;

import com.eu.habbo.Emulator;

public class ShowInteractionsCommand extends ConsoleCommand
{
    public ShowInteractionsCommand()
    {
        super("interactions", "Show a list of available furniture interactions.");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        for (String interaction : Emulator.getGameEnvironment().getItemManager().getInteractionList())
        {
            System.out.println(interaction);
        }
    }
}