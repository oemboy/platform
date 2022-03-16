
package net.platform.center.cfg.executor;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
@Data
@ConfigurationProperties(prefix = "spring.visible.executor")
public class VisibleExecutorConfig {
    private int coreThreadNumber;

    private int maxTheadNumber;

    private int queueSize;

    private int keepAliveSeconds;

    private String threadNamePrefix;

    private boolean waitForTasksToCompleteOnShutdown;

    @Bean("visibleExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor(@Autowired(required = false) TaskDecorator decorator,
        @Autowired(required = false) RejectedExecutionHandler rejectedHandler) {
        ThreadPoolTaskExecutor executor = new VisibleThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreThreadNumber);
        executor.setMaxPoolSize(maxTheadNumber);
        executor.setQueueCapacity(queueSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.setRejectedExecutionHandler(
            Optional.ofNullable(rejectedHandler).orElse(new ThreadPoolExecutor.CallerRunsPolicy()));
        executor.setTaskDecorator(Optional.ofNullable(decorator).orElse(new ContextCopyDecorator()));
        executor.initialize();
        return executor;

    }
}
