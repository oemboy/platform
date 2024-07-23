
package net.platform.center.service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
@Slf4j
public abstract class BaseTimeoutService {

    public <T> CompletableFuture<T> completeInTime(Duration duration) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(this::doBusinessInTime);
        return future.orTimeout(duration.toSeconds(), TimeUnit.SECONDS);

    }

    /**
     * 实现业务逻辑
     * 
     * @param <T> 业务逻辑返回
     * @return 业务逻辑返回
     */
    protected abstract <T> T doBusinessInTime();
}
