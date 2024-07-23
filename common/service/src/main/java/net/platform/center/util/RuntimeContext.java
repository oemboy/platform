
package net.platform.center.util;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import net.platform.center.bean.DfxSqlBean;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
@Data
public class RuntimeContext {

    private LocalDateTime reqLocalTime;

    private LocalDateTime respLocalTime;

    private LocalDateTime downSystemReqLocalTime;

    private LocalDateTime downSystemRespLocalTime;

    private Map<String, Object> contextMap = new ConcurrentHashMap<>();

    private Map<DfxSqlBean, String> dfxSqlBeanMap = new ConcurrentHashMap<>(32);

    private Deque<String> regions = new LinkedList<>();

    public RuntimeContext() {
        this.reqLocalTime = LocalDateTime.now();
    }
}
