package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.messages.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ChannelReadHandler implements Runnable
{
    private final ChannelHandlerContext ctx;
    private final Object msg;

    public ChannelReadHandler(ChannelHandlerContext ctx, Object msg)
    {
        this.ctx = ctx;
        this.msg = msg;
    }

    public void run()
    {
        ByteBuf m = (ByteBuf) this.msg;
        int length = m.readInt();
        short header = m.readShort();
        GameClient client = this.ctx.channel().attr(GameClientManager.CLIENT).get();

        if (client != null)
        {
            int count = 0;
            int timestamp = Emulator.getIntUnixTimestamp();
            if (timestamp - client.lastPacketCounterCleared > 1)
            {
                client.incomingPacketCounter.clear();
                client.lastPacketCounterCleared = timestamp;
            } else
            {
                count = client.incomingPacketCounter.getOrDefault(header, 0);
            }

            if (count <= 10)
            {
                count++;
                client.incomingPacketCounter.put((int) header, count);
                ByteBuf body = Unpooled.wrappedBuffer(m.readBytes(m.readableBytes()));
                Emulator.getGameServer().getPacketManager().handlePacket(client, new ClientMessage(header, body));
                body.release();
            }
        }
        m.release();
    }
}