package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemsDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_items", scope))
            return;

        scope.put(cx, "item", scope, new LambdaBaseFunction(ItemsDSL::findItem));
        scope.put(cx, "stack", scope, new LambdaBaseFunction(ItemsDSL::makeItemStack));

        scope.put(cx, ".use_items", scope, true);
    }

    private static Object findItem(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var item = DSLHelpers.find(ForgeRegistries.ITEMS, (String) args[0]);
        return DSLHelpers.wrap(cx, scope, item, Item.class);
    }

    private static Object makeItemStack(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var item = DSLHelpers.getRegistryEntry(args[0], ForgeRegistries.ITEMS);

        var stack = new ItemStack(item);
        if (args.length >= 2)
        {
            stack.setCount(((Number) args[1]).intValue());
        }
        if (args.length >= 3)
        {
            var tag = (CompoundTag)NbtDSL.wrapVanillaInternal(args[2]);
            stack.setTag(tag);
        }

        return DSLHelpers.wrap(cx, scope, stack, ItemStack.class);
    }
}