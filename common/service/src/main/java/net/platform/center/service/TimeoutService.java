
package net.platform.center.service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public abstract class TimeoutService {

    public <T> CompletableFuture<T> completeInTime(Duration duration) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(this::doBusinessInTime);
        return future.orTimeout(duration.toSeconds(), TimeUnit.SECONDS);

    }

    protected abstract <T> T doBusinessInTime();
}
