package com.eu.habbo.habbohotel.rooms;

public class RoomTile
{
    public final short x;
    public final short y;
    public final short z;
    public RoomTileState state;

    private double stackHeight;
    private boolean allowStack = true;


    private RoomTile previous = null;
    private boolean diagonally;
    private short gCosts;
    private short hCosts;


    public RoomTile(short x, short y, short z, RoomTileState state, boolean allowStack)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.stackHeight = z;
        this.state = state;
        this.setAllowStack(allowStack);
    }

    public RoomTile(RoomTile tile)
    {
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.z;
        this.stackHeight = tile.stackHeight;
        this.state = tile.state;
        this.allowStack = tile.allowStack;
        this.diagonally = tile.diagonally;
        this.gCosts = tile.gCosts;
        this.hCosts = tile.hCosts;

        if (this.state == RoomTileState.INVALID)
        {
            this.allowStack = false;
        }
    }

    public double getStackHeight()
    {
        return this.stackHeight;
    }

    public void setStackHeight(double stackHeight)
    {
        if (this.state == RoomTileState.INVALID)
        {
            this.stackHeight = Short.MAX_VALUE;
            this.allowStack = false;
            return;
        }

        if (stackHeight >= 0 && stackHeight != Short.MAX_VALUE)
        {
            this.stackHeight = stackHeight;
            this.allowStack = true;
        }
        else
        {
            this.allowStack = false;
            this.stackHeight = this.z;
        }
    }

    public boolean getAllowStack()
    {
        if (this.state == RoomTileState.INVALID)
        {
            return false;
        }

        return this.allowStack;
    }

    public void setAllowStack(boolean allowStack)
    {
        this.allowStack = allowStack;
    }

    public short relativeHeight()
    {
        if (this.state == RoomTileState.INVALID)
        {
            return Short.MAX_VALUE;
        }
        else if (!this.allowStack && (this.state == RoomTileState.BLOCKED || this.state == RoomTileState.SIT))
        {
            return 64 * 256;
        }

        return this.allowStack ? (short) (this.getStackHeight() * 256.0) : 64 * 256;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof RoomTile       &&
                ((RoomTile) o).x == this.x &&
                ((RoomTile) o).y == this.y;
    }

    public RoomTile copy()
    {
        return new RoomTile(this);
    }

    public double distance(RoomTile roomTile)
    {
        double x = this.x - roomTile.x;
        double y = this.y - roomTile.y;
        return Math.sqrt(x * x + y * y);
    }

    public void isDiagonally(boolean isDiagonally)
    {
        this.diagonally = isDiagonally;
    }

    public RoomTile getPrevious()
    {
        return this.previous;
    }

    public void setPrevious(RoomTile previous)
    {
        this.previous = previous;
    }

    public int getfCosts()
    {
        return this.gCosts + this.hCosts;
    }

    public int getgCosts()
    {
        return this.gCosts;
    }

    private void setgCosts(short gCosts)
    {
        this.gCosts = gCosts;
    }

    void setgCosts(RoomTile previousRoomTile, int basicCost)
    {
        this.setgCosts((short)(previousRoomTile.getgCosts() + basicCost));
    }

    public void setgCosts(RoomTile previousRoomTile)
    {
        this.setgCosts(previousRoomTile, this.diagonally ? RoomLayout.DIAGONALMOVEMENTCOST : RoomLayout.BASICMOVEMENTCOST);
    }

    public int calculategCosts(RoomTile previousRoomTile)
    {
        if (this.diagonally)
        {
            return previousRoomTile.getgCosts() + 14;
        }

        return previousRoomTile.getgCosts() + 10;
    }

    public void sethCosts(RoomTile parent)
    {
        this.hCosts = (short)((Math.abs(this.x - parent.x) + Math.abs(this.y - parent.y)) * (parent.diagonally ? RoomLayout.DIAGONALMOVEMENTCOST : RoomLayout.BASICMOVEMENTCOST));
    }

    public String toString()
    {
        return "RoomTile (" + this.x + ", " + this.y + ", " + this.z + "): h: " + this.hCosts + " g: " + this.gCosts + " f: " + this.getfCosts();
    }

    public boolean isWalkable()
    {
        return this.state == RoomTileState.OPEN;
    }

    public RoomTileState getState()
    {
        return this.state;
    }

    public void setState(RoomTileState state)
    {
        this.state = state;
    }

    public boolean is(short x, short y)
    {
        return this.x == x && this.y == y;
    }
}