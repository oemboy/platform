
package net.platform.flow.step;

import net.platform.flow.process.FlowContext;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface Step {
    /**
     * 获取类型
     * @return 返回类型
     */
    StepType getType();

    /**
     * 匹配路径
     * @param ctx 上下文
     * @return 匹配条件
     * @param <K> key
     * @param <V> value
     */
    <K, V> EdgeMatcher run(FlowContext<K, V> ctx);

    /**
     * 获取name
     * @return name
     */
    default String getName() {
        return "";
    }
}
