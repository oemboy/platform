
package net.platform.center.cfg.executor;

import org.springframework.core.task.TaskDecorator;

import net.platform.center.util.ApplicationContextHolder;
import net.platform.center.util.LogUtil;
import net.platform.center.util.RuntimeContext;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class ContextCopyDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        String logId = LogUtil.getLogId();
        RuntimeContext context = ApplicationContextHolder.getRuntimeContext();
        return () -> {
            try {
                LogUtil.setMdcInfo(logId);
                ApplicationContextHolder.setRuntimeContext(context);
                runnable.run();
            } finally {
                ApplicationContextHolder.clearRuntimeContext();
                LogUtil.clearMdcInfo();
            }
        };
    }
}
