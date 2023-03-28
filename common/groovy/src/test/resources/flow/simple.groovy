import net.platform.flow.step.EdgeMatcher

flow {
    node('action1')({
        ctx ->
            ctx.put("data", 1)
    })
    node('action2')({
        ctx ->
            def i = ctx.get("data")
            ctx.put("data", i + 2);
    })
    node('action3')({
        ctx ->
            def i = ctx.get("data")
            ctx.put("data", i + 4);
    })
    condition('condition')({
        ctx -> EdgeMatcher.FALSE;
    })

    start 'action1'
    end 'action2'
    end 'action3'
    edge 'action1' to 'condition'
    when true edge 'condition' to 'action2'
    when false edge 'condition' to 'action3'
}