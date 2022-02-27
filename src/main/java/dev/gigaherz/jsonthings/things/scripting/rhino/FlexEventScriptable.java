package dev.gigaherz.jsonthings.things.scripting.rhino;

import dev.gigaherz.jsonthings.things.events.ContextValue;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;

public class FlexEventScriptable extends NativeJavaObject
{
    private final FlexEventContext ctx;
    private final Scriptable scope;

    public FlexEventScriptable(Scriptable scope, FlexEventContext ctx)
    {
        super(scope, ctx, FlexEventContext.class);
        this.scope = scope;
        this.ctx = ctx;
    }

    @Override
    public boolean has(String name, Scriptable start)
    {
        var val = ContextValue.get(name);
        if (ctx.has(val))
            return true;
        return super.has(name, start);
    }

    @Override
    public Object get(String name, Scriptable start)
    {
        var val = ContextValue.get(name);
        if (ctx.has(val))
        {
            var rval = ctx.get(val);

            var scope = ScriptableObject.getTopLevelScope(this);
            Context cx = Context.getContext();
            return cx.getWrapFactory().wrap(cx, scope, rval, val.getType());
        }
        return super.get(name, start);
    }
}
