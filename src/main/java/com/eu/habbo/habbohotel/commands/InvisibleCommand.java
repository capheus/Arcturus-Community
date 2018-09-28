package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;

public class InvisibleCommand extends Command
{
    public InvisibleCommand()
    {
        super("cmd_invisible",  Emulator.getTexts().getValue("commands.keys.cmd_invisible").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        RoomUnit roomUnit = gameClient.getHabbo().getRoomUnit();

        roomUnit.setInvisible(true);

        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_invisible.updated"));

        gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserRemoveComposer(roomUnit).compose());

        return true;
    }
}
