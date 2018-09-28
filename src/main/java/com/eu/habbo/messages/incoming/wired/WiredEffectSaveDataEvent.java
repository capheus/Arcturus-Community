package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.wired.WiredSavedComposer;

public class WiredEffectSaveDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room != null)
        {
            if(room.hasRights(this.client.getHabbo()) || room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermission("acc_moverotate"))
            {
                InteractionWiredEffect effect = room.getRoomSpecialTypes().getEffect(itemId);

                if(effect != null)
                {
                    if(effect.saveData(this.packet, this.client))
                    {
                        this.client.sendResponse(new WiredSavedComposer());
                        effect.needsUpdate(true);
                        Emulator.getThreading().run(effect);
                    }
                }
            }
        }
    }
}
