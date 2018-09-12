package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RoomUserSignEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int signId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        this.client.getHabbo().getRoomUnit().setStatus(RoomUnitStatus.SIGN, signId + "");
        this.client.getHabbo().getHabboInfo().getCurrentRoom().unIdle(this.client.getHabbo());
    }
}
