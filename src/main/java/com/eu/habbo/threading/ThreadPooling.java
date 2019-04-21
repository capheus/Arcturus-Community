package com.eu.habbo.threading;

import com.eu.habbo.Emulator;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPooling
{
    public final int threads;
    private final ScheduledExecutorService scheduledPool;
    private volatile boolean canAdd;

    public ThreadPooling(Integer threads)
    {
        this.threads = threads;
        this.scheduledPool = new HabboExecutorService(this.threads, new DefaultThreadFactory("ArcturusThreadFactory"));
        this.canAdd = true;
        Emulator.getLogging().logStart("Thread Pool -> Loaded!");
    }

    public ScheduledFuture run(Runnable run)
    {
        try
        {
            if (this.canAdd)
            {
                return this.run(run, 0);
            }
            else
            {
                if (Emulator.isShuttingDown)
                {
                    run.run();
                }
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return null;
    }

    public ScheduledFuture run(Runnable run, long delay)
    {
        try
        {
            if (this.canAdd)
            {
                return this.scheduledPool.schedule(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            run.run();
                        }
                        catch (Exception e)
                        {
                            Emulator.getLogging().logErrorLine(e);
                        }
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return null;
    }

    public void shutDown()
    {
        this.canAdd = false;
        this.scheduledPool.shutdownNow();

        Emulator.getLogging().logShutdownLine("Threading -> Disposed!");
    }

    public void setCanAdd(boolean canAdd)
    {
        this.canAdd = canAdd;
    }

    public ScheduledExecutorService getService()
    {
        return this.scheduledPool;
    }


}
