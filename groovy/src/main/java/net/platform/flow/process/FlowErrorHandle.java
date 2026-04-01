
package net.platform.flow.process;

import net.platform.flow.step.EdgeMatcher;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface FlowErrorHandle {
    /**
     * 处理
     * @param ctx 上下文
     * @param e 异常
     * @param name 人名
     * @return 返回条件
     */
    EdgeMatcher handle(FlowContext<String, Object> ctx, Exception e, String name);
}
