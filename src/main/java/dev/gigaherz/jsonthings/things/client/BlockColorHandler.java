package dev.gigaherz.jsonthings.things.client;

import com.google.common.collect.Maps;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.FoliageColor;

import java.util.List;
import java.util.Map;

public class BlockColorHandler
{
    private static final Map<String, List<BlockTintSource>> colorHandlersByName = Maps.newHashMap();
    public static void init()
    {
        register("tall_grass", BlockTintSources.doubleTallGrass());
        register("grass", BlockTintSources.grass());
        register("grass_block", BlockTintSources.grassBlock());
        register("spruce", BlockTintSources.constant(FoliageColor.FOLIAGE_EVERGREEN));
        register("birch", BlockTintSources.constant(FoliageColor.FOLIAGE_BIRCH));
        register("mangrove", BlockTintSources.constant(FoliageColor.FOLIAGE_MANGROVE));
        register("foliage", BlockTintSources.foliage());
        register("dry_foliage", BlockTintSources.dryFoliage());
        register("water", BlockTintSources.water());
        register("redstone", BlockTintSources.redstone());
        register("sugarcane", BlockTintSources.sugarCane());
        register("stem", BlockTintSources.stem());
        register("attached_stem", BlockTintSources.constant(0xffe0c71c));
    }

    public static void register(String name, BlockTintSource... handler)
    {
        colorHandlersByName.put(name, List.of(handler));
    }

    public static List<BlockTintSource> get(String handlerName)
    {
        if (!colorHandlersByName.containsKey(handlerName))
            throw new IllegalStateException("No block color handler known with name " + handlerName);
        return colorHandlersByName.get(handlerName);
    }
}
