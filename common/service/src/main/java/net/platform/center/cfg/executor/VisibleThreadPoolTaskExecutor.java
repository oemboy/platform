
package net.platform.center.cfg.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
@Slf4j
public class VisibleThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    @Override
    public void execute(Runnable task) {
        showThreadPoolInfo("1. do execute");
        super.execute(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        showThreadPoolInfo("1. do submit");
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        showThreadPoolInfo("2. do submit");
        return super.submit(task);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ListenableFuture<?> submitListenable(Runnable task) {
        showThreadPoolInfo("1. do submit Listenable");
        return super.submitListenable(task);
    }

    @Override
    public <T> CompletableFuture<T> submitCompletable(Callable<T> task) {
        showThreadPoolInfo("2. do submit Callable Completable");
        return super.submitCompletable(task);
    }

    @Override
    public CompletableFuture<Void> submitCompletable(Runnable task) {
        showThreadPoolInfo("1. do submit Runnable Completable");
        return super.submitCompletable(task);
    }

    private void showThreadPoolInfo(String prefix) {
        ThreadPoolExecutor executor = getThreadPoolExecutor();
        if (log.isDebugEnabled()) {
            log.debug("{}, {}, taskCount [{}], completedTaskCount [{}], activeCount [{}], queueSize [{}]",
                this.getThreadNamePrefix(), prefix, executor.getTaskCount(), executor.getCompletedTaskCount(),
                executor.getActiveCount(), executor.getQueue().size());
        }
    }
}
