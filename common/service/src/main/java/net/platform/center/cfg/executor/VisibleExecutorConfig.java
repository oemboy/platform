
package net.platform.center.cfg.executor;

import java.util.Optional;
import java.util.Set;
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
@ConfigurationProperties(prefix = "spring.visible")
public class VisibleExecutorConfig {

    private Executor executor;

    private ContextCopy contextCopy;

    @Bean("visibleExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor(@Autowired(required = false) TaskDecorator decorator,
        @Autowired(required = false) RejectedExecutionHandler rejectedHandler) {
        ThreadPoolTaskExecutor taskExecutor = new VisibleThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(getExecutor().getCoreThreadNumber());
        taskExecutor.setMaxPoolSize(getExecutor().getMaxTheadNumber());
        taskExecutor.setQueueCapacity(getExecutor().getQueueSize());
        taskExecutor.setKeepAliveSeconds(getExecutor().getKeepAliveSeconds());
        taskExecutor.setThreadNamePrefix(getExecutor().getThreadNamePrefix());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(getExecutor().isWaitForTasksToCompleteOnShutdown());
        taskExecutor.setAwaitTerminationMillis(getExecutor().getAwaitTerminationMillis());
        taskExecutor.setRejectedExecutionHandler(
            Optional.ofNullable(rejectedHandler).orElse(new ThreadPoolExecutor.CallerRunsPolicy()));
        taskExecutor.setTaskDecorator(Optional.ofNullable(decorator).orElse(new ContextCopyDecorator(contextCopy)));
        taskExecutor.initialize();
        return taskExecutor;

    }

    @Data
    public static class Executor {

        private int coreThreadNumber;

        private int maxTheadNumber;

        private int queueSize;

        private int keepAliveSeconds;

        private String threadNamePrefix;

        private boolean waitForTasksToCompleteOnShutdown;

        private long awaitTerminationMillis;
    }

    @Data
    public static class ContextCopy {

        private Set<String> runtimeExclude;
    }
}
