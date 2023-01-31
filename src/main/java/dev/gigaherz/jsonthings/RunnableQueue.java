package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class RunnableQueue implements Executor
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Thread thread = Thread.currentThread();
    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
    //private final Semaphore sem = new Semaphore(1);

    public boolean isSameThread()
    {
        return Thread.currentThread() == thread;
    }

    @Override
    public void execute(@NotNull Runnable command)
    {
        if (!this.isSameThread())
        {
            queue.add(command);
            //sem.release();
        }
        else
        {
            command.run();
        }
    }

    public boolean runQueue()
    {
        if (!isSameThread())
        {
            throw new IllegalStateException("This method must be called in the main thread.");
        }

        if (queue.size() > 0) LOGGER.debug("Running " + queue.size() + " tasks (give or take)");

        int n = 0;
        while (queue.size() > 0)
        {
            var run = queue.poll();
            if (run != null) run.run();
            n++;
        }
        return n != 0;
    }

    public void finish()
    {
        //sem.release();
    }

    public void waitForTasks() throws InterruptedException
    {
        // FIXME: sem.acquire(); deadlocks
        Thread.sleep(1);
    }
}
