package dev.gigaherz.jsonthings.things.client;

import com.google.common.collect.Maps;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.biome.BiomeColors;

import java.util.Map;

public class BlockColorHandler
{
    private static final Map<String, IBlockColor> colorHandlersByName = Maps.newHashMap();

    public static void init()
    {
        register("tall_grass", (state, reader, pos, color) ->
                reader != null && pos != null
                        ? BiomeColors.getAverageGrassColor(reader, state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos)
                        : -1);
        register("grass", (state, reader, pos, color) ->
                reader != null && pos != null
                        ? BiomeColors.getAverageGrassColor(reader, pos)
                        : GrassColors.get(0.5D, 1.0D));
        register("spruce", (state, reader, pos, color) ->
                FoliageColors.getEvergreenColor());
        register("birch", (state, reader, pos, color) ->
                FoliageColors.getBirchColor());
        register("foliage", (state, reader, pos, color) ->
                reader != null && pos != null
                        ? BiomeColors.getAverageFoliageColor(reader, pos)
                        : FoliageColors.getDefaultColor());
        register("water", (state, reader, pos, color) -> reader != null && pos != null
                ? BiomeColors.getAverageWaterColor(reader, pos)
                : -1);
        register("redstone", (state, reader, pos, color) ->
                RedstoneWireBlock.getColorForPower(state.getValue(RedstoneWireBlock.POWER)));
        register("sugarcane", (state, reader, pos, color) ->
                reader != null && pos != null
                        ? BiomeColors.getAverageGrassColor(reader, pos)
                        : -1);

        // learn what this does and if it's needed
        //blockcolors.addColoringState(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
    }

    public static void register(String name, IBlockColor handler)
    {
        colorHandlersByName.put(name, handler);
    }

    public static IBlockColor get(String handlerName)
    {
        if (!colorHandlersByName.containsKey(handlerName))
            throw new IllegalStateException("No block color handler known with name " + handlerName);
        return colorHandlersByName.get(handlerName);
    }
}
