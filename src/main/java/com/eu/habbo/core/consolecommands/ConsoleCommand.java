package com.eu.habbo.core.consolecommands;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public abstract class ConsoleCommand
{

    public static THashMap<String, ConsoleCommand> commands = new THashMap<String, ConsoleCommand>();


    public final String key;


    public final String usage;


    public ConsoleCommand(String key, String usage)
    {
        this.key    = key;
        this.usage  = usage;
    }


    public static void load()
    {
        addCommand(new ConsoleShutdownCommand());
        addCommand(new ConsoleInfoCommand());
        addCommand(new ConsoleTestCommand());
        addCommand(new ConsoleReconnectCameraCommand());
    }


    public abstract void handle(String[] args) throws Exception;


    public static void addCommand(ConsoleCommand command)
    {
        commands.put(command.key, command);
    }


    public static ConsoleCommand findCommand(String key)
    {
        return commands.get(key);
    }


    public static boolean handle(String line)
    {
        String[] message = line.split(" ");

        if (message.length > 0)
        {
            ConsoleCommand command = ConsoleCommand.findCommand(message[0]);

            if (command != null)
            {
                try
                {
                    command.handle(message);
                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Unknown Console Command " + message[0]);
                System.out.println("Commands Available (" + commands.size() + "): ");

                for (ConsoleCommand c : commands.values())
                {
                    System.out.println(c.key + " " + c.usage);
                }
            }
        }

        return false;
    }
}