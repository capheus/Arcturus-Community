package com.eu.habbo.messages.incoming.floorplaneditor;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.CustomRoomLayout;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FloorPlanEditorSaveEvent extends MessageHandler
{
    public static int MAXIMUM_FLOORPLAN_WIDTH_LENGTH = 64;
    public static int MAXIMUM_FLOORPLAN_SIZE = 64 * 64;

    @Override
    public void handle() throws Exception
    {
        if (!this.client.getHabbo().hasPermission("acc_floorplan_editor"))
        {
            this.client.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("floorplan.permission")));
            return;
        }

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        if(room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER))
        {
            List<String> errors = new ArrayList<>();
            String map = this.packet.readString();
            map = map.replace("X", "x");

            if(map.isEmpty() || map.replace("x", "").replace(((char) 13) + "", "").length() == 0)
            {
                errors.add("${notification.floorplan_editor.error.message.effective_height_is_0}");
            }

            int lengthX = -1;
            int lengthY = -1;

            String[] data = map.split(((char) 13) + "");

            if (errors.isEmpty())
            {
                if (map.length() > 64 * 64)
                {
                    errors.add("${notification.floorplan_editor.error.message.too_large_area}");
                }


                lengthY = data.length;
                if (data.length > 64)
                {
                    errors.add("${notification.floorplan_editor.error.message.too_large_height}");
                } else
                {
                    for (String s : data)
                    {
                        if (lengthX == -1)
                        {
                            lengthX = s.length();
                        }

                        if (s.length() != lengthX)
                        {
                            break;
                        }

                        if (s.length() > 64 || s.length() == 0)
                        {
                            errors.add("${notification.floorplan_editor.error.message.too_large_width}");
                        }
                    }
                }
            }

            int doorX = this.packet.readInt();
            int doorY = this.packet.readInt();

            if (doorX < 0 || doorX > lengthX || doorY < 0 || doorY > lengthY || data[doorY].charAt(doorX) == 'x')
            {
                errors.add("${notification.floorplan_editor.error.message.entry_tile_outside_map}");
            }

            int doorRotation = this.packet.readInt();

            if (doorRotation < 0 || doorRotation > 7)
            {
                errors.add("${notification.floorplan_editor.error.message.invalid_entry_tile_direction}");
            }

            int wallSize = this.packet.readInt();
            if (wallSize < -2 || wallSize > 1)
            {
                errors.add("${notification.floorplan_editor.error.message.invalid_wall_thickness}");
            }

            int floorSize = this.packet.readInt();
            if (floorSize < -2 || floorSize > 1)
            {
                errors.add("${notification.floorplan_editor.error.message.invalid_floor_thickness}");
            }
            int wallHeight = -1;

            if(this.packet.bytesAvailable() >= 4)
                wallHeight = this.packet.readInt();

            if (wallHeight < -1 || wallHeight > 15)
            {
                errors.add("${notification.floorplan_editor.error.message.invalid_walls_fixed_height}");
            }

            if (!errors.isEmpty())
            {
                StringBuilder errorMessage = new StringBuilder();
                for (String s : errors)
                {
                    errorMessage.append(s).append("<br />");
                }
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FLOORPLAN_EDITOR_ERROR.key, errorMessage.toString()));
                return;
            }

            RoomLayout layout = room.getLayout();

            if(layout instanceof CustomRoomLayout)
            {
                layout.setDoorX((short) doorX);
                layout.setDoorY((short) doorY);
                layout.setDoorDirection(doorRotation);
                layout.setHeightmap(map);
                layout.parse();

                if (layout.getDoorTile() == null)
                {
                    this.client.getHabbo().alert("Error");
                    ((CustomRoomLayout)layout).needsUpdate(false);
                    Emulator.getGameEnvironment().getRoomManager().unloadRoom(room);
                    return;
                }
                ((CustomRoomLayout)layout).needsUpdate(true);
                Emulator.getThreading().run((CustomRoomLayout)layout);
            }
            else
            {
                layout = Emulator.getGameEnvironment().getRoomManager().insertCustomLayout(room, map, doorX, doorY, doorRotation);
            }

            if(layout != null)
            {
                room.setHasCustomLayout(true);
                room.setNeedsUpdate(true);
                room.setLayout(layout);
                room.setWallSize(wallSize);
                room.setFloorSize(floorSize);
                room.setWallHeight(wallHeight);
                room.save();
                Collection<Habbo> habbos = new ArrayList<>(room.getUserCount());
                habbos.addAll(room.getHabbos());
                Emulator.getGameEnvironment().getRoomManager().unloadRoom(room);
                room = Emulator.getGameEnvironment().getRoomManager().loadRoom(room.getId());
                ServerMessage message = new ForwardToRoomComposer(room.getId()).compose();
                for (Habbo habbo : habbos)
                {
                    habbo.getClient().sendResponse(message);
                }
            }
        }
    }
}
