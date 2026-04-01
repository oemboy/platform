
package net.platform.center.cfg.executor;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author bing zi
 * @since 2026/3/3 15:22
 */

@Data
@ConfigurationProperties(prefix = "spring.visible")
public class VisibleExecutorProperties {
    // 是否开启自定义 VisibleExecutor 自动配置
    private boolean enabled = true;

    private Executor executor = new Executor();

    private Exclude exclude = new Exclude();

    @Data
    public static class Executor {
        private int coreThreadNumber = 8;

        private int maxTheadNumber = 16;

        private int queueSize = 512;

        private int keepAliveSeconds = 60;

        private String threadNamePrefix = "visible-exec-";

        private boolean waitForTasksToCompleteOnShutdown = true;

        private long awaitTerminationMillis = 30000;
    }

    @Data
    public static class Exclude {
        private Set<String> runtimeExclude = Set.of();
    }
}
