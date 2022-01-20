
package net.platform.center.cfg.executor;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean("visibleExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new VisibleThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreThreadNumber);
        executor.setMaxPoolSize(maxTheadNumber);
        executor.setQueueCapacity(queueSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new ContextCopyDecorator());

        executor.initialize();
        return executor;

    }
}
