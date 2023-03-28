package net.platform.flow

import net.platform.flow.process.Flow
import net.platform.flow.step.Condition
import net.platform.flow.step.Job
import net.platform.flow.step.EdgeMatcher

class FlowDSL {
    def builder = new Flow.Builder()
    def actions = [:]

    def start(String name) {
        builder.linkStart(actions[name])
    }

    def end(String name) {
        builder.linkEnd(actions[name], EdgeMatcher.ANY)
    }

    def build() {
        builder.build()
    }

    def condition(String name) {
        {
            it ->
                actions[name] = [judge: it] as Condition
                actions[name].setName(name)
        }
    }

    def node(String name) {
        {
            it ->
                actions[name] = [execute: it] as Job
                actions[name].setName(name)
        }
    }

    def when(boolean condition) {
        [
                edge: {
                    String from ->
                        [
                                to: {
                                    String to ->
                                        if (condition) {
                                            builder.putEdgeValue(actions[from], actions[to], EdgeMatcher.TRUE)
                                        } else {
                                            builder.putEdgeValue(actions[from], actions[to], EdgeMatcher.FALSE)
                                        }
                                }
                        ]
                },
                end : {
                    String name ->
                        if (condition) {
                            builder.linkEnd(actions[name], EdgeMatcher.TRUE)
                        } else {
                            builder.linkEnd(actions[name], EdgeMatcher.FALSE)
                        }
                }
        ]
    }

    def edge(String from) {
        [
                to: {
                    String to -> builder.putEdgeValue(actions[from], actions[to], EdgeMatcher.ANY)
                }

        ]
    }

    static def flow(Closure cl) {
        def flow = new FlowDSL()
        def code = cl.rehydrate(flow, flow, flow)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        return flow.build()
    }
}
