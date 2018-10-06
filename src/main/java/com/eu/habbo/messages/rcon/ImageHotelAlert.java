package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.google.gson.Gson;
import gnu.trove.map.hash.THashMap;

import java.util.Map;

public class ImageHotelAlert extends RCONMessage<ImageHotelAlert.JSON>
{
    public ImageHotelAlert()
    {
        super(ImageHotelAlert.JSON.class);
    }

    @Override
    public void handle(Gson gson, JSON json)
    {
        THashMap<String, String> keys = new THashMap<>();
        keys.put("message", json.message);
        keys.put("linkUrl", json.url);
        keys.put("linkTitle", json.url_message);
        keys.put("title", json.title);
        ServerMessage message = new BubbleAlertComposer(json.bubble_key, keys).compose();
        for(Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet())
        {
            Habbo habbo = set.getValue();
            if(habbo.getHabboStats().blockStaffAlerts)
                continue;

            habbo.getClient().sendResponse(message);
        }
    }

    public static class JSON
    {

        public String bubble_key;


        public String message;


        public String url;


        public String url_message;


        public String title;
    }
}