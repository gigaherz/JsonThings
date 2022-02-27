package dev.gigaherz.jsonthings.things.scripting.scriptengine;
/*
import dev.gigaherz.jsonthings.things.events.ContextValue;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import net.minecraftforge.common.util.Lazy;

import java.util.List;
import java.util.function.Supplier;

public class FlexEventScriptable
{
    private final FlexEventContext ctx;
    private final Supplier<List<String>> members;

    public FlexEventScriptable(FlexEventContext ctx)
    {
        this.ctx = ctx;
        this.members = Lazy.of(() -> ctx.keySet().stream().map(ContextValue::getName).toList());
    }

    public Object getMember(String key)
    {
        var val = ContextValue.get(key);
        if (ctx.has(val))
        {
            return ctx.get(val);
        }
        throw new RuntimeException("Context Value "+key+" not found");
    }

    public Object getMemberKeys()
    {
        return members;
    }

    public boolean hasMember(String key)
    {
        var val = ContextValue.get(key);
        return ctx.has(val);
    }
}
*/