
package net.platform.flow.step;

import net.platform.flow.process.FlowContext;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public abstract class Condition implements Step {
    private String name;

    @Override
    public StepType getType() {
        return StepType.CONDITION;
    }

    @Override
    public <K, V> EdgeMatcher run(FlowContext<K, V> ctx) {
        return judge(ctx);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 判断条件
     * @param ctx 上下文
     * @return 匹配条件
     * @param <K> key
     * @param <V> value
     */
    protected abstract <K, V> EdgeMatcher judge(FlowContext<K, V> ctx);
}
