package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleportTile;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;

class TeleportActionThree implements Runnable
{
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    public TeleportActionThree(HabboItem currentTeleport, Room room, GameClient client)
    {
        this.currentTeleport = currentTeleport;
        this.client = client;
        this.room = room;
    }

    @Override
    public void run()
    {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != this.room)
            return;

        HabboItem targetTeleport;
        Room targetRoom = this.room;

        if(this.currentTeleport.getRoomId() != ((InteractionTeleport)this.currentTeleport).getTargetRoomId())
        {
            targetRoom = Emulator.getGameEnvironment().getRoomManager().loadRoom(((InteractionTeleport) this.currentTeleport).getTargetRoomId());
        }

        if(targetRoom == null)
            return;

        if (targetRoom.isPreLoaded())
        {
            targetRoom.loadData();
        }

        targetTeleport = targetRoom.getHabboItem(((InteractionTeleport) this.currentTeleport).getTargetId());

        if(targetTeleport == null)
            return;

        this.client.getHabbo().getRoomUnit().setLocation(targetRoom.getLayout().getTile(targetTeleport.getX(), targetTeleport.getY()));
        this.client.getHabbo().getRoomUnit().getPath().clear();
        this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
        this.client.getHabbo().getRoomUnit().setZ(targetTeleport.getZ());
        this.client.getHabbo().getRoomUnit().setPreviousLocationZ(targetTeleport.getZ());
        this.client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[targetTeleport.getRotation() % 8]);

        if(targetRoom != this.room)
        {
            this.room.sendComposer(new RoomUserRemoveComposer(this.client.getHabbo().getRoomUnit()).compose());
            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), targetRoom.getId(), "", Emulator.getConfig().getBoolean("hotel.teleport.locked.allowed"));
        }

        targetTeleport.setExtradata("2");
        targetRoom.updateItem(targetTeleport);
        targetRoom.updateHabbo(this.client.getHabbo());
        System.out.println(targetTeleport.getX() + " | " + targetTeleport.getY());
        this.client.getHabbo().getHabboInfo().setCurrentRoom(targetRoom);
        //Emulator.getThreading().run(new HabboItemNewState(this.currentTeleport, this.room, "0"), 500);
        Emulator.getThreading().run(new TeleportActionFour(targetTeleport, targetRoom, this.client), this.currentTeleport instanceof InteractionTeleportTile ? 0 : 500);

    }
}
