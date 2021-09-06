
package net.platform.center.util;

import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public class LogUtil {
    public static final String TRANS_IDO = "TRANSIDO";

    public static String getLogId() {
        return MDC.get(TRANS_IDO);
    }

    public static void setMdcInfo(String logId) {
        MDC.put(TRANS_IDO, logId);
    }

    public static void clearMdcInfo() {
        MDC.clear();
    }
}
