package com.eu.habbo.habbohotel.games;

public enum GameTeamColors
{

    RED(0),


    GREEN(1),


    BLUE(2),


    YELLOW(3),


    NONE(4),

    ONE(5),
    TWO(6),
    THREE(7),
    FOUR(8),
    FIVE(9),
    SIX(10),
    SEVEN(11),
    EIGHT(12),
    NINE(13),
    TEN(14);


    public final int type;

    GameTeamColors(int type)
    {
        this.type = type;
    }

    public static GameTeamColors fromType(int type)
    {
        for (GameTeamColors teamColors : values())
        {
            if (teamColors.type == type)
            {
                return teamColors;
            }
        }

        return RED;
    }
}
