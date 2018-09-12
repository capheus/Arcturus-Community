package com.eu.habbo.core.consolecommands;


import com.eu.habbo.Emulator;
import com.eu.habbo.messages.PacketManager;

public class ConsoleTestCommand extends ConsoleCommand
{
    public ConsoleTestCommand()
    {
        super("test", "test");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        System.out.println("This is a test command for live debugging.");

        PacketManager.DEBUG_SHOW_PACKETS = true;
        Emulator.getConfig().update("debug.show.packets", "1");

    }
}