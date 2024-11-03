package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.gigaherz.jsonthings.util.Utils;
import dev.gigaherz.rhinolib.Context;
import dev.gigaherz.rhinolib.Scriptable;
import dev.gigaherz.rhinolib.Undefined;
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
        boolean useActionBar = Utils.orElse(args.length > 2 ? DSLHelpers.get(args[2]) : null, true);

        player.displayClientMessage(DSLHelpers.getComponent(cx, args[1]), useActionBar);

        return Undefined.instance;
    }
}
