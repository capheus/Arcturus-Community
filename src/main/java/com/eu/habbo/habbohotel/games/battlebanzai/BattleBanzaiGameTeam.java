package com.eu.habbo.habbohotel.games.battlebanzai;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;

public class BattleBanzaiGameTeam extends GameTeam
{
    public BattleBanzaiGameTeam(GameTeamColors teamColor)
    {
        super(teamColor);
    }

    @Override
    public void addMember(GamePlayer gamePlayer)
    {
        super.addMember(gamePlayer);

        gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(gamePlayer.getHabbo(), BattleBanzaiGame.effectId + this.teamColor.type, -1);
    }

    @Override
    public void removeMember(GamePlayer gamePlayer)
    {
        Game game = gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().getGame(gamePlayer.getHabbo().getHabboInfo().getCurrentGame());
        if(game instanceof BattleBanzaiGame)
        {
            ((BattleBanzaiGame) game).addPositionToGate(gamePlayer.getTeamColor());
        }

        gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(gamePlayer.getHabbo(), 0, -1);
        gamePlayer.getHabbo().getRoomUnit().setCanWalk(true);

        super.removeMember(gamePlayer);
    }
}
