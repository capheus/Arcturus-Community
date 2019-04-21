package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.PostItColor;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class PostItSaveDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();
        String color = this.packet.readString();
        String text = this.packet.readString();

        if (text.length() > Emulator.getConfig().getInt("postit.charlimit"))
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.sticky.size").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%amount%", text.length() + "").replace("%limit%", "366"));

            if (text.length() >= Emulator.getConfig().getInt("postit.charlimit") + 50)
            {
                this.client.getHabbo().alert("8=====D~~~~~<br><br>Computer Says:<b><u>NO</u></b>");
            }
            return;
        }

        text = text.replace(((char) 9) + "", "");
        if(text.startsWith("#") || text.startsWith(" #"))
        {
            String colorCheck = text.split(" ")[0].replace(" ", "").replace(" #", "").replace("#", "");

            if(colorCheck.length() == 6)
            {
                color = colorCheck;
                text = text.replace("#" + colorCheck + " ", "");
            }
        }

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        HabboItem item = room.getHabboItem(itemId);

        if(!(item instanceof InteractionPostIt))
            return;

        if(!color.equalsIgnoreCase(PostItColor.YELLOW.hexColor) && !room.hasRights(this.client.getHabbo())&& item.getUserId() != this.client.getHabbo().getHabboInfo().getId())
        {
            if(!text.startsWith(item.getExtradata().replace(item.getExtradata().split(" ")[0], "")))
            {
                return;
            }
        }
        else
        {
            if(!room.hasRights(this.client.getHabbo()) && item.getUserId() != this.client.getHabbo().getHabboInfo().getId())
                return;
        }

        if(color.isEmpty())
            color = PostItColor.YELLOW.hexColor;

        item.setUserId(room.getOwnerId());
        item.setExtradata(color + " " + text);
        item.needsUpdate(true);
        room.updateItem(item);
        Emulator.getThreading().run(item);
    }
}
