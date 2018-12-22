package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.users.MeMenuSettingsComposer;
import com.eu.habbo.messages.outgoing.users.UserDataComposer;
import com.eu.habbo.messages.outgoing.users.UserPerksComposer;

import java.util.ArrayList;

public class RequestUserDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (this.client.getHabbo() != null)
        {
            //this.client.sendResponse(new TestComposer());

            //this.client.sendResponse(new UserDataComposer(this.client.getHabbo()));
            //this.client.sendResponse(new HotelViewComposer());
            //this.client.sendResponse(new UserHomeRoomComposer());
            //this.client.sendResponse(new UserPermissionsComposer(this.client.getHabbo()));

            //this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));
            //this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));
            //this.client.sendResponse(new FavoriteRoomsCountComposer());

            //this.client.sendResponse(new UserAchievementScoreComposer(this.client.getHabbo()));
            //this.client.sendResponse(new UserClothesComposer());
            //this.client.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("hotel.alert.message.welcome").replace("%user%", this.client.getHabbo().getHabboInfo().getUsername()), this.client.getHabbo()));


            //

            ArrayList<ServerMessage> messages = new ArrayList<>();




















            messages.add(new UserDataComposer(this.client.getHabbo()).compose());
            messages.add(new UserPerksComposer(this.client.getHabbo()).compose());

            if(this.client.getHabbo().getHabboInfo().getHomeRoom() != 0)
                messages.add(new ForwardToRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom()).compose());
            else if (RoomManager.HOME_ROOM_ID > 0)
                messages.add(new ForwardToRoomComposer(RoomManager.HOME_ROOM_ID).compose());

            messages.add(new MeMenuSettingsComposer(this.client.getHabbo()).compose());







//
//

//
//
//




















            this.client.sendResponses(messages);



        }
        else
        {
            Emulator.getLogging().logDebugLine("Habbo is NULL!");
            Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
        }
    }
}
