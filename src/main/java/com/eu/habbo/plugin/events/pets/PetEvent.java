package com.eu.habbo.plugin.events.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.plugin.Event;

public abstract class PetEvent extends Event
{

    public final AbstractPet pet;


    public PetEvent(AbstractPet pet)
    {
        this.pet = pet;
    }
}