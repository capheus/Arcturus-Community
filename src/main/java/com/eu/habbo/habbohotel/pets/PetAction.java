package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.ArrayList;
import java.util.List;

public abstract class PetAction
{

    public int minimumActionDuration = 500;


    public final PetTasks petTask;


    public final boolean stopsPetWalking;


    public final List<RoomUnitStatus> statusToRemove = new ArrayList<>();


    public String gestureToSet                       = null;


    public final List<RoomUnitStatus> statusToSet    = new ArrayList<>();

    protected PetAction(PetTasks petTask, boolean stopsPetWalking)
    {
        this.petTask         = petTask;
        this.stopsPetWalking = stopsPetWalking;
    }


    public abstract boolean apply(Pet pet, Habbo habbo, String[] data);
}
