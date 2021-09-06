
package net.platform.center.service;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public abstract class TimeoutService {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    public static <T> CompletableFuture<T> failAfter(final Duration duration, Class<T> clazz) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        SCHEDULER.schedule(() -> {
            final TimeoutException ex = new TimeoutException("timeout after " + formatTime(duration));
            return promise.completeExceptionally(ex);
        }, duration.toMillis(), TimeUnit.MICROSECONDS);
        return promise;
    }

    private static String formatTime(Duration duration) {
        return duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
    }

    public static <T> void cancelTask(CompletableFuture<T> future) {
        try {
            if (future != null && !future.isDone()) {
                log.info("cancel task: {}", future.cancel(false));
            }
        } catch (Exception e) {
            log.error("cancel task error: ", e.getMessage());
        }
    }

    public <T> CompletableFuture<T> completableFutureInTime(Duration duration, Class<T> clazz) {
        CompletableFuture<T> timeout = failAfter(duration, clazz);
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> doBusinessInTime());

        return timeout.applyToEitherAsync(future, i -> i).exceptionally(ex -> {
            log.error("error:", ex.getMessage());
            cancelTask(future);
            return null;
        });
    }

    protected abstract <T> T doBusinessInTime();
}
