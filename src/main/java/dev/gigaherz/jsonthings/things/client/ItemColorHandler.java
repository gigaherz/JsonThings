package dev.gigaherz.jsonthings.things.client;

import com.google.common.collect.Maps;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Function;

public class ItemColorHandler
{
    private static final Map<String, Function<BlockColors, ItemColor>> colorHandlersByName = Maps.newHashMap();

    public static void init()
    {
        register("tall_grass", blockColors -> (stack, color) -> {
            return GrassColor.get(0.5D, 1.0D);
        });

        register("foliage", blockColors -> (stack, color) -> {
            BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(blockstate, null, null, color);
        });
    }

    public static void register(String name, Function<BlockColors, ItemColor> handler)
    {
        colorHandlersByName.put(name, handler);
    }

    public static Function<BlockColors, ItemColor> get(String handlerName)
    {
        if (!colorHandlersByName.containsKey(handlerName))
            throw new IllegalStateException("No item color handler known with name " + handlerName);
        return colorHandlersByName.get(handlerName);
    }
}
