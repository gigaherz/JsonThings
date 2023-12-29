package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.gigaherz.rhinolib.Context;
import dev.gigaherz.rhinolib.Scriptable;

public class LevelsDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_levels", scope))
            return;

        scope.put(cx, ".use_levels", scope, true);
    }
}
