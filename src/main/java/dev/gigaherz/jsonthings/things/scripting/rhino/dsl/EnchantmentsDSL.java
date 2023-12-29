package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.gigaherz.rhinolib.Context;
import dev.gigaherz.rhinolib.NativeJavaObject;
import dev.gigaherz.rhinolib.Scriptable;
import dev.gigaherz.rhinolib.ScriptableObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class EnchantmentsDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_enchantments", scope))
            return;

        scope.put(cx, "enchantment", scope, new LambdaBaseFunction(EnchantmentsDSL::findEnchantment));

        scope.put(cx, ".use_enchantments", scope, true);
    }

    private static Object findEnchantment(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        return new NativeJavaObject(
                ScriptableObject.getTopLevelScope(scope),
                DSLHelpers.find(BuiltInRegistries.ENCHANTMENT, (String) args[0]),
                Item.class,
                cx);
    }
}
