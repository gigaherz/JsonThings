package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

public class AttributesDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_attributes", scope))
            return;

        scope.put(cx, ".use_attributes", scope, true);
    }
}
