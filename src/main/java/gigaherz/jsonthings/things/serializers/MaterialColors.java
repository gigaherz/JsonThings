package gigaherz.jsonthings.things.serializers;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.MaterialColor;

import java.util.Map;

public class MaterialColors
{
    public static final Map<String, MaterialColor> COLORS = ImmutableMap.<String, MaterialColor>builder()
            .put("NONE", MaterialColor.NONE)
            .put("GRASS", MaterialColor.GRASS)
            .put("SAND", MaterialColor.SAND)
            .put("WOOL", MaterialColor.WOOL)
            .put("FIRE", MaterialColor.FIRE)
            .put("ICE", MaterialColor.ICE)
            .put("METAL", MaterialColor.METAL)
            .put("PLANT", MaterialColor.PLANT)
            .put("SNOW", MaterialColor.SNOW)
            .put("CLAY", MaterialColor.CLAY)
            .put("DIRT", MaterialColor.DIRT)
            .put("STONE", MaterialColor.STONE)
            .put("WATER", MaterialColor.WATER)
            .put("WOOD", MaterialColor.WOOD)
            .put("QUARTZ", MaterialColor.QUARTZ)
            .put("COLOR_ORANGE", MaterialColor.COLOR_ORANGE)
            .put("COLOR_MAGENTA", MaterialColor.COLOR_MAGENTA)
            .put("COLOR_LIGHT_BLUE", MaterialColor.COLOR_LIGHT_BLUE)
            .put("COLOR_YELLOW", MaterialColor.COLOR_YELLOW)
            .put("COLOR_LIGHT_GREEN", MaterialColor.COLOR_LIGHT_GREEN)
            .put("COLOR_PINK", MaterialColor.COLOR_PINK)
            .put("COLOR_GRAY", MaterialColor.COLOR_GRAY)
            .put("COLOR_LIGHT_GRAY", MaterialColor.COLOR_LIGHT_GRAY)
            .put("COLOR_CYAN", MaterialColor.COLOR_CYAN)
            .put("COLOR_PURPLE", MaterialColor.COLOR_PURPLE)
            .put("COLOR_BLUE", MaterialColor.COLOR_BLUE)
            .put("COLOR_BROWN", MaterialColor.COLOR_BROWN)
            .put("COLOR_GREEN", MaterialColor.COLOR_GREEN)
            .put("COLOR_RED", MaterialColor.COLOR_RED)
            .put("COLOR_BLACK", MaterialColor.COLOR_BLACK)
            .put("GOLD", MaterialColor.GOLD)
            .put("DIAMOND", MaterialColor.DIAMOND)
            .put("LAPIS", MaterialColor.LAPIS)
            .put("EMERALD", MaterialColor.EMERALD)
            .put("PODZOL", MaterialColor.PODZOL)
            .put("NETHER", MaterialColor.NETHER)
            .put("TERRACOTTA_WHITE", MaterialColor.TERRACOTTA_WHITE)
            .put("TERRACOTTA_ORANGE", MaterialColor.TERRACOTTA_ORANGE)
            .put("TERRACOTTA_MAGENTA", MaterialColor.TERRACOTTA_MAGENTA)
            .put("TERRACOTTA_LIGHT_BLUE", MaterialColor.TERRACOTTA_LIGHT_BLUE)
            .put("TERRACOTTA_YELLOW", MaterialColor.TERRACOTTA_YELLOW)
            .put("TERRACOTTA_LIGHT_GREEN", MaterialColor.TERRACOTTA_LIGHT_GREEN)
            .put("TERRACOTTA_PINK", MaterialColor.TERRACOTTA_PINK)
            .put("TERRACOTTA_GRAY", MaterialColor.TERRACOTTA_GRAY)
            .put("TERRACOTTA_LIGHT_GRAY", MaterialColor.TERRACOTTA_LIGHT_GRAY)
            .put("TERRACOTTA_CYAN", MaterialColor.TERRACOTTA_CYAN)
            .put("TERRACOTTA_PURPLE", MaterialColor.TERRACOTTA_PURPLE)
            .put("TERRACOTTA_BLUE", MaterialColor.TERRACOTTA_BLUE)
            .put("TERRACOTTA_BROWN", MaterialColor.TERRACOTTA_BROWN)
            .put("TERRACOTTA_GREEN", MaterialColor.TERRACOTTA_GREEN)
            .put("TERRACOTTA_RED", MaterialColor.TERRACOTTA_RED)
            .put("TERRACOTTA_BLACK", MaterialColor.TERRACOTTA_BLACK)
            .put("CRIMSON_NYLIUM", MaterialColor.CRIMSON_NYLIUM)
            .put("CRIMSON_STEM", MaterialColor.CRIMSON_STEM)
            .put("CRIMSON_HYPHAE", MaterialColor.CRIMSON_HYPHAE)
            .put("WARPED_NYLIUM", MaterialColor.WARPED_NYLIUM)
            .put("WARPED_STEM", MaterialColor.WARPED_STEM)
            .put("WARPED_HYPHAE", MaterialColor.WARPED_HYPHAE)
            .put("WARPED_WART_BLOCK", MaterialColor.WARPED_WART_BLOCK)
            .build();
    
    public static void init()
    {
        /* nothing to do */
    }

    public static MaterialColor get(String mapColor)
    {
        MaterialColor color = COLORS.get(mapColor);
        if (color == null)
            throw new IllegalStateException("No block map color known with name " + mapColor);
        return color;
    }
}
