package dev.gigaherz.jsonthings.things.scripting.graal;
/*
import dev.gigaherz.jsonthings.things.events.ContextValue;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import net.minecraftforge.common.util.Lazy;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.util.List;
import java.util.function.Supplier;

public class FlexEventGraalProxy implements ProxyObject
{
    private final FlexEventContext ctx;
    private final Supplier<List<String>> members;


    public FlexEventGraalProxy(FlexEventContext ctx)
    {
        this.ctx = ctx;
        this.members = Lazy.of(() -> ctx.keySet().stream().map(ContextValue::getName).toList());
    }

    @Override
    public Object getMember(String key)
    {
        var val = ContextValue.get(key);
        if (ctx.has(val))
        {
            return ctx.get(val);
        }
        throw new RuntimeException("Context Value "+key+" not found");
    }

    @Override
    public Object getMemberKeys()
    {
        return members;
    }

    @Override
    public boolean hasMember(String key)
    {
        var val = ContextValue.get(key);
        return ctx.has(val);
    }

    @Override
    public void putMember(String key, Value value)
    {
        throw new UnsupportedOperationException("putMember() not supported.");
    }
}
*/