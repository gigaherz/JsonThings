package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.gigaherz.rhinolib.Context;
import dev.gigaherz.rhinolib.NativeJavaObject;
import dev.gigaherz.rhinolib.NativeObject;
import dev.gigaherz.rhinolib.Scriptable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlocksDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_blocks", scope))
            return;

        scope.put(cx, "block", scope, new LambdaBaseFunction(BlocksDSL::findBlock));
        scope.put(cx, "blockState", scope, new LambdaBaseFunction(BlocksDSL::makeBlockState));

        scope.put(cx, ".use_blocks", scope, true);
    }

    private static Object findBlock(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var block = DSLHelpers.find(BuiltInRegistries.BLOCK, (String) args[0]);
        return DSLHelpers.wrap(cx, scope, block, Block.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object makeBlockState(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var block = DSLHelpers.getRegistryEntry(args[0], BuiltInRegistries.BLOCK);

        var baseState = block.defaultBlockState();
        if (args.length > 1)
        {
            var obj = (NativeObject) args[1];
            var props = baseState.getProperties();
            for (var kv : obj.entrySet())
            {
                var key = (String) kv.getKey();
                var value = kv.getValue();
                if (value instanceof NativeJavaObject wrapped)
                    value = wrapped.unwrap();
                var prop = (Property) props.stream().filter(p -> p.getName().equals(key)).findFirst().orElseThrow();
                baseState = baseState.setValue(prop, (Comparable) value);
            }
        }

        return DSLHelpers.wrap(cx, scope, baseState, BlockState.class);
    }
}
