
package net.platform.flow.process;

import static net.platform.center.exception.ExceptionEnum.ERR_COMMON_VALID;

import java.util.Set;

import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.exception.BusinessException;
import net.platform.flow.step.EdgeMatcher;
import net.platform.flow.step.EndStep;
import net.platform.flow.step.StartStep;
import net.platform.flow.step.Step;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public class Flow {
    private static final StartStep START_STEP = StartStep.SIMPLE;

    private static final EndStep END_STEP = EndStep.SIMPLE;

    private ImmutableValueGraph<Step, EdgeMatcher> graph;

    public Flow(ValueGraph<Step, EdgeMatcher> mutableGraph) {
        graph = ImmutableValueGraph.copyOf(mutableGraph);
    }

    public static class Builder {
        private MutableValueGraph<Step, EdgeMatcher> mutableValueGraph;

        public Builder() {
            mutableValueGraph = ValueGraphBuilder.directed().allowsSelfLoops(false).build();

            mutableValueGraph.addNode(START_STEP);
            mutableValueGraph.addNode(END_STEP);
        }

        public Builder linkStart(Step to) {
            mutableValueGraph.putEdgeValue(START_STEP, to, EdgeMatcher.ANY);
            return this;
        }

        public Builder linkEnd(Step from, EdgeMatcher matcher) {
            mutableValueGraph.putEdgeValue(from, END_STEP, matcher);
            return this;
        }

        public Builder putEdgeValue(Step from, Step to, EdgeMatcher matcher) {
            mutableValueGraph.putEdgeValue(from, to, matcher);
            return this;
        }

        public Flow build() {
            return new Flow(mutableValueGraph);
        }

    }

    public void run(FlowContext<String, Object> ctx) {
        Step curr = START_STEP;
        while (curr != END_STEP) {
            EdgeMatcher state;
            try {
                log.info("Flow run curr name: {}, type: {}", curr.getName(), curr.getType());
                state = curr.run(ctx);
            } catch (Exception e) {
                log.error("Flow run exception:", e);
                state = errorHandle(ctx, e, curr.getName());
            }

            final Set<Step> successors = graph.successors(curr);
            boolean found = false;
            for (Step successor : successors) {
                EdgeMatcher edgeMatcher = graph.edgeValue(curr, successor).get();
                if (edgeMatcher == EdgeMatcher.ANY || edgeMatcher == state) {
                    curr = successor;
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.error("not found next node!!!");
                throw new BusinessException(ERR_COMMON_VALID, "The next node is not exist!");
            }
        }

        if (curr == END_STEP) {
            log.info("Flow run curr name: {}, type: {}", curr.getName(), curr.getType());
        }
    }

    public EdgeMatcher errorHandle(FlowContext<String, Object> ctx, Exception e, String name) {
        Object obj = ctx.get("errorHandle");
        if (obj instanceof FlowErrorHandle) {
            FlowErrorHandle handler = (FlowErrorHandle) obj;
            return handler.handle(ctx, e, name);
        } else {
            throw new BusinessException(ERR_COMMON_VALID, e);
        }
    }
}
