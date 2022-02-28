package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectsDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(".use_effects", scope))
            return;

        scope.put("effect", scope, new LambdaBaseFunction(EffectsDSL::findEffect));
        scope.put("effectInstance", scope, new LambdaBaseFunction(EffectsDSL::makeEffectInstance));

        scope.put(".use_effects", scope, true);
    }

    private static Object findEffect(Context _cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        return new NativeJavaObject(
                ScriptableObject.getTopLevelScope(scope),
                DSLHelpers.find(ForgeRegistries.MOB_EFFECTS, (String)args[0]),
                MobEffect.class);
    }

    private static Object makeEffectInstance(Context _cx, Scriptable _scope, Scriptable thisObj, Object[] args)
    {
        var effect = args[0] instanceof String str
                ? DSLHelpers.find(ForgeRegistries.MOB_EFFECTS, str)
                : (MobEffect)((NativeJavaObject)args[0]).unwrap();
        var duration = ((Number) args[1]).intValue();
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
            visible = (boolean)args[4];
        }
        return new NativeJavaObject(_scope, new MobEffectInstance(effect, duration, amplifier, ambient, visible), MobEffectInstance.class);
    }
}
