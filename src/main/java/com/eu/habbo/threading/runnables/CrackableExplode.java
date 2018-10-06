package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionCrackable;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.items.AddFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;

public class CrackableExplode implements Runnable
{
    private final Room room;
    private final InteractionCrackable habboItem;
    private final Habbo habbo;
    private final boolean toInventory;

    public CrackableExplode(Room room, InteractionCrackable item, Habbo habbo, boolean toInventory)
    {
        this.room = room;
        this.habboItem = item;
        this.habbo = habbo;
        this.toInventory = toInventory;
    }

    @Override
    public void run()
    {
        if (habboItem.getRoomId() == 0)
        {
            return;
        }

        if (!this.habboItem.resetable())
        {
            this.room.removeHabboItem(this.habboItem);
            this.room.sendComposer(new RemoveFloorItemComposer(this.habboItem).compose());
            this.habboItem.setRoomId(0);
            Emulator.getGameEnvironment().getItemManager().deleteItem(this.habboItem);
        }
        else
        {
            this.habboItem.reset(this.room);
        }
        HabboItem newItem = Emulator.getGameEnvironment().getItemManager().createItem(this.habboItem.allowAnyone() ? habbo.getHabboInfo().getId() : this.habboItem.getUserId(), Emulator.getGameEnvironment().getItemManager().getCrackableReward(this.habboItem.getBaseItem().getId()), 0, 0, "");

        if (newItem != null)
        {
            if (this.toInventory)
            {
                habbo.getInventory().getItemsComponent().addItem(newItem);
                habbo.getClient().sendResponse(new AddHabboItemComposer(newItem));
                habbo.getClient().sendResponse(new InventoryRefreshComposer());
            }
            else
            {
                newItem.setX(this.habboItem.getX());
                newItem.setY(this.habboItem.getY());
                newItem.setZ(this.habboItem.getZ());
                newItem.setRoomId(this.room.getId());
                newItem.needsUpdate(true);
                this.room.addHabboItem(newItem);
                this.room.sendComposer(new AddFloorItemComposer(newItem, this.room.getFurniOwnerNames().get(newItem.getUserId())).compose());
            }
        }
    }
}
