package io.github.gaming32.fabricspigot.api.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.Validate;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.logging.Level;

/**
 * The fundamental concepts for this implementation:
 * <li>Main thread owns {@link #head} and {@link #currentTick}, but it may be read from any thread</li>
 * <li>Main thread exclusively controls {@link #temp} and {@link #pending}.
 *     They are never to be accessed outside of the main thread; alternatives exist to prevent locking.</li>
 * <li>{@link #head} to {@link #tail} act as a linked list/queue, with 1 consumer and infinite producers.
 *     Adding to the tail is atomic and very efficient; utility method is {@link #handle(FabricTask, long)} or {@link #addTask(FabricTask)}. </li>
 * <li>Changing the period on a task is delicate.
 *     Any future task needs to notify waiting threads.
 *     Async tasks must be synchronized to make sure that any thread that's finishing will remove itself from {@link #runners}.
 *     Another utility method is provided for this, {@link #cancelTask(int)}</li>
 * <li>{@link #runners} provides a moderately up-to-date view of active tasks.
 *     If the linked head to tail set is read, all remaining tasks that were active at the time execution started will be located in runners.</li>
 * <li>Async tasks are responsible for removing themselves from runners</li>
 * <li>Sync tasks are only to be removed from runners on the main thread when coupled with a removal from pending and temp.</li>
 * <li>Most of the design in this scheduler relies on queuing special tasks to perform any data changes on the main thread.
 *     When executed from inside a synchronous method, the scheduler will be updated before next execution by virtue of the frequent {@link #parsePending()} calls.</li>
 */
public class FabricScheduler implements BukkitScheduler {
    /**
     * The start ID for the counter.
     */
    private static final int START_ID = 1;
    /**
     * Increment the {@link #ids} field and reset it to the {@link #START_ID} if it reaches {@link Integer#MAX_VALUE}
     */
    private static final IntUnaryOperator INCREMENT_IDS = previous -> {
        // We reached the end, go back to the start!
        if (previous == Integer.MAX_VALUE) {
            return START_ID;
        }
        return previous + 1;
    };
    /**
     * Counter for IDs. Order doesn't matter, only uniqueness.
     */
    private final AtomicInteger ids = new AtomicInteger(START_ID);
    /**
     * Current head of linked-list. This reference is always stale, {@link FabricTask#next} is the live reference.
     */
    private volatile FabricTask head = new FabricTask();
    /**
     * Tail of a linked-list. AtomicReference only matters when adding to queue
     */
    private final AtomicReference<FabricTask> tail = new AtomicReference<>(head);
    // If the tasks should run on the same tick they should be run FIFO
    /**
     * Main thread logic only
     */
    private final PriorityQueue<FabricTask> pending = new PriorityQueue<>(
        10,
        Comparator.comparingLong(FabricTask::getNextRun).thenComparingLong(FabricTask::getCreatedAt)
    );
    /**
     * Main thread logic only
     */
    private final List<FabricTask> temp = new ArrayList<>();
    /**
     * These are tasks that are currently active. It's provided for 'viewing' the current state.
     */
    private final ConcurrentHashMap<Integer, FabricTask> runners = new ConcurrentHashMap<>();
    /**
     * The sync task that is currently running on the main thread.
     */
    private volatile FabricTask currentTask = null;
    private volatile int currentTick = -1;
    private final Executor executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Craft Scheduler Thread - %d").build());
    private FabricAsyncDebugger debugHead = new FabricAsyncDebugger(-1, null, null) {
        @Override
        StringBuilder debugTo(StringBuilder string) {
            return string;
        }
    };
    private FabricAsyncDebugger debugTail = debugHead;
    private static final int RECENT_TICKS;

    static {
        RECENT_TICKS = 30;
    }

    @Override
    public int scheduleSyncDelayedTask(final @NotNull Plugin plugin, final @NotNull Runnable task) {
        return this.scheduleSyncDelayedTask(plugin, task, 0L);
    }

    @NotNull
    @Override
    public BukkitTask runTask(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        return runTaskLater(plugin, runnable, 0L);
    }

    @Override
    public void runTask(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> task) throws IllegalArgumentException {
        runTaskLater(plugin, task, 0L);
    }

    @Deprecated
    @Override
    public int scheduleAsyncDelayedTask(final @NotNull Plugin plugin, final @NotNull Runnable task) {
        return this.scheduleAsyncDelayedTask(plugin, task, 0L);
    }

