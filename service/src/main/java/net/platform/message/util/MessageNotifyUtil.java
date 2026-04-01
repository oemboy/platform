
package net.platform.message.util;

import java.util.Objects;
import java.util.Optional;

import net.platform.center.util.ApplicationContextHolder;
import net.platform.message.bean.NotifyMessageInfo;
import net.platform.message.service.SystemMessageNotifyService;

/**
 * 消息通知工具类
 *
 * @author Jack
 * @since 0.0.3
 */
public final class MessageNotifyUtil {
    private static final Object LOCK = new Object();

    private static SystemMessageNotifyService systemMessageNotifyService;

    private MessageNotifyUtil() {
    }

    static SystemMessageNotifyService getMessageNotifyService() {
        if (Objects.isNull(systemMessageNotifyService)) {
            synchronized (LOCK) {
                systemMessageNotifyService = Optional.ofNullable(systemMessageNotifyService)
                    .orElse(ApplicationContextHolder.getBean(SystemMessageNotifyService.class));
            }
        }
        return systemMessageNotifyService;
    }

    public static Long addNotifyMessageInfo(NotifyMessageInfo notifyMessage) {
        return getMessageNotifyService().addNotifyMessageInfo(notifyMessage);
    }
}
