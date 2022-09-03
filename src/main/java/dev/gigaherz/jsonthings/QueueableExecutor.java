package dev.gigaherz.jsonthings;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

public class QueueableExecutor implements Executor
{
    private final Thread thread = Thread.currentThread();
    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private final Semaphore sem = new Semaphore(1);

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
            sem.release();
        }
        else
        {
            command.run();
        }
    }

    public void runQueue()
    {
        if (!isSameThread())
        {
            throw new IllegalStateException("This method must be called in the main thread.");
        }

        while (queue.size() > 0)
        {
            var run = queue.poll();
            if (run != null) run.run();
        }
    }

    public void finish()
    {
        sem.release();
    }

    public void waitForTasks() throws InterruptedException
    {
        sem.acquire();
    }
}
