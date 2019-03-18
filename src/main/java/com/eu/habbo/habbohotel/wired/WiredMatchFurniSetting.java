package com.eu.habbo.habbohotel.wired;

public class WiredMatchFurniSetting
{
    public final int itemId;
    public final String state;
    public final int rotation;
    public final int x;
    public final int y;

    public WiredMatchFurniSetting(int itemId, String state, int rotation, int x, int y)
    {
        this.itemId = itemId;
        this.state = state.replace("\t\t\t", " ");
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString()
    {
        return this.toString(true);
    }

    public String toString(boolean includeState)
    {
        return this.itemId + "-" + (this.state.isEmpty() || !includeState ? " " : this.state) + "-" + this.rotation + "-" + this.x + "-" + this.y;
    }

}
