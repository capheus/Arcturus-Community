package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.NavigatorFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterField;
import com.eu.habbo.habbohotel.navigation.SearchResultList;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.NewNavigatorSearchResultsComposer;

import java.util.*;

public class RequestNewNavigatorRoomsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String view = this.packet.readString();
        String query = this.packet.readString();

        NavigatorFilter filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get(view);
        RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategoryBySafeCaption(view);

        String filterField = "anything";
        String part = query;
        NavigatorFilterField field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField);
        if (filter != null)
        {
            if (query.contains(":"))
            {
                String[] parts = query.split(":");

                if (parts.length > 1)
                {
                    filterField = parts[0];
                    part = parts[1];
                } else
                {
                    filterField = parts[0].replace(":", "");
                    if (!Emulator.getGameEnvironment().getNavigatorManager().filterSettings.containsKey(filterField))
                    {
                        filterField = "anything";
                    }
                }
            }

            if (Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField) != null)
            {
                field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField);
            }
        }

        if (field == null || query.isEmpty())
        {
            if (filter == null)
                return;

            List<SearchResultList> resultLists = filter.getResult(this.client.getHabbo());
            Collections.sort(resultLists);
            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
            return;
        }

        if (filter == null && category != null)
        {
            filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get("hotel_view");
        }

        if (filter == null)
            return;

        try
        {

            List<SearchResultList> resultLists = new ArrayList<>(filter.getResult(this.client.getHabbo(), field, part, category != null ? category.getId() : -1));
            filter.filter(field.field, part, resultLists);

            Collections.sort(resultLists);
            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    private void filter(List<SearchResultList> resultLists, NavigatorFilter filter, String part)
    {
        List<SearchResultList> toRemove = new ArrayList<>();
        Map<Integer, HashMap<Integer, Room>> filteredRooms = new HashMap<>();

        for (NavigatorFilterField field : Emulator.getGameEnvironment().getNavigatorManager().filterSettings.values())
        {
            for (SearchResultList result : resultLists)
            {
                if (result.filter)
                {
                    List<Room> rooms = new ArrayList<>(result.rooms.subList(0, result.rooms.size()));
                    filter.filterRooms(field.field, part, rooms);

                    if (!filteredRooms.containsKey(result.order))
                    {
                        filteredRooms.put(result.order, new HashMap<>());
                    }

                    for (Room room : rooms)
                    {
                        filteredRooms.get(result.order).put(room.getId(), room);
                    }
                }
            }
        }

        for (Map.Entry<Integer, HashMap<Integer, Room>> set : filteredRooms.entrySet())
        {
            for (SearchResultList resultList : resultLists)
            {
                if (resultList.filter)
                {
                    resultList.rooms.clear();
                    resultList.rooms.addAll(set.getValue().values());

                    if (resultList.rooms.isEmpty())
                    {
                        toRemove.add(resultList);
                    }
                }
            }
        }

        resultLists.removeAll(toRemove);
    }
}
