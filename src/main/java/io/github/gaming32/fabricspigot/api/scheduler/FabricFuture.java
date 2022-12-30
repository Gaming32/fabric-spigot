package io.github.gaming32.fabricspigot.api.scheduler;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.*;

class FabricFuture<T> extends FabricTask implements Future<T> {
    private final Callable<T> callable;
    private T value;
    private Exception exception = null;

    FabricFuture(final Callable<T> callable, final Plugin plugin, final int id) {
        super(plugin, null, id, FabricTask.NO_REPEATING);
        this.callable = callable;
    }

    @Override
    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (getPeriod() != FabricTask.NO_REPEATING) {
            return false;
        }
        setPeriod(FabricTask.CANCEL);
        return true;
    }

    @Override
    public boolean isDone() {
        final long period = this.getPeriod();
        return period != FabricTask.NO_REPEATING && period != FabricTask.PROCESS_FOR_FUTURE;
    }

    @Override
    public T get() throws CancellationException, InterruptedException, ExecutionException {
        try {
            return get(0, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            throw new Error(e);
        }
    }

    @Override
    public synchronized T get(long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        timeout = unit.toMillis(timeout);
        long period = this.getPeriod();
        long timestamp = timeout > 0 ? System.currentTimeMillis() : 0L;
        while (true) {
            if (period == FabricTask.NO_REPEATING || period == FabricTask.PROCESS_FOR_FUTURE) {
                this.wait(timeout);
                period = this.getPeriod();
                if (period == FabricTask.NO_REPEATING || period == FabricTask.PROCESS_FOR_FUTURE) {
                    if (timeout == 0L) {
                        continue;
                    }
                    timeout += timestamp - (timestamp = System.currentTimeMillis());
                    if (timeout > 0) {
                        continue;
                    }
                    throw new TimeoutException();
                }
            }
            if (period == FabricTask.CANCEL) {
                throw new CancellationException();
            }
            if (period == FabricTask.DONE_FOR_FUTURE) {
                if (exception == null) {
                    return value;
                }
                throw new ExecutionException(exception);
            }
            throw new IllegalStateException("Expected " + FabricTask.NO_REPEATING + " to " + FabricTask.DONE_FOR_FUTURE + ", got " + period);
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (getPeriod() == FabricTask.CANCEL) {
                return;
            }
            setPeriod(FabricTask.PROCESS_FOR_FUTURE);
        }
        try {
            value = callable.call();
        } catch (final Exception e) {
            exception = e;
        } finally {
            synchronized (this) {
                setPeriod(FabricTask.DONE_FOR_FUTURE);
                this.notifyAll();
            }
        }
    }

    @Override
    synchronized boolean cancel0() {
        if (getPeriod() != FabricTask.NO_REPEATING) {
            return false;
        }
        setPeriod(FabricTask.CANCEL);
        notifyAll();
        return true;
    }
}
