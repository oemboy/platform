
package net.platform.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;

import groovy.lang.GroovyShell;
import net.platform.center.test.ResourceFile;
import net.platform.flow.process.DefaultFlowContext;
import net.platform.flow.process.Flow;
import net.platform.flow.process.FlowContext;
import net.platform.flow.process.FlowGroovyShell;

class FlowTest {
    private GroovyShell shell = new FlowGroovyShell();

    @ParameterizedTest
    @ResourceFile(resources = {"/flow/simple.groovy"})
    void testScriptFlow(String simple) {
        final String key = "data";
        Flow flow = (Flow) loadFlow(simple);
        final FlowContext<String, Object> ctx = new DefaultFlowContext();
        flow.run(ctx);
        assertEquals(5, ctx.get(key));
        flow.run(ctx);
        assertEquals(5, ctx.get(key));
    }

    private Object loadFlow(String simple) {
        return shell.evaluate(simple);
    }

}
