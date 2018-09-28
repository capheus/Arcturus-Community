package com.eu.habbo.habbohotel.games;

import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.set.hash.THashSet;

public class GameTeam
{

    private final THashSet<GamePlayer> members;


    public final GameTeamColors teamColor;


    private int teamScore;


    public GameTeam(GameTeamColors teamColor)
    {
        this.teamColor = teamColor;

        this.members = new THashSet<>();
    }


    public void initialise()
    {
        for(GamePlayer player : this.members)
        {
            player.reset();
        }

        this.teamScore = 0;
    }


    public void reset()
    {
        this.members.clear();
    }


    public void addTeamScore(int teamScore)
    {
        this.teamScore += teamScore;
    }


    public int getTeamScore()
    {
        return this.teamScore;
    }


    public synchronized int getTotalScore()
    {
        int score = this.teamScore;

        for(GamePlayer player : this.members)
        {
            score += player.getScore();
        }

        return score;
    }


    public void addMember(GamePlayer gamePlayer)
    {
        synchronized (this.members)
        {
            this.members.add(gamePlayer);
        }
    }


    public void removeMember(GamePlayer gamePlayer)
    {
        synchronized (this.members)
        {
            this.members.remove(gamePlayer);
        }
    }


    public THashSet<GamePlayer> getMembers()
    {
        return this.members;
    }


    public boolean isMember(Habbo habbo)
    {
        for(GamePlayer p : this.members)
        {
            if(p.getHabbo().equals(habbo))
            {
                return true;
            }
        }

        return false;
    }


    @Deprecated
    public GamePlayer getPlayerForHabbo(Habbo habbo)
    {
        for(GamePlayer p : this.members)
        {
            if(p.getHabbo().equals(habbo))
            {
                return p;
            }
        }

        return null;
    }
}
