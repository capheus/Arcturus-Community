package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.threading.runnables.ChannelReadHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

@ChannelHandler.Sharable
public class GameMessageHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
    {
        if (!Emulator.getGameServer().getGameClientManager().addClient(ctx))
        {
            ctx.channel().close();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
    {
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        try
        {
            ChannelReadHandler handler = new ChannelReadHandler(ctx, msg);

            if (PacketManager.MULTI_THREADED_PACKET_HANDLING)
            {
                Emulator.getThreading().run(handler);
                return;
            }

            handler.run();
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        if (cause instanceof Exception)
        {
            if (!(cause instanceof IOException))
            {
                cause.printStackTrace(Logging.getErrorsRuntimeWriter());
            }
        }

        ctx.channel().close();
    }
}