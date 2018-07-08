package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.habbohotel.items.interactions.InteractionJukeBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxPlayListAddSongComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxPlayListComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxPlayListUpdatedComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxTrackDataComposer;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;

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