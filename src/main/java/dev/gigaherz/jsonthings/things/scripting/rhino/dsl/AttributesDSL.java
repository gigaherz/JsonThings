package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.gigaherz.rhinolib.Context;
import dev.gigaherz.rhinolib.Scriptable;

public class AttributesDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_attributes", scope))
            return;

        scope.put(cx, ".use_attributes", scope, true);
    }
}
