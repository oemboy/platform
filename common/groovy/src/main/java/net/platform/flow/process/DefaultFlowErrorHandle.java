
package net.platform.flow.process;

import net.platform.flow.step.EdgeMatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class DefaultFlowErrorHandle implements FlowErrorHandle {
    @Override
    public EdgeMatcher handle(FlowContext<String, Object> ctx, Exception e, String name) {
        ctx.put(name, getErrorMap(e.getMessage()));
        return EdgeMatcher.FALSE;
    }

    public Map<String, Object> getErrorMap(String message) {
        Map<String, Object> errorMap = new HashMap<>(8);
        errorMap.put("code", "0");
        errorMap.put("message", message);
        return errorMap;
    }
}
