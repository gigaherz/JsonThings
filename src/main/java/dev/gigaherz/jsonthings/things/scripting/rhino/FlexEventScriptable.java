package dev.gigaherz.jsonthings.things.scripting.rhino;

import dev.gigaherz.jsonthings.things.events.ContextValue;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.rhinolib.Context;
import dev.gigaherz.rhinolib.NativeJavaObject;
import dev.gigaherz.rhinolib.Scriptable;
import dev.gigaherz.rhinolib.ScriptableObject;

public class FlexEventScriptable extends NativeJavaObject
{
    private final FlexEventContext ctx;

    public FlexEventScriptable(Scriptable scope, FlexEventContext ctx, Context cx)
    {
        super(scope, ctx, FlexEventContext.class, cx);
        this.ctx = ctx;
    }

    @Override
    public boolean has(Context cx, String name, Scriptable start)
    {
        var val = ContextValue.get(name);
        if (ctx.has(val))
            return true;
        return super.has(cx, name, start);
    }

    @Override
    public Object get(Context cx, String name, Scriptable start)
    {
        var val = ContextValue.get(name);
        if (ctx.has(val))
        {
            var rval = ctx.get(val);

            var scope = ScriptableObject.getTopLevelScope(this);
            return cx.getWrapFactory().wrap(cx, scope, rval, val.getType());
        }
        return super.get(cx, name, start);
    }
}
