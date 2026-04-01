
package net.platform.center.cfg.executor;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
@AutoConfiguration
@EnableConfigurationProperties(VisibleExecutorProperties.class)
@ConditionalOnProperty(prefix = "spring.visible", name = "enabled", havingValue = "true", matchIfMissing = true)
public class VisibleExecutorAutoConfiguration {
    private final VisibleExecutorProperties properties;

    public VisibleExecutorAutoConfiguration(VisibleExecutorProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(TaskDecorator.class)
    public TaskDecorator taskDecorator() {
        return new ContextCopyDecorator(properties);
    }

    @Bean("contextScheduler")
    @ConditionalOnMissingBean(name = "contextScheduler")
    public ThreadPoolTaskScheduler contextScheduler(TaskDecorator taskDecorator) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setTaskDecorator(taskDecorator);
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("context-scheduler-");
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Bean("visibleExecutor")
    @ConditionalOnMissingBean(name = "visibleExecutor")
    public ThreadPoolTaskExecutor visibleExecutor(TaskDecorator taskDecorator,
        @Autowired(required = false) RejectedExecutionHandler rejectedHandler) {

        RejectedExecutionHandler handler =
            Optional.ofNullable(rejectedHandler).orElse(new ThreadPoolExecutor.CallerRunsPolicy());

        return buildThreadPoolTaskExecutor(properties.getExecutor(), handler, taskDecorator);
    }

    private ThreadPoolTaskExecutor buildThreadPoolTaskExecutor(VisibleExecutorProperties.Executor config,
        RejectedExecutionHandler rejectedHandler, TaskDecorator taskDecorator) {

        ThreadPoolTaskExecutor taskExecutor = new VisibleThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(config.getCoreThreadNumber());
        taskExecutor.setMaxPoolSize(config.getMaxTheadNumber());
        taskExecutor.setQueueCapacity(config.getQueueSize());
        taskExecutor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        taskExecutor.setThreadNamePrefix(config.getThreadNamePrefix());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(config.isWaitForTasksToCompleteOnShutdown());
        taskExecutor.setAwaitTerminationMillis(config.getAwaitTerminationMillis());
        taskExecutor.setRejectedExecutionHandler(rejectedHandler);
        taskExecutor.setTaskDecorator(taskDecorator);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
