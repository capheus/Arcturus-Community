package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.events.guilds.GuildChangedBadgeEvent;

public class GuildChangeBadgeEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if (guild != null)
        {
            if (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_guild_admin"))
            {
                Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(guild.getRoomId());

                if (room == null || room.getId() != guild.getRoomId())
                    return;

                int count = this.packet.readInt();

                StringBuilder badge = new StringBuilder();

                byte base = 1;

                while (base < count)
                {
                    int id = this.packet.readInt();
                    int color = this.packet.readInt();
                    int pos = this.packet.readInt();

                    if (base == 1)
                    {
                        badge.append("b");
                    } else
                    {
                        badge.append("s");
                    }

                    badge.append(id < 100 ? "0" : "").append(id < 10 ? "0" : "").append(id).append(color < 10 ? "0" : "").append(color).append(pos);

                    base += 3;
                }

                if (guild.getBadge().toLowerCase().equals(badge.toString().toLowerCase()))
                    return;

                GuildChangedBadgeEvent badgeEvent = new GuildChangedBadgeEvent(guild, badge.toString());
                Emulator.getPluginManager().fireEvent(badgeEvent);

                if (badgeEvent.isCancelled())
                    return;

                guild.setBadge(badgeEvent.badge);
                guild.needsUpdate = true;

                if (Emulator.getConfig().getBoolean("imager.internal.enabled"))
                {
                    Emulator.getBadgeImager().generate(guild);
                }

                room.refreshGuild(guild);
                Emulator.getThreading().run(guild);
            }
        }
    }
}
