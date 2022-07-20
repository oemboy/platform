
package net.platform.center.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public class LogUtil {
    public static final String TRANS_IDO = "TRANSIDO";

    public static final String DEBUG_TYPE = "DebugType";

    public static final String USE_DFX_LOG = "useDfxLog";

    private static final String DFX_LOG_NAME = "DFXLogName";

    private static final String DFX_LOG = "DFXLog";

    private LogUtil() {
    }

    public static String getLogId() {
        return MDC.get(TRANS_IDO);
    }

    public static String getDfxLogName() {
        return MDC.get(DFX_LOG_NAME);
    }

    public static String getDfxLog() {
        return MDC.get(DFX_LOG);
    }

    public static void setMdcInfo(String logId) {
        if (StringUtils.isNotBlank(logId)) {
            MDC.put(TRANS_IDO, logId);
        }
    }

    public static void setMdcDfxInfo(String dfxLogName, String dfxLogLevel) {
        if (StringUtils.isNotBlank(dfxLogName)) {
            MDC.put(DFX_LOG_NAME, dfxLogName);
        }
        if (StringUtils.isNotBlank(dfxLogLevel)) {
            MDC.put(DFX_LOG, dfxLogLevel);
        }
    }

    public static void clearMdcInfo() {
        MDC.clear();
    }
}
