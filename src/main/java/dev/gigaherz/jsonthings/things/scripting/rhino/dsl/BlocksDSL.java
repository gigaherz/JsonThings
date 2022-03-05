package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.latvian.mods.rhino.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

public class BlocksDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(".use_blocks", scope))
            return;

        scope.put("block", scope, new LambdaBaseFunction(BlocksDSL::findBlock));
        scope.put("blockState", scope, new LambdaBaseFunction(BlocksDSL::makeBlockState));

        scope.put(".use_blocks", scope, true);
    }

    private static Object findBlock(Context _cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        return new NativeJavaObject(
                ScriptableObject.getTopLevelScope(scope),
                DSLHelpers.find(ForgeRegistries.BLOCKS, (String)args[0]),
                Block.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object makeBlockState(Context _cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var block = args[0] instanceof String str
                ? DSLHelpers.find(ForgeRegistries.BLOCKS, str)
                : (Block)((NativeJavaObject)args[0]).unwrap();

        var baseState = block.defaultBlockState();
        if (args.length > 1)
        {
            var obj = (NativeObject)args[1];
            var props = baseState.getProperties();
            for(var kv : obj.entrySet())
            {
                var key = (String)kv.getKey();
                var value = kv.getValue();
                if (value instanceof NativeJavaObject wrapped)
                    value = wrapped.unwrap();
                var prop = (Property)props.stream().filter(p -> p.getName().equals(key)).findFirst().orElseThrow();
                baseState = baseState.setValue(prop, (Comparable)value);
            }
        }

        return new NativeJavaObject(ScriptableObject.getTopLevelScope(scope), baseState, ItemStack.class);
    }
}
