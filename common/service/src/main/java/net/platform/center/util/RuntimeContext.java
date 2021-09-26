
package net.platform.center.util;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Data
public class RuntimeContext {

    private LocalDateTime reqLocalTime;

    private LocalDateTime respLocalTime;

    private LocalDateTime downSystemReqLocalTime;

    private LocalDateTime downSystemRespLocalTime;

    private Map<String, String> contextMap = new ConcurrentHashMap<>();

    private Stack<String> regions = new Stack<>();

    public RuntimeContext() {
        this.reqLocalTime = LocalDateTime.now();
    }
}
