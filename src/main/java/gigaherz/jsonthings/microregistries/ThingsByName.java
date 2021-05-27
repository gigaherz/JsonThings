package gigaherz.jsonthings.microregistries;

import com.google.common.collect.Maps;
import net.minecraft.item.*;
import net.minecraft.state.IntegerProperty;

import java.util.Map;

public class ThingsByName
{
    public static Map<String, IItemTier> ITEM_TIERS = Maps.newHashMap();
    public static Map<String, IArmorMaterial> ARMOR_TIERS = Maps.newHashMap();
    public static Map<String, Food> FOODSTUFFS = Maps.newHashMap();
    public static Map<String, PropertyType> PROPERTY_TYPES = Maps.newHashMap();

    public static void initVanillaThings()
    {
        // no "name" field in item tiers
        ITEM_TIERS.put("wood", ItemTier.WOOD);
        ITEM_TIERS.put("stone", ItemTier.STONE);
        ITEM_TIERS.put("gold", ItemTier.GOLD);
        ITEM_TIERS.put("iron", ItemTier.IRON);
        ITEM_TIERS.put("diamond", ItemTier.DIAMOND);

        for(ArmorMaterial mat : ArmorMaterial.values())
        {
            ARMOR_TIERS.put(mat.getName(), mat);
        }

        FOODSTUFFS.put("apple", Foods.APPLE);
        FOODSTUFFS.put("baked_potato", Foods.BAKED_POTATO);
        FOODSTUFFS.put("beef", Foods.BEEF);
        FOODSTUFFS.put("beetroot", Foods.BEETROOT);
        FOODSTUFFS.put("beetroot_soup", Foods.BEETROOT_SOUP);
        FOODSTUFFS.put("bread", Foods.BREAD);
        FOODSTUFFS.put("carrot", Foods.CARROT);
        FOODSTUFFS.put("chicken", Foods.CHICKEN);
        FOODSTUFFS.put("chorus_fruit", Foods.CHORUS_FRUIT);
        FOODSTUFFS.put("cod", Foods.COD);
        FOODSTUFFS.put("cooked_beef", Foods.COOKED_BEEF);
        FOODSTUFFS.put("cooked_chicken", Foods.COOKED_CHICKEN);
        FOODSTUFFS.put("cooked_cod", Foods.COOKED_COD);
        FOODSTUFFS.put("cooked_mutton", Foods.COOKED_MUTTON);
        FOODSTUFFS.put("cooked_porkchop", Foods.COOKED_PORKCHOP);
        FOODSTUFFS.put("cooked_rabbit", Foods.COOKED_RABBIT);
        FOODSTUFFS.put("cooked_salmon", Foods.COOKED_SALMON);
        FOODSTUFFS.put("cookie", Foods.COOKIE);
        FOODSTUFFS.put("dried_kelp", Foods.DRIED_KELP);
        FOODSTUFFS.put("enchanted_golden_apple", Foods.ENCHANTED_GOLDEN_APPLE);
        FOODSTUFFS.put("golden_apple", Foods.GOLDEN_APPLE);
        FOODSTUFFS.put("golden_carrot", Foods.GOLDEN_CARROT);
        FOODSTUFFS.put("melon_slice", Foods.MELON_SLICE);
        FOODSTUFFS.put("mushroom_stew", Foods.MUSHROOM_STEW);
        FOODSTUFFS.put("mutton", Foods.MUTTON);
        FOODSTUFFS.put("poisonous_potato", Foods.POISONOUS_POTATO);
        FOODSTUFFS.put("porkchop", Foods.PORKCHOP);
        FOODSTUFFS.put("potato", Foods.POTATO);
        FOODSTUFFS.put("pufferfish", Foods.PUFFERFISH);
        FOODSTUFFS.put("pumpkin_pie", Foods.PUMPKIN_PIE);
        FOODSTUFFS.put("rabbit", Foods.RABBIT);
        FOODSTUFFS.put("rabbit_stew", Foods.RABBIT_STEW);
        FOODSTUFFS.put("rotten_flesh", Foods.ROTTEN_FLESH);
        FOODSTUFFS.put("salmon", Foods.SALMON);
        FOODSTUFFS.put("spider_eye", Foods.SPIDER_EYE);
        FOODSTUFFS.put("suspicious_stew", Foods.SUSPICIOUS_STEW);
        FOODSTUFFS.put("sweet_berries", Foods.SWEET_BERRIES);
        FOODSTUFFS.put("tropical_fish", Foods.TROPICAL_FISH);

        PROPERTY_TYPES.put("boolean", new PropertyType.BoolType());
        PROPERTY_TYPES.put("int", new PropertyType.RangeType<>(IntegerProperty.class, IntegerProperty::create, js -> js.getAsJsonPrimitive().getAsInt()));
        PROPERTY_TYPES.put("direction", new PropertyType.DirectionType());
        PROPERTY_TYPES.put("enum", new PropertyType.EnumType());
    }
}
