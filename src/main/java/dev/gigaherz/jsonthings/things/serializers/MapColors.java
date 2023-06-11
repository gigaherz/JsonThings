package dev.gigaherz.jsonthings.things.serializers;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.material.MapColor;

import java.util.Locale;
import java.util.Map;

public class MapColors
{
    public static final Map<String, MapColor> COLORS = ImmutableMap.<String, MapColor>builder()
            .put("NONE", MapColor.NONE)
            .put("GRASS", MapColor.GRASS)
            .put("SAND", MapColor.SAND)
            .put("WOOL", MapColor.WOOL)
            .put("FIRE", MapColor.FIRE)
            .put("ICE", MapColor.ICE)
            .put("METAL", MapColor.METAL)
            .put("PLANT", MapColor.PLANT)
            .put("SNOW", MapColor.SNOW)
            .put("CLAY", MapColor.CLAY)
            .put("DIRT", MapColor.DIRT)
            .put("STONE", MapColor.STONE)
            .put("WATER", MapColor.WATER)
            .put("WOOD", MapColor.WOOD)
            .put("QUARTZ", MapColor.QUARTZ)
            .put("COLOR_ORANGE", MapColor.COLOR_ORANGE)
            .put("COLOR_MAGENTA", MapColor.COLOR_MAGENTA)
            .put("COLOR_LIGHT_BLUE", MapColor.COLOR_LIGHT_BLUE)
            .put("COLOR_YELLOW", MapColor.COLOR_YELLOW)
            .put("COLOR_LIGHT_GREEN", MapColor.COLOR_LIGHT_GREEN)
            .put("COLOR_PINK", MapColor.COLOR_PINK)
            .put("COLOR_GRAY", MapColor.COLOR_GRAY)
            .put("COLOR_LIGHT_GRAY", MapColor.COLOR_LIGHT_GRAY)
            .put("COLOR_CYAN", MapColor.COLOR_CYAN)
            .put("COLOR_PURPLE", MapColor.COLOR_PURPLE)
            .put("COLOR_BLUE", MapColor.COLOR_BLUE)
            .put("COLOR_BROWN", MapColor.COLOR_BROWN)
            .put("COLOR_GREEN", MapColor.COLOR_GREEN)
            .put("COLOR_RED", MapColor.COLOR_RED)
            .put("COLOR_BLACK", MapColor.COLOR_BLACK)
            .put("GOLD", MapColor.GOLD)
            .put("DIAMOND", MapColor.DIAMOND)
            .put("LAPIS", MapColor.LAPIS)
            .put("EMERALD", MapColor.EMERALD)
            .put("PODZOL", MapColor.PODZOL)
            .put("NETHER", MapColor.NETHER)
            .put("TERRACOTTA_WHITE", MapColor.TERRACOTTA_WHITE)
            .put("TERRACOTTA_ORANGE", MapColor.TERRACOTTA_ORANGE)
            .put("TERRACOTTA_MAGENTA", MapColor.TERRACOTTA_MAGENTA)
            .put("TERRACOTTA_LIGHT_BLUE", MapColor.TERRACOTTA_LIGHT_BLUE)
            .put("TERRACOTTA_YELLOW", MapColor.TERRACOTTA_YELLOW)
            .put("TERRACOTTA_LIGHT_GREEN", MapColor.TERRACOTTA_LIGHT_GREEN)
            .put("TERRACOTTA_PINK", MapColor.TERRACOTTA_PINK)
            .put("TERRACOTTA_GRAY", MapColor.TERRACOTTA_GRAY)
            .put("TERRACOTTA_LIGHT_GRAY", MapColor.TERRACOTTA_LIGHT_GRAY)
            .put("TERRACOTTA_CYAN", MapColor.TERRACOTTA_CYAN)
            .put("TERRACOTTA_PURPLE", MapColor.TERRACOTTA_PURPLE)
            .put("TERRACOTTA_BLUE", MapColor.TERRACOTTA_BLUE)
            .put("TERRACOTTA_BROWN", MapColor.TERRACOTTA_BROWN)
            .put("TERRACOTTA_GREEN", MapColor.TERRACOTTA_GREEN)
            .put("TERRACOTTA_RED", MapColor.TERRACOTTA_RED)
            .put("TERRACOTTA_BLACK", MapColor.TERRACOTTA_BLACK)
            .put("CRIMSON_NYLIUM", MapColor.CRIMSON_NYLIUM)
            .put("CRIMSON_STEM", MapColor.CRIMSON_STEM)
            .put("CRIMSON_HYPHAE", MapColor.CRIMSON_HYPHAE)
            .put("WARPED_NYLIUM", MapColor.WARPED_NYLIUM)
            .put("WARPED_STEM", MapColor.WARPED_STEM)
            .put("WARPED_HYPHAE", MapColor.WARPED_HYPHAE)
            .put("WARPED_WART_BLOCK", MapColor.WARPED_WART_BLOCK)
            .put("DEEPSLATE", MapColor.DEEPSLATE)
            .put("RAW_IRON", MapColor.RAW_IRON)
            .put("GLOW_LICHEN", MapColor.GLOW_LICHEN)
            .build();

    public static void init()
    {
        /* nothing to do */
    }

    public static MapColor get(String mapColor)
    {
        MapColor color = COLORS.get(mapColor.toUpperCase(Locale.ROOT));
        if (color == null)
            throw new IllegalStateException("No block map color known with name " + mapColor);
        return color;
    }
}
