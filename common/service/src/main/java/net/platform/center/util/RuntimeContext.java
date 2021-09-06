
package net.platform.center.util;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, String> contextMap = new HashMap<>();

    public RuntimeContext() {
        this.reqLocalTime = LocalDateTime.now();
    }
}
