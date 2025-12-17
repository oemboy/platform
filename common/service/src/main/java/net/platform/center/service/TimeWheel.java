
package net.platform.center.service;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author bing zi
 * @since 2025/12/29 16:04
 *        完整版：高性能分层时间轮 (Hierarchical Timing Wheel)
 */
public class TimeWheel {

    // 必须是 2 的幂
    private static final int SLOT_COUNT = 64;

    private static final int MASK = SLOT_COUNT - 1;

    // L1 精度 100ms
    private static final long TICK_MS = 100L;

    // 自动计算跨度：L1=6.4s, L2=409.6s(~6.8min), L3=26214s(~7.2hour)
    private static final long L1_INTERVAL = TICK_MS * SLOT_COUNT;

    // 6400ms
    private static final long L2_INTERVAL = L1_INTERVAL * SLOT_COUNT;

    private final Wheel rootWheel;

    private final DelayQueue<Bucket> delayQueue = new DelayQueue<>();

    private final ExecutorService driverThread;

    private final ExecutorService taskExecutor;

    private final LongAdder pendingTasks = new LongAdder();

    public TimeWheel() {
        // 构建级联结构
        Wheel l3 = new Wheel(SLOT_COUNT, L2_INTERVAL, null, delayQueue);
        Wheel l2 = new Wheel(SLOT_COUNT, L1_INTERVAL, l3, delayQueue);
        this.rootWheel = new Wheel(SLOT_COUNT, TICK_MS, l2, delayQueue);

        // 任务执行池：根据业务压力调整线程数
        this.taskExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100000),
            new ThreadPoolExecutor.CallerRunsPolicy());

        // 驱动线程
        this.driverThread = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "TimeWheel-Driver");
            t.setDaemon(true);
            return t;
        });
        this.driverThread.submit(this::drive);
    }

    private void drive() {
        try {
            while (!Thread.interrupted()) {
                Bucket bucket = delayQueue.take();
                // 向上取整推进时钟，配合测试通过 (或者保持原样，允许微小误差)
                rootWheel.advanceClock(bucket.getExpiration());
                // 弹出任务进行降级或执行
                bucket.flush(this::addTaskInternal);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 暴露给业务层的添加接口
     */
    public void addTask(long delayMs, Runnable task) {
        pendingTasks.increment();
        addTaskInternal(System.currentTimeMillis() + delayMs, task);
    }

    private void addTaskInternal(long expiration, Runnable task) {
        TimerTask timerTask = new TimerTask(expiration, task);
        if (!rootWheel.add(timerTask)) {
            // 到期执行
            taskExecutor.submit(() -> {
                try {
                    timerTask.task.run();
                } finally {
                    pendingTasks.decrement();
                }
            });
        }
    }

    public long getPendingTaskCount() {
        return pendingTasks.sum();
    }

    public void shutdown() {
        driverThread.shutdownNow();
        taskExecutor.shutdown();
    }

    // --- 内部类：任务实体 ---
    static class TimerTask {
        final long expiration;

        final Runnable task;

        Bucket bucket;

        TimerTask next;

        TimerTask prev;

        TimerTask(long expiration, Runnable task) {
            this.expiration = expiration;
            this.task = task;
        }
    }

    // 时间轮
    static class Wheel {
        private final long tickMs;

        private final long interval;

        private final Bucket[] buckets;

        private final Wheel parentWheel;

        private final DelayQueue<Bucket> queue;

        private final AtomicLong currentTime;

        Wheel(int slotCount, long tickMs, Wheel parentWheel, DelayQueue<Bucket> queue) {
            this.tickMs = tickMs;
            this.interval = tickMs * slotCount;
            this.parentWheel = parentWheel;
            this.queue = queue;
            long now = System.currentTimeMillis();
            this.currentTime = new AtomicLong(now - (now % tickMs));
            this.buckets = new Bucket[slotCount];
            for (int i = 0; i < slotCount; i++) {
                buckets[i] = new Bucket();
            }
        }

        public boolean add(TimerTask task) {
            long expiration = task.expiration;
            if (expiration < currentTime.get() + tickMs) {
                return false;
            }

            if (expiration < currentTime.get() + interval) {
                long virtualId = expiration / tickMs;
                // 全位运算优化：& MASK
                Bucket bucket = buckets[(int) (virtualId & MASK)];
                bucket.add(task);
                if (bucket.setExpiration(virtualId * tickMs)) {
                    return queue.offer(bucket);
                }
                return true;
            } else {
                return parentWheel != null && parentWheel.add(task);
            }
        }

        public void advanceClock(long time) {
            if (time >= currentTime.get() + tickMs) {
                currentTime.set(time - (time % tickMs));
                if (parentWheel != null) {
                    parentWheel.advanceClock(time);
                }
            }
        }
    }

    // 内部类：任务桶 (双向链表实现)
    static class Bucket implements Delayed {
        private final TimerTask root = new TimerTask(-1, null);

        private final AtomicLong expiration = new AtomicLong(-1);

        Bucket() {
            root.next = root;
            root.prev = root;
        }

        public boolean setExpiration(long expireMs) {
            return expiration.getAndSet(expireMs) != expireMs;
        }

        public long getExpiration() {
            return expiration.get();
        }

        public synchronized void add(TimerTask task) {
            task.bucket = this;
            TimerTask tail = root.prev;
            task.next = root;
            task.prev = tail;
            tail.next = task;
            root.prev = task;
        }

        public synchronized void remove(TimerTask task) {
            if (task.bucket == this) {
                task.next.prev = task.prev;
                task.prev.next = task.next;
                task.bucket = null;
                task.next = null;
                task.prev = null;
            }
        }

        public synchronized void flush(java.util.function.BiConsumer<Long, Runnable> reinsert) {
            TimerTask curr = root.next;
            while (curr != root) {
                TimerTask next = curr.next;
                remove(curr);
                reinsert.accept(curr.expiration, curr.task);
                curr = next;
            }
            expiration.set(-1);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(Math.max(0, expiration.get() - System.currentTimeMillis()), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (o == null) {
                return 1;
            }
            if (o == this) {
                return 0;
            }
            return Long.compare(this.getExpiration(), ((Bucket) o).getExpiration());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Bucket bucket)) {
                return false;
            }
            return getExpiration() == bucket.getExpiration();
        }

        @Override
        public int hashCode() {
            // 使用过期时间的 hash
            return Long.hashCode(getExpiration());
        }
    }
}
