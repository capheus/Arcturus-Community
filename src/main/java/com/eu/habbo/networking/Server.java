package com.eu.habbo.networking;

import com.eu.habbo.Emulator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.TimeUnit;

public abstract class Server
{
    private final String name;
    private final String host;
    private final int port;

    protected final ServerBootstrap serverBootstrap;
    protected final EventLoopGroup bossGroup;
    protected final EventLoopGroup workerGroup;

    public Server(String name, String host, int port, int bossGroupThreads, int workerGroupThreads) throws Exception
    {
        this.name = name;
        this.host = host;
        this.port = port;

        this.bossGroup = new NioEventLoopGroup(bossGroupThreads);
        this.workerGroup = new NioEventLoopGroup(workerGroupThreads);
        this.serverBootstrap = new ServerBootstrap();
    }

    public void initializePipeline()
    {
        this.serverBootstrap.group(this.bossGroup, this.workerGroup);
        this.serverBootstrap.channel(NioServerSocketChannel.class);
        this.serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        this.serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        this.serverBootstrap.childOption(ChannelOption.SO_RCVBUF, 5120);
        this.serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(5120));
        this.serverBootstrap.childOption(ChannelOption.ALLOCATOR, new UnpooledByteBufAllocator(false));
    }

    public void connect()
    {
        ChannelFuture channelFuture = this.serverBootstrap.bind(this.host, this.port);

        while (!channelFuture.isDone())
        {}

        if (!channelFuture.isSuccess())
        {
            Emulator.getLogging().logShutdownLine("Failed to connect to the host (" + this.host + ":" + this.port + ")@" + this.name);
            System.exit(0);
        }
        else
        {
            Emulator.getLogging().logStart("Started GameServer on " + this.host + ":" + this.port + "@" + this.name);
        }
    }

    public void stop()
    {
        Emulator.getLogging().logShutdownLine("Stopping " + this.name);
        try
        {
            this.workerGroup.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS).sync();
            this.bossGroup.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS).sync();
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine("Exception during " + this.name + " shutdown... HARD STOP");
        }
        Emulator.getLogging().logShutdownLine("GameServer Stopped!");
    }

    public ServerBootstrap getServerBootstrap()
    {
        return this.serverBootstrap;
    }

    public EventLoopGroup getBossGroup()
    {
        return this.bossGroup;
    }

    public EventLoopGroup getWorkerGroup()
    {
        return this.workerGroup;
    }

    public String getHost()
    {
        return this.host;
    }

    public int getPort()
    {
        return this.port;
    }
}