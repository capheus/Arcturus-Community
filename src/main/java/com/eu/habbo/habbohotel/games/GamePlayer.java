package com.eu.habbo.habbohotel.games;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

public class GamePlayer
{

    private final Habbo habbo;


    private GameTeamColors teamColor;


    private int score;


    public GamePlayer(Habbo habbo, GameTeamColors teamColor)
    {
        this.habbo = habbo;
        this.teamColor = teamColor;
    }


    public void reset()
    {
        this.score = 0;
    }


    public synchronized void addScore(int amount)
    {
        this.score += amount;
        WiredHandler.handle(WiredTriggerType.SCORE_ACHIEVED, null, this.habbo.getHabboInfo().getCurrentRoom(), new Object[]{this.habbo.getHabboInfo().getCurrentRoom().getGame(this.habbo.getHabboInfo().getCurrentGame()).getTeamForHabbo(this.habbo).getTotalScore(), amount});
    }


    public Habbo getHabbo()
    {
        return this.habbo;
    }


    public GameTeamColors getTeamColor()
    {
        return this.teamColor;
    }


    public int getScore()
    {
        return this.score;
    }
}
