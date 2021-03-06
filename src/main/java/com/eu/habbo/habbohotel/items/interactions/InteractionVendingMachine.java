package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitVendingMachineAction;
import com.eu.habbo.util.pathfinding.Rotation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionVendingMachine extends HabboItem
{
    public InteractionVendingMachine(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.setExtradata("0");
    }

    public InteractionVendingMachine(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if (client != null)
        {
            RoomTile tile = getSquareInFront(room.getLayout(), this);

            if (tile != null)
            {
                if (tile.equals(client.getHabbo().getRoomUnit().getCurrentLocation()))
                {
                    if (this.getExtradata().equals("0") || this.getExtradata().length() == 0)
                    {
                        room.updateHabbo(client.getHabbo());
                        if (!client.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.SIT))
                        {
                            client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[Rotation.Calculate(client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY(), this.getX(), this.getY())]);
                            client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
                            room.scheduledComposers.add(new RoomUserStatusComposer(client.getHabbo().getRoomUnit()).compose());
                        }
                        this.setExtradata("1");
                        room.scheduledComposers.add(new FloorItemUpdateComposer(this).compose());
                        Emulator.getThreading().run(this, 1000);
                        Emulator.getThreading().run(new RoomUnitGiveHanditem(client.getHabbo().getRoomUnit(), room, this.getBaseItem().getRandomVendingItem()));

                        if (this.getBaseItem().getEffectM() > 0 && client.getHabbo().getHabboInfo().getGender() == HabboGender.M) room.giveEffect(client.getHabbo(), this.getBaseItem().getEffectM(), -1);
                        if (this.getBaseItem().getEffectF() > 0 && client.getHabbo().getHabboInfo().getGender() == HabboGender.F) room.giveEffect(client.getHabbo(), this.getBaseItem().getEffectF(), -1);
                    }
                }
                else
                {
                    if (!tile.isWalkable() && tile.state != RoomTileState.SIT && tile.state != RoomTileState.LAY)
                    {
                        for (RoomTile t : room.getLayout().getTilesAround(room.getLayout().getTile(this.getX(), this.getY())))
                        {
                            if (t != null && t.isWalkable())
                            {
                                tile = t;
                                break;
                            }
                        }
                    }
                    client.getHabbo().getRoomUnit().setGoalLocation(tile);
                    Emulator.getThreading().run(new RoomUnitVendingMachineAction(client.getHabbo(), this, room), client.getHabbo().getRoomUnit().getPath().size() + 2 * 510);
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void run()
    {
        super.run();
        if(this.getExtradata().equals("1"))
        {
            this.setExtradata("0");
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            if(room != null)
            {
                room.updateItem(this);
            }
        }
    }


    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean isUsable()
    {
        return true;
    }
}
