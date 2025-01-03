package dev.gigaherz.jsonthings.things.client;

import com.google.common.collect.Maps;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Map;

public class BlockColorHandler
{
    private static final Map<String, BlockColor> colorHandlersByName = Maps.newHashMap();

    public static void init()
    {
        register("tall_grass", (state, reader, pos, color) ->
                reader != null && pos != null
                        ? BiomeColors.getAverageGrassColor(reader, state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos)
                        : -1);
        register("grass", (state, reader, pos, color) ->
                reader != null && pos != null
                        ? BiomeColors.getAverageGrassColor(reader, pos)
                        : GrassColor.get(0.5D, 1.0D));
        register("spruce", (state, reader, pos, color) -> FoliageColor.FOLIAGE_EVERGREEN);
        register("birch", (state, reader, pos, color) -> FoliageColor.FOLIAGE_BIRCH);
        register("mangrove", (state, reader, pos, color) -> FoliageColor.FOLIAGE_MANGROVE);
        register("foliage", (state, reader, pos, color) ->
                reader != null && pos != null
                        ? BiomeColors.getAverageFoliageColor(reader, pos)
                        : FoliageColor.FOLIAGE_DEFAULT);
        register("water", (state, reader, pos, color) -> reader != null && pos != null
                ? BiomeColors.getAverageWaterColor(reader, pos)
                : -1);
        register("redstone", (state, reader, pos, color) ->
                RedStoneWireBlock.getColorForPower(state.getValue(RedStoneWireBlock.POWER)));
        register("sugarcane", (state, reader, pos, color) ->
                reader != null && pos != null
                        ? BiomeColors.getAverageGrassColor(reader, pos)
                        : -1);

        // learn what this does and if it's needed
        //blockcolors.addColoringState(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
    }

    public static void register(String name, BlockColor handler)
    {
        colorHandlersByName.put(name, handler);
    }

    public static BlockColor get(String handlerName)
    {
        if (!colorHandlersByName.containsKey(handlerName))
            throw new IllegalStateException("No block color handler known with name " + handlerName);
        return colorHandlersByName.get(handlerName);
    }
}
