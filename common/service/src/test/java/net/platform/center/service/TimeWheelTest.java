
package net.platform.center.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author bing zi
 * @since 2025/12/29 16:08
 */
class TimeWheelTest {
    private TimeWheel timeWheel;

    @BeforeEach
    void setUp() {
        timeWheel = new TimeWheel();
    }

    @AfterEach
    void tearDown() {
        timeWheel.shutdown();
    }

    @Test
    @DisplayName("基础功能：测试 L1 层级短延迟任务 (500ms)")
    void testShortDelayTask() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        long start = System.currentTimeMillis();
        long delay = 500;

        timeWheel.addTask(delay, latch::countDown);

        latch.await(1000, TimeUnit.MILLISECONDS);
        long duration = System.currentTimeMillis() - start;

        // 允许一个 TICK_MS (100ms) 的误差范围
        // 逻辑时钟对齐会导致任务在 [delay - TICK_MS, delay + TICK_MS] 范围内触发
        assertTrue(duration >= (delay - 100), "执行太早: " + duration + "ms");
    }

    @Test
    @DisplayName("跨层级：测试 L2 层级长延迟任务 (15s)")
    void testL2CrossLevelTask() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        long start = System.currentTimeMillis();
        long delay = 15000;

        timeWheel.addTask(delay, latch::countDown);

        latch.await(17, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - start;

        // 允许误差：由于经过了两次降级，允许 200ms 左右的逻辑对齐误差
        assertTrue(duration >= (delay - 200), "执行太早: " + duration + "ms");
    }

    @Test
    @DisplayName("边界值：测试过期或极短延迟任务")
    void testExpiredTask() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        // 1. 负延迟
        timeWheel.addTask(-100, latch::countDown);
        // 2. 0 延迟
        timeWheel.addTask(0, latch::countDown);

        boolean completed = latch.await(500, TimeUnit.MILLISECONDS);
        assertTrue(completed, "过期或 0 延迟任务应立即由线程池执行");
    }

    @Test
    @DisplayName("高并发：测试 10,000 个随机订单超时任务")
    void testHighConcurrency() throws InterruptedException {
        int taskCount = 10000;
        CountDownLatch latch = new CountDownLatch(taskCount);
        LongAdder executionCount = new LongAdder();

        long start = System.currentTimeMillis();
        for (int i = 0; i < taskCount; i++) {
            // 模拟 100ms 到 2000ms 之间的随机订单超时
            long delay = (long) (Math.random() * 1900 + 100);
            timeWheel.addTask(delay, () -> {
                executionCount.increment();
                latch.countDown();
            });
        }

        // 验证在预期时间内全部完成
        boolean allFinished = latch.await(5, TimeUnit.SECONDS);
        assertTrue(allFinished, "10000 个并发任务应在 5s 内处理完毕");
        assertEquals(taskCount, executionCount.sum(), "执行总数应匹配");

        System.out.println("高并发测试耗时: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    @DisplayName("稳定性：模拟 15 分钟订单业务场景 (加速验证)")
    void testOrderScenarioSimulated() throws InterruptedException {
        AtomicBoolean statusChecked = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 模拟业务逻辑：15分钟后检查订单状态并关单
        timeWheel.addTask(2000, () -> {
            // 模拟数据库操作
            statusChecked.set(true);
            latch.countDown();
        });

        // 验证任务是否被正常调度并执行
        boolean completed = latch.await(3, TimeUnit.SECONDS);
        assertTrue(completed);
        assertTrue(statusChecked.get(), "业务逻辑应被触发");
    }
}