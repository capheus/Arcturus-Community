package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class JukeBoxAddSoundTrackEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();
        int unknown = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null)
        {
            HabboItem item = room.getHabboItem(itemId);

            if (item instanceof InteractionMusicDisc)
            {


                if (this.client.getHabbo().getHabboInfo().getCurrentRoom().hasRights(this.client.getHabbo()))
                {
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().getTraxManager().addSong(itemId);
                }
            }
        }
    }
}