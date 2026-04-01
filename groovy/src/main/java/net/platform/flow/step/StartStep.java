
package net.platform.flow.step;

import net.platform.flow.process.FlowContext;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
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

    @Override
    public String getName() {
        return "start";
    }
}
