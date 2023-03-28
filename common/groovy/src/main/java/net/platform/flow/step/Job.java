
package net.platform.flow.step;

import net.platform.flow.process.FlowContext;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public abstract class Job implements Step {
    private String name;

    @Override
    public StepType getType() {
        return StepType.ACTION;
    }

    @Override
    public <K, V> EdgeMatcher run(FlowContext<K, V> ctx) {
        execute(ctx);
        return EdgeMatcher.ANY;
    }

    @Override
    public String getName() {
        return this.name;
    }


    /**
     * 执行
     * @param ctx 上下文
     * @param <K> key
     * @param <V> value
     */
    public abstract <K, V> void execute(FlowContext<K, V> ctx);

    public void setName(String name) {
        this.name = name;
    }
}
