
package net.platform.center.cfg.executor;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */

@Slf4j
@Data
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "spring.visible")
public class VisibleExecutorConfig {

    private Executor executor;

    private Exclude exclude;

    @Bean
    @ConditionalOnMissingBean(TaskDecorator.class)
    public TaskDecorator taskDecorator() {
        return new ContextCopyDecorator(exclude);
    }

    @Bean("contextScheduler")
    public ThreadPoolTaskScheduler contextScheduler(TaskDecorator taskDecorator) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setTaskDecorator(taskDecorator);
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("context-scheduler-");
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Bean("visibleExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor(@Autowired(required = false) TaskDecorator decorator,
        @Autowired(required = false) RejectedExecutionHandler rejectedHandler) {

        RejectedExecutionHandler rejectedExecutionHandler =
            Optional.ofNullable(rejectedHandler).orElse(new ThreadPoolExecutor.CallerRunsPolicy());
        TaskDecorator taskDecorator = Optional.ofNullable(decorator).orElse(new ContextCopyDecorator(exclude));

        return buildThreadPoolTaskExecutor(getExecutor(), rejectedExecutionHandler, taskDecorator);
    }

    public static ThreadPoolTaskExecutor buildThreadPoolTaskExecutor(Executor executorConfig,
        RejectedExecutionHandler rejectedExecutionHandler, TaskDecorator taskDecorator) {
        ThreadPoolTaskExecutor taskExecutor = new VisibleThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(executorConfig.getCoreThreadNumber());
        taskExecutor.setMaxPoolSize(executorConfig.getMaxTheadNumber());
        taskExecutor.setQueueCapacity(executorConfig.getQueueSize());
        taskExecutor.setKeepAliveSeconds(executorConfig.getKeepAliveSeconds());
        taskExecutor.setThreadNamePrefix(executorConfig.getThreadNamePrefix());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(executorConfig.isWaitForTasksToCompleteOnShutdown());
        taskExecutor.setAwaitTerminationMillis(executorConfig.getAwaitTerminationMillis());
        taskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        taskExecutor.setTaskDecorator(taskDecorator);
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
    public static class Exclude {
        private Set<String> runtimeExclude;
    }
}
