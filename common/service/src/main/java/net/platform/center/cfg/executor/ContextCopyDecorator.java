
package net.platform.center.cfg.executor;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.core.task.TaskDecorator;

import lombok.Data;
import net.platform.center.util.ApplicationContextHolder;
import net.platform.center.util.LogUtil;
import net.platform.center.util.RuntimeContext;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Data
public class ContextCopyDecorator implements TaskDecorator {

    private VisibleExecutorConfig.Exclude excludeConfig;

    public ContextCopyDecorator() {
    }

    public ContextCopyDecorator(VisibleExecutorConfig.Exclude excludeConfig) {
        this.excludeConfig = excludeConfig;
    }

    /**
     * 根据配置移除后的上下文, 以防止异步线程的上下文cpoy数据, 剔除不能复制的上下文数据
     * 
     * @param context 上下文
     * @return 根据配置移除后的上下文
     */
    protected RuntimeContext copyRuntimeContext(RuntimeContext context) {
        final RuntimeContext newContext = new RuntimeContext();
        newContext.setReqLocalTime(context.getReqLocalTime());
        newContext.setRespLocalTime(context.getRespLocalTime());
        newContext.setDownSystemReqLocalTime(context.getDownSystemReqLocalTime());
        newContext.setDownSystemRespLocalTime(context.getDownSystemRespLocalTime());

        // 复制Regions
        final Deque<String> regions = context.getRegions();
        if (Objects.nonNull(regions)) {
            newContext.setRegions(new LinkedList<>(regions));
        }

        final Map<String, Object> contextMap = context.getContextMap();
        if (Objects.nonNull(contextMap)) {
            final Set<String> excludeKey = Optional.ofNullable(getExcludeConfig())
                .map(VisibleExecutorConfig.Exclude::getRuntimeExclude)
                .orElse(Collections.emptySet());

            newContext.setContextMap(contextMap.entrySet()
                .stream()
                .filter(e -> !excludeKey.contains(e.getKey()))
                .collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o2, ConcurrentHashMap::new)));
        }
        return newContext;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        final String logId = LogUtil.getLogId();
        final String dfxLogName = LogUtil.getDfxLogName();
        final String dfxLog = LogUtil.getDfxLog();
        final RuntimeContext context = copyRuntimeContext(ApplicationContextHolder.getRuntimeContext());
        return () -> {
            try {
                LogUtil.setMdcInfo(logId);
                LogUtil.setMdcDfxInfo(dfxLogName, dfxLog);
                ApplicationContextHolder.setRuntimeContext(context);
                runnable.run();
            } finally {
                ApplicationContextHolder.clearRuntimeContext();
                LogUtil.clearMdcInfo();
            }
        };
    }
}
