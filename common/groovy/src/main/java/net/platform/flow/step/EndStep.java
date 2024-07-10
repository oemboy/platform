
package net.platform.flow.step;

import net.platform.flow.process.FlowContext;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public abstract class EndStep implements Step {
    public static final EndStep SIMPLE = new EndStep() {
        @Override
        public <K, V> EdgeMatcher run(FlowContext<K, V> ctx) {
            return EdgeMatcher.ANY;
        }
    };

    @Override
    public StepType getType() {
        return StepType.END;
    }

    @Override
    public String getName() {
        return "end";
    }
}