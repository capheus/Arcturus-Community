package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.plugin.HabboPlugin;

public class PluginsCommand extends Command
{
    public PluginsCommand()
    {
        super("cmd_plugins", Emulator.getTexts().getValue("commands.keys.cmd_plugins").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        StringBuilder message = new StringBuilder("Plugins (" + Emulator.getPluginManager().getPlugins().size() + ")\r");

        for (HabboPlugin plugin : Emulator.getPluginManager().getPlugins())
        {
            message.append("\r").append(plugin.configuration.name).append(" By ").append(plugin.configuration.author);
        }

        gameClient.getHabbo().alert(message.toString());

        return true;
    }
}
