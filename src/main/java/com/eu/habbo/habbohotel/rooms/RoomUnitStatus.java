package com.eu.habbo.habbohotel.rooms;

public enum RoomUnitStatus
{
    MOVE("mv", true),
    SIT("sit", true),
    LAY("lay", true),
    FLAT_CONTROL("flatctrl"),
    SIGN("sign"),
    GESTURE("gst"),
    WAVE("wav"),
    TRADING("trd"),

    DIP("dip"),
    EAT("eat"),
    BEG("beg", true),
    DEAD("ded", true),
    JUMP("jmp", true),
    PLAY("pla", true),
    SPEAK("spk"),
    CROAK("crk"),
    RELAX("rlx"),
    WINGS("wng", true),
    FLAME("flm"),
    RIP("rip"),
    GROW("grw"),
    GROW_1("grw1"),
    GROW_2("grw2"),
    GROW_3("grw3"),
    GROW_4("grw4"),
    GROW_5("grw5"),
    GROW_6("grw6"),
    GROW_7("grw7"),
    LAY_IN("lay-in"),
    LAY_OUT("lay-out"),
    KICK("kck"),
    WAG_TAIL("wag"),
    JUMP_IN("jmp-in"),
    JUMP_OUT("jmp-out");

    public final String key;
    public final boolean removeWhenWalking;

    RoomUnitStatus(String key)
    {
        this.key = key;
        this.removeWhenWalking = false;
    }

    RoomUnitStatus(String key, boolean removeWhenWalking)
    {
        this.key = key;
        this.removeWhenWalking = removeWhenWalking;
    }

    @Override
    public String toString()
    {
        return this.key;
    }

    public static RoomUnitStatus fromString(String key)
    {
        for (RoomUnitStatus status : values())
        {
            if (status.key.equalsIgnoreCase(key))
            {
                return status;
            }
        }

        return null;
    }
}