    @NotNull
    @Override
    public BukkitTask runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        return runTaskLaterAsynchronously(plugin, runnable, 0L);
    }

    @Override
    public void runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> task) throws IllegalArgumentException {
        runTaskLaterAsynchronously(plugin, task, 0L);
    }

    @Override
    public int scheduleSyncDelayedTask(final @NotNull Plugin plugin, final @NotNull Runnable task, final long delay) {
        return this.scheduleSyncRepeatingTask(plugin, task, delay, FabricTask.NO_REPEATING);
    }

    @NotNull
    @Override
    public BukkitTask runTaskLater(@NotNull Plugin plugin, @NotNull Runnable runnable, long delay) {
        return runTaskTimer(plugin, runnable, delay, FabricTask.NO_REPEATING);
    }

    @Override
    public void runTaskLater(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> task, long delay) throws IllegalArgumentException {
        runTaskTimer(plugin, task, delay, FabricTask.NO_REPEATING);
    }

    @Deprecated
    @Override
    public int scheduleAsyncDelayedTask(final @NotNull Plugin plugin, final @NotNull Runnable task, final long delay) {
        return this.scheduleAsyncRepeatingTask(plugin, task, delay, FabricTask.NO_REPEATING);
    }

    @NotNull
    @Override
    public BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, @NotNull Runnable runnable, long delay) {
        return runTaskTimerAsynchronously(plugin, runnable, delay, FabricTask.NO_REPEATING);
    }

    @Override
    public void runTaskLaterAsynchronously(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> task, long delay) throws IllegalArgumentException {
        runTaskTimerAsynchronously(plugin, task, delay, FabricTask.NO_REPEATING);
    }

    @Override
    public void runTaskTimerAsynchronously(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> task, long delay, long period) throws IllegalArgumentException {
        runTaskTimerAsynchronously(plugin, (Object) task, delay, FabricTask.NO_REPEATING);
    }

    @Override
    public int scheduleSyncRepeatingTask(final @NotNull Plugin plugin, final @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimer(plugin, runnable, delay, period).getTaskId();
    }

    @NotNull
    @Override
    public BukkitTask runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimer(plugin, (Object) runnable, delay, period);
    }

    @Override
    public void runTaskTimer(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> task, long delay, long period) throws IllegalArgumentException {
        runTaskTimer(plugin, (Object) task, delay, period);
    }

    public BukkitTask runTaskTimer(Plugin plugin, Object runnable, long delay, long period) {
        validate(plugin, runnable);
        if (delay < 0L) {
            delay = 0;
        }
        if (period == FabricTask.ERROR) {
            period = 1L;
        } else if (period < FabricTask.NO_REPEATING) {
            period = FabricTask.NO_REPEATING;
        }
        return handle(new FabricTask(plugin, runnable, nextId(), period), delay);
    }

    @Deprecated
    @Override
    public int scheduleAsyncRepeatingTask(final @NotNull Plugin plugin, final @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimerAsynchronously(plugin, runnable, delay, period).getTaskId();
    }

    @NotNull
    @Override
    public BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, @NotNull Runnable runnable, long delay, long period) {
        return runTaskTimerAsynchronously(plugin, (Object) runnable, delay, period);
    }

    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, Object runnable, long delay, long period) {
        validate(plugin, runnable);
        if (delay < 0L) {
            delay = 0;
        }
        if (period == FabricTask.ERROR) {
            period = 1L;
        } else if (period < FabricTask.NO_REPEATING) {
            period = FabricTask.NO_REPEATING;
        }
        return handle(new FabricAsyncTask(runners, plugin, runnable, nextId(), period), delay);
    }

    @NotNull
    @Override
    public <T> Future<T> callSyncMethod(final @NotNull Plugin plugin, final @NotNull Callable<T> task) {
        validate(plugin, task);
        final FabricFuture<T> future = new FabricFuture<>(task, plugin, nextId());
        handle(future, 0L);
        return future;
    }

    @Override
    public void cancelTask(final int taskId) {
        if (taskId <= 0) {
            return;
        }
        FabricTask task = runners.get(taskId);
        if (task != null) {
            task.cancel0();
        }
        task = new FabricTask(
            new Runnable() {
                @Override
                public void run() {
                    if (!check(FabricScheduler.this.temp)) {
                        check(FabricScheduler.this.pending);
                    }
                }
                private boolean check(final Iterable<FabricTask> collection) {
                    final Iterator<FabricTask> tasks = collection.iterator();
                    while (tasks.hasNext()) {
                        final FabricTask task = tasks.next();
                        if (task.getTaskId() == taskId) {
                            task.cancel0();
                            tasks.remove();
                            if (task.isSync()) {
                                runners.remove(taskId);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });
        handle(task, 0L);
        for (FabricTask taskPending = head.getNext(); taskPending != null; taskPending = taskPending.getNext()) {
            if (taskPending == task) {
                return;
            }
            if (taskPending.getTaskId() == taskId) {
                taskPending.cancel0();
            }
        }
    }

    @Override
    public void cancelTasks(final @NotNull Plugin plugin) {
        Validate.notNull(plugin, "Cannot cancel tasks of null plugin");
        final FabricTask task = new FabricTask(
            new Runnable() {
                @Override
                public void run() {
                    check(FabricScheduler.this.pending);
                    check(FabricScheduler.this.temp);
                }
                void check(final Iterable<FabricTask> collection) {
                    final Iterator<FabricTask> tasks = collection.iterator();
                    while (tasks.hasNext()) {
                        final FabricTask task = tasks.next();
                        if (task.getOwner().equals(plugin)) {
                            task.cancel0();
                            tasks.remove();
                            if (task.isSync()) {
                                runners.remove(task.getTaskId());
                            }
                        }
                    }
                }
            });
        handle(task, 0L);
        for (FabricTask taskPending = head.getNext(); taskPending != null; taskPending = taskPending.getNext()) {
            if (taskPending == task) {
                break;
            }
            if (taskPending.getTaskId() != -1 && taskPending.getOwner().equals(plugin)) {
                taskPending.cancel0();
            }
        }
        for (FabricTask runner : runners.values()) {
            if (runner.getOwner().equals(plugin)) {
                runner.cancel0();
            }
        }
    }

    @Override
    public boolean isCurrentlyRunning(final int taskId) {
        final FabricTask task = runners.get(taskId);
        if (task == null) {
            return false;
        }
        if (task.isSync()) {
            return (task == currentTask);
        }
        final FabricAsyncTask asyncTask = (FabricAsyncTask) task;
        synchronized (asyncTask.getWorkers()) {
            return !asyncTask.getWorkers().isEmpty();
        }
    }

    @Override
    public boolean isQueued(final int taskId) {
        if (taskId <= 0) {
            return false;
        }
        for (FabricTask task = head.getNext(); task != null; task = task.getNext()) {
            if (task.getTaskId() == taskId) {
                return task.getPeriod() >= FabricTask.NO_REPEATING; // The task will run
            }
        }
        FabricTask task = runners.get(taskId);
        return task != null && task.getPeriod() >= FabricTask.NO_REPEATING;
    }

    @NotNull
    @Override
    public List<BukkitWorker> getActiveWorkers() {
        final ArrayList<BukkitWorker> workers = new ArrayList<>();
        for (final FabricTask taskObj : runners.values()) {
            // Iterator will be a best-effort (may fail to grab very new values) if called from an async thread
            if (taskObj.isSync()) {
                continue;
            }
            final FabricAsyncTask task = (FabricAsyncTask) taskObj;
            synchronized (task.getWorkers()) {
                // This will never have an issue with stale threads; it's state-safe
                workers.addAll(task.getWorkers());
            }
        }
        return workers;
    }

    @NotNull
    @Override
    public List<BukkitTask> getPendingTasks() {
        final ArrayList<FabricTask> truePending = new ArrayList<>();
        for (FabricTask task = head.getNext(); task != null; task = task.getNext()) {
            if (task.getTaskId() != -1) {
                // -1 is special code
                truePending.add(task);
            }
        }

        final ArrayList<BukkitTask> pending = new ArrayList<>();
        for (FabricTask task : runners.values()) {
            if (task.getPeriod() >= FabricTask.NO_REPEATING) {
                pending.add(task);
            }
        }

        for (final FabricTask task : truePending) {
            if (task.getPeriod() >= FabricTask.NO_REPEATING && !pending.contains(task)) {
                pending.add(task);
            }
        }
        return pending;
    }

    /**
     * This method is designed to never block or wait for locks; an immediate execution of all current tasks.
     */
    public void mainThreadHeartbeat(final int currentTick) {
        this.currentTick = currentTick;
        final List<FabricTask> temp = this.temp;
        parsePending();
        while (isReady(currentTick)) {
            final FabricTask task = pending.remove();
            if (task.getPeriod() < FabricTask.NO_REPEATING) {
                if (task.isSync()) {
                    runners.remove(task.getTaskId(), task);
                }
                parsePending();
                continue;
            }
            if (task.isSync()) {
                currentTask = task;
                try {
                    task.run();
                } catch (final Throwable throwable) {
                    task.getOwner().getLogger().log(
                        Level.WARNING,
                        String.format(
                            "Task #%s for %s generated an exception",
                            task.getTaskId(),
                            task.getOwner().getDescription().getFullName()),
                        throwable);
                } finally {
                    currentTask = null;
                }
                parsePending();
            } else {
                debugTail = debugTail.setNext(new FabricAsyncDebugger(currentTick + RECENT_TICKS, task.getOwner(), task.getTaskClass()));
                executor.execute(task);
                // We don't need to parse pending
                // (async tasks must live with race-conditions if they attempt to cancel between these few lines of code)
            }
            final long period = task.getPeriod(); // State consistency
            if (period > 0) {
                task.setNextRun(currentTick + period);
                temp.add(task);
            } else if (task.isSync()) {
                runners.remove(task.getTaskId());
            }
        }
        pending.addAll(temp);
        temp.clear();
        debugHead = debugHead.getNextHead(currentTick);
    }

    private void addTask(final FabricTask task) {
        final AtomicReference<FabricTask> tail = this.tail;
        FabricTask tailTask = tail.get();
        while (!tail.compareAndSet(tailTask, task)) {
            tailTask = tail.get();
        }
        tailTask.setNext(task);
    }

    private FabricTask handle(final FabricTask task, final long delay) {
        task.setNextRun(currentTick + delay);
        addTask(task);
        return task;
    }

    private static void validate(final Plugin plugin, final Object task) {
        Validate.notNull(plugin, "Plugin cannot be null");
        Validate.notNull(task, "Task cannot be null");
        Validate.isTrue(task instanceof Runnable || task instanceof Consumer || task instanceof Callable, "Task must be Runnable, Consumer, or Callable");
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register task while disabled");
        }
    }

    private int nextId() {
        Validate.isTrue(runners.size() < Integer.MAX_VALUE, "There are already " + Integer.MAX_VALUE + " tasks scheduled! Cannot schedule more.");
        int id;
        do {
            id = ids.updateAndGet(INCREMENT_IDS);
        } while (runners.containsKey(id)); // Avoid generating duplicate IDs
        return id;
    }

    private void parsePending() {
        FabricTask head = this.head;
        FabricTask task = head.getNext();
        FabricTask lastTask = head;
        for (; task != null; task = (lastTask = task).getNext()) {
            if (task.getTaskId() == -1) {
                task.run();
            } else if (task.getPeriod() >= FabricTask.NO_REPEATING) {
                pending.add(task);
                runners.put(task.getTaskId(), task);
            }
        }
        // We split this because of the way things are ordered for all the async calls in CraftScheduler
        // (it prevents race-conditions)
        for (task = head; task != lastTask; task = head) {
            head = task.getNext();
            task.setNext(null);
        }
        this.head = lastTask;
    }

    private boolean isReady(final int currentTick) {
        return !pending.isEmpty() && pending.peek().getNextRun() <= currentTick;
    }

    @Override
    public String toString() {
        int debugTick = currentTick;
        StringBuilder string = new StringBuilder("Recent tasks from ").append(debugTick - RECENT_TICKS).append('-').append(debugTick).append('{');
        debugHead.debugTo(string);
        return string.append('}').toString();
    }

    @Deprecated
    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull BukkitRunnable task, long delay) {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTaskLater(Plugin, long)");
    }

    @Deprecated
    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin plugin, @NotNull BukkitRunnable task) {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTask(Plugin)");
    }

    @Deprecated
    @Override
    public int scheduleSyncRepeatingTask(@NotNull Plugin plugin, @NotNull BukkitRunnable task, long delay, long period) {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTaskTimer(Plugin, long, long)");
    }

    @NotNull
    @Deprecated
    @Override
    public BukkitTask runTask(@NotNull Plugin plugin, @NotNull BukkitRunnable task) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTask(Plugin)");
    }

    @NotNull
    @Deprecated
    @Override
    public BukkitTask runTaskAsynchronously(@NotNull Plugin plugin, @NotNull BukkitRunnable task) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTaskAsynchronously(Plugin)");
    }

    @NotNull
    @Deprecated
    @Override
    public BukkitTask runTaskLater(@NotNull Plugin plugin, @NotNull BukkitRunnable task, long delay) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTaskLater(Plugin, long)");
    }

    @NotNull
    @Deprecated
    @Override
    public BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, @NotNull BukkitRunnable task, long delay) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTaskLaterAsynchronously(Plugin, long)");
    }

    @NotNull
    @Deprecated
    @Override
    public BukkitTask runTaskTimer(@NotNull Plugin plugin, @NotNull BukkitRunnable task, long delay, long period) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTaskTimer(Plugin, long, long)");
    }

    @NotNull
    @Deprecated
    @Override
    public BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, @NotNull BukkitRunnable task, long delay, long period) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Use BukkitRunnable#runTaskTimerAsynchronously(Plugin, long, long)");
    }
}
