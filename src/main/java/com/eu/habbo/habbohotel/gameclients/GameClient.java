package com.eu.habbo.habbohotel.gameclients;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GameClient
{

    private final Channel channel;


    private Habbo habbo;


    private String machineId = "";

    public final ConcurrentHashMap<Integer, Integer> incomingPacketCounter = new ConcurrentHashMap<>(25);
    public long lastPacketCounterCleared = Emulator.getIntUnixTimestamp();

    public GameClient(Channel channel)
    {
        this.channel = channel;
    }


    public void sendResponse(MessageComposer composer)
    {
        if(this.channel.isOpen())
        {
            try
            {
                ServerMessage msg = composer.compose();
                this.sendResponse(msg);
            } catch (Exception e)
            {
                Emulator.getLogging().logPacketError(e);
            }
        }
    }


    public void sendResponse(ServerMessage response)
    {
        if(this.channel.isOpen())
        {
            if (response == null || response.getHeader() <= 0)
            {
                return;
            }

            if (PacketManager.DEBUG_SHOW_PACKETS)
                Emulator.getLogging().logPacketLine("[" + Logging.ANSI_PURPLE + "SERVER" + Logging.ANSI_RESET + "] => [" + response.getHeader() + "] -> " + response.getBodyString());

            this.channel.write(response.get(), this.channel.voidPromise());
            this.channel.flush();
        }
    }


    public void sendResponses(ArrayList<ServerMessage> responses)
    {
        ByteBuf buffer = Unpooled.buffer();

        if(this.channel.isOpen())
        {
            for(ServerMessage response : responses)
            {
                if (response == null || response.getHeader() <= 0)
                {
                    return;
                }

                if (PacketManager.DEBUG_SHOW_PACKETS)
                    Emulator.getLogging().logPacketLine("[" + Logging.ANSI_PURPLE + "SERVER" + Logging.ANSI_RESET + "] => [" + response.getHeader() + "] -> " + response.getBodyString());

                buffer.writeBytes(response.get());
            }
            this.channel.write(buffer.copy(), this.channel.voidPromise());
            this.channel.flush();
        }
        buffer.release();
    }


    public void dispose()
    {
        try
        {
            this.channel.close();

            if(this.habbo != null)
            {
                if(this.habbo.isOnline())
                {
                    this.habbo.getHabboInfo().setOnline(false);
                    this.habbo.disconnect();
                }

                this.habbo = null;
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public Channel getChannel()
    {
        return this.channel;
    }

    public Habbo getHabbo()
    {
        return this.habbo;
    }

    public void setHabbo(Habbo habbo)
    {
        this.habbo = habbo;
    }

    public String getMachineId()
    {
        return this.machineId;
    }

    public void setMachineId(String machineId)
    {
        if (machineId == null)
        {
            throw new RuntimeException("Cannot set machineID to NULL");
        }
        this.machineId = machineId;

        if (this.habbo != null)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET machine_id = ? WHERE id = ? LIMIT 1"))
            {
                statement.setString(1, this.machineId);
                statement.setInt(2, this.habbo.getHabboInfo().getId());
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }
}