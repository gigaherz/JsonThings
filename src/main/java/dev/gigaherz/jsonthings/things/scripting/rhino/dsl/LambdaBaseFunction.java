package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.gigaherz.jsonthings.things.scripting.rhino.RhinoThingScript;
import dev.gigaherz.rhinolib.BaseFunction;
import dev.gigaherz.rhinolib.Context;
import dev.gigaherz.rhinolib.Scriptable;

public class LambdaBaseFunction extends BaseFunction
{
    private final RhinoThingScript.LambdaFunction impl;

    public LambdaBaseFunction(RhinoThingScript.LambdaFunction impl)
    {
        this.impl = impl;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        return impl.call(cx, scope, thisObj, args);
    }
}
