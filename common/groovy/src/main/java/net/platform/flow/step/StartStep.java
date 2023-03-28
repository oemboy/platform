
package net.platform.flow.step;

import net.platform.flow.process.FlowContext;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public abstract class StartStep implements Step {
    public static final StartStep SIMPLE = new StartStep() {
        @Override
        public <K, V> EdgeMatcher run(FlowContext<K, V> ctx) {
            return EdgeMatcher.ANY;
        }
    };

    @Override
    public StepType getType() {
        return StepType.START;
    }

    /**
     * 匹配条件
     * @param ctx 上下文
     * @return 匹配条件
     * @param <K> key
     * @param <V> value
     */
    @Override
    public abstract <K, V> EdgeMatcher run(FlowContext<K, V> ctx);

    @Override
    public String getName() {
        return "start";
    }
}
