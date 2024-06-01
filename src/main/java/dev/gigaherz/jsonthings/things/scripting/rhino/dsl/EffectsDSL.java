package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.gigaherz.rhinolib.Context;
import dev.gigaherz.rhinolib.Scriptable;
import dev.gigaherz.rhinolib.ScriptableObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class EffectsDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_effects", scope))
            return;

        scope.put(cx, "effect", scope, new LambdaBaseFunction(EffectsDSL::findEffect));
        scope.put(cx, "effectInstance", scope, new LambdaBaseFunction(EffectsDSL::makeEffectInstance));

        scope.put(cx, ".use_effects", scope, true);
    }

    private static Object findEffect(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var effect = DSLHelpers.find(BuiltInRegistries.MOB_EFFECT, (String) args[0]);
        return DSLHelpers.wrap(cx, ScriptableObject.getTopLevelScope(scope), effect, MobEffect.class);
    }

    private static Object makeEffectInstance(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var effect = DSLHelpers.getRegistryHolder(args[0], BuiltInRegistries.MOB_EFFECT);
        var duration = DSLHelpers.getInt(args[1]);
        var amplifier = 0;
        var ambient = false;
        var visible = true;
        if (args.length >= 3)
        {
            amplifier = ((Number) args[2]).intValue();
        }
        if (args.length >= 4)
        {
            ambient = (boolean) args[3];
        }
        if (args.length >= 5)
        {
            visible = (boolean) args[4];
        }

        var mobEffectInstance = new MobEffectInstance(effect, duration, amplifier, ambient, visible);

        return DSLHelpers.wrap(cx, scope, mobEffectInstance, MobEffectInstance.class);
    }
}
