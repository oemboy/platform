
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
        String dfxLogName = LogUtil.getDfxLogName();
        String dfxLog = LogUtil.getDfxLog();
        RuntimeContext context = ApplicationContextHolder.getRuntimeContext();
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
