package com.eu.habbo.habbohotel.items.interactions.wired.extra;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredBlob extends InteractionDefault
{
    public WiredBlob(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredBlob(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        if (this.getExtradata().equals("0"))
        {
            Habbo habbo = room.getHabbo(roomUnit);

            if (habbo != null && habbo.getHabboInfo().getCurrentGame() != null)
            {
                int points = Emulator.getConfig().getInt("hotel.item.wiredblob." + this.getBaseItem().getName());

                if (points == 0)
                {
                    Emulator.getConfig().register("hotel.item.wiredblob." + this.getBaseItem().getName(), "3000");
                    points = 1;
                }

                boolean triggered = false;
                Game game = room.getGame(habbo.getHabboInfo().getCurrentGame());

                if (game != null)
                {
                    GameTeam team = game.getTeamForHabbo(habbo);

                    if (team != null)
                    {
                        team.addTeamScore(points);
                        triggered = true;
                    } else
                    {
                        GamePlayer player = habbo.getHabboInfo().getGamePlayer();

                        if (player != null)
                        {
                            player.addScore(points);
                            triggered = true;
                        }
                    }
                }

                if (triggered)
                {
                    this.setExtradata("1");
                    room.updateItem(this);
                }
            }
        }
    }
}
