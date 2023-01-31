package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import net.minecraft.world.entity.player.Player;

public class ChatDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_chat", scope))
            return;

        scope.put(cx, "sendSystemMessage", scope, new LambdaBaseFunction(ChatDSL::sendSystemMessage));

        scope.put(cx, ".use_chat", scope, true);
    }

    private static Object sendSystemMessage(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        Player player = DSLHelpers.get(args[0]);

        player.sendSystemMessage(DSLHelpers.getComponent(cx, args[1]));

        return Undefined.instance;
    }
}
