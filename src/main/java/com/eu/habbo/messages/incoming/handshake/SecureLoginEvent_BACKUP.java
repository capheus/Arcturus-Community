package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendsComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.habboway.nux.NewUserIdentityComposer;
import com.eu.habbo.messages.outgoing.handshake.DebugConsoleComposer;
import com.eu.habbo.messages.outgoing.handshake.SecureLoginOKComposer;
import com.eu.habbo.messages.outgoing.handshake.SessionRightsComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolComposer;
import com.eu.habbo.messages.outgoing.navigator.*;
import com.eu.habbo.messages.outgoing.users.*;
import com.eu.habbo.plugin.events.users.UserLoginEvent;

import java.util.ArrayList;

public class SecureLoginEvent_BACKUP extends MessageHandler
{


    @Override
    public void handle() throws Exception
    {

        if(!Emulator.isReady)
            return;

        String sso = this.packet.readString();

        if(this.client.getHabbo() == null)
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().loadHabbo(sso);
            if(habbo != null)
            {
                habbo.setClient(this.client);
                this.client.setHabbo(habbo);
                this.client.getHabbo().connect();
                //this.client.sendResponse(new DebugConsoleComposer());
                Emulator.getThreading().run(habbo);
                Emulator.getGameEnvironment().getHabboManager().addHabbo(habbo);

                ArrayList<ServerMessage> messages = new ArrayList<>();





                messages.add(new SecureLoginOKComposer().compose());
                messages.add(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), 0).compose());
                messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
                messages.add(new UserClubComposer(this.client.getHabbo()).compose());
                messages.add(new DebugConsoleComposer(Emulator.debugging).compose());
                messages.add(new UserAchievementScoreComposer(this.client.getHabbo()).compose());
                messages.add(new NewUserIdentityComposer(habbo).compose());
                messages.add(new UserPerksComposer(habbo).compose());
                messages.add(new SessionRightsComposer().compose());
                messages.add(new FavoriteRoomsCountComposer(habbo).compose());
                messages.add(new FriendsComposer(this.client.getHabbo()).compose());
                //messages.add(new NewUserIdentityComposer().compose());
                //messages.add(new UserDataComposer(this.client.getHabbo()).compose());
                //messages.add(new SessionRightsComposer().compose());
                //messages.add(new MinimailCountComposer().compose());
                //messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
                //messages.add(new FriendsComposer(this.client.getHabbo()).compose());

                if(this.client.getHabbo().hasPermission(Permission.ACC_SUPPORTTOOL))
                {
                    messages.add(new ModToolComposer(this.client.getHabbo()).compose());
                }

                this.client.sendResponses(messages);

                //Hardcoded
                this.client.sendResponse(new NewNavigatorMetaDataComposer());
                this.client.sendResponse(new NewNavigatorLiftedRoomsComposer());
                this.client.sendResponse(new NewNavigatorCollapsedCategoriesComposer());
                this.client.sendResponse(new NewNavigatorSavedSearchesComposer());
                this.client.sendResponse(new NewNavigatorEventCategoriesComposer());
                //this.client.sendResponse(new HotelViewComposer());
                //this.client.sendResponse(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), this.client.getHabbo().getHabboInfo().getHomeRoom()));
                //this.client.sendResponse(new UserEffectsListComposer());






                Emulator.getPluginManager().fireEvent(new UserLoginEvent(habbo, this.client.getChannel().localAddress()));

            }
            else
            {
                this.client.sendResponse(new GenericAlertComposer("Can't connect *sadpanda*"));

                this.client.getChannel().close();
            }
        }
    }
}
