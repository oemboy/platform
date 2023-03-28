
package net.platform.flow.process;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class DefaultFlowContext implements FlowContext<String, Object> {
    private final Map<String, Object> context = new HashMap<>();

    @Override
    public void put(String key, Object value) {
        context.put(key, value);
    }

    @Override
    public Object get(String key) {
        return context.get(key);
    }
}
