package dev.gigaherz.jsonthings.things.client;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.BlockItem;
import net.minecraft.world.GrassColors;

import java.util.Map;
import java.util.function.Function;

public class ItemColorHandler
{
    private static final Map<String, Function<BlockColors, IItemColor>> colorHandlersByName = Maps.newHashMap();

    public static void init()
    {
        register("tall_grass", blockColors -> (stack, color) -> {
            return GrassColors.get(0.5D, 1.0D);
        });

        register("foliage", blockColors -> (stack, color) -> {
            BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(blockstate, null, null, color);
        });
    }

    public static void register(String name, Function<BlockColors, IItemColor> handler)
    {
        colorHandlersByName.put(name, handler);
    }

    public static Function<BlockColors, IItemColor> get(String handlerName)
    {
        if (!colorHandlersByName.containsKey(handlerName))
            throw new IllegalStateException("No item color handler known with name " + handlerName);
        return colorHandlersByName.get(handlerName);
    }
}
