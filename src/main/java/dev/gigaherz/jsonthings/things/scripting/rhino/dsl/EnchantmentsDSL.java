package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

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
                DSLHelpers.find(ForgeRegistries.ENCHANTMENTS, (String)args[0]),
                Item.class,
                cx);
    }
}
