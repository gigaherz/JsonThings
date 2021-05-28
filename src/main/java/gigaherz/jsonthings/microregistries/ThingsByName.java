package gigaherz.jsonthings.microregistries;

import com.google.common.collect.Maps;
import net.minecraft.item.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;

public class ThingsByName
{
    public static Map<String, IItemTier> ITEM_TIERS = Maps.newHashMap();
    public static Map<String, IArmorMaterial> ARMOR_TIERS = Maps.newHashMap();
    public static Map<String, Food> FOODSTUFFS = Maps.newHashMap();
    public static Map<String, PropertyType> PROPERTY_TYPES = Maps.newHashMap();
    public static Map<String, Property<?>> PROPERTIES = Maps.newHashMap();

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

        PROPERTIES.put("attached", BlockStateProperties.ATTACHED);
        PROPERTIES.put("bottom", BlockStateProperties.BOTTOM);
        PROPERTIES.put("conditional", BlockStateProperties.CONDITIONAL);
        PROPERTIES.put("disarmed", BlockStateProperties.DISARMED);
        PROPERTIES.put("drag", BlockStateProperties.DRAG);
        PROPERTIES.put("enabled", BlockStateProperties.ENABLED);
        PROPERTIES.put("extended", BlockStateProperties.EXTENDED);
        PROPERTIES.put("eye", BlockStateProperties.EYE);
        PROPERTIES.put("falling", BlockStateProperties.FALLING);
        PROPERTIES.put("hanging", BlockStateProperties.HANGING);
        PROPERTIES.put("has_bottle_0", BlockStateProperties.HAS_BOTTLE_0);
        PROPERTIES.put("has_bottle_1", BlockStateProperties.HAS_BOTTLE_1);
        PROPERTIES.put("has_bottle_2", BlockStateProperties.HAS_BOTTLE_2);
        PROPERTIES.put("has_record", BlockStateProperties.HAS_RECORD);
        PROPERTIES.put("has_book", BlockStateProperties.HAS_BOOK);
        PROPERTIES.put("inverted", BlockStateProperties.INVERTED);
        PROPERTIES.put("in_wall", BlockStateProperties.IN_WALL);
        PROPERTIES.put("lit", BlockStateProperties.LIT);
        PROPERTIES.put("locked", BlockStateProperties.LOCKED);
        PROPERTIES.put("occupied", BlockStateProperties.OCCUPIED);
        PROPERTIES.put("open", BlockStateProperties.OPEN);
        PROPERTIES.put("persistent", BlockStateProperties.PERSISTENT);
        PROPERTIES.put("powered", BlockStateProperties.POWERED);
        PROPERTIES.put("short", BlockStateProperties.SHORT);
        PROPERTIES.put("signal_fire", BlockStateProperties.SIGNAL_FIRE);
        PROPERTIES.put("snowy", BlockStateProperties.SNOWY);
        PROPERTIES.put("triggered", BlockStateProperties.TRIGGERED);
        PROPERTIES.put("unstable", BlockStateProperties.UNSTABLE);
        PROPERTIES.put("waterlogged", BlockStateProperties.WATERLOGGED);
        PROPERTIES.put("vine_end", BlockStateProperties.VINE_END);
        PROPERTIES.put("horizontal_axis", BlockStateProperties.HORIZONTAL_AXIS);
        PROPERTIES.put("axis", BlockStateProperties.AXIS);
        PROPERTIES.put("up", BlockStateProperties.UP);
        PROPERTIES.put("down", BlockStateProperties.DOWN);
        PROPERTIES.put("north", BlockStateProperties.NORTH);
        PROPERTIES.put("east", BlockStateProperties.EAST);
        PROPERTIES.put("south", BlockStateProperties.SOUTH);
        PROPERTIES.put("west", BlockStateProperties.WEST);
        PROPERTIES.put("facing", BlockStateProperties.FACING);
        PROPERTIES.put("facing_except_up", BlockStateProperties.FACING_EXCEPT_UP);
        PROPERTIES.put("horizontal_facing", BlockStateProperties.HORIZONTAL_FACING);
        PROPERTIES.put("orientation", BlockStateProperties.ORIENTATION);
        PROPERTIES.put("face", BlockStateProperties.FACE);
        PROPERTIES.put("bell_attachment", BlockStateProperties.BELL_ATTACHMENT);
        PROPERTIES.put("wall_height_east", BlockStateProperties.WALL_HEIGHT_EAST);
        PROPERTIES.put("wall_height_north", BlockStateProperties.WALL_HEIGHT_NORTH);
        PROPERTIES.put("wall_height_south", BlockStateProperties.WALL_HEIGHT_SOUTH);
        PROPERTIES.put("wall_height_west", BlockStateProperties.WALL_HEIGHT_WEST);
        PROPERTIES.put("redstone_east", BlockStateProperties.REDSTONE_EAST);
        PROPERTIES.put("redstone_north", BlockStateProperties.REDSTONE_NORTH);
        PROPERTIES.put("redstone_south", BlockStateProperties.REDSTONE_SOUTH);
        PROPERTIES.put("redstone_west", BlockStateProperties.REDSTONE_WEST);
        PROPERTIES.put("double_block_half", BlockStateProperties.DOUBLE_BLOCK_HALF);
        PROPERTIES.put("half", BlockStateProperties.HALF);
        PROPERTIES.put("rail_shape", BlockStateProperties.RAIL_SHAPE);
        PROPERTIES.put("rail_shape_straight", BlockStateProperties.RAIL_SHAPE_STRAIGHT);
        PROPERTIES.put("age_0_1", BlockStateProperties.AGE_0_1);
        PROPERTIES.put("age_0_2", BlockStateProperties.AGE_0_2);
        PROPERTIES.put("age_0_3", BlockStateProperties.AGE_0_3);
        PROPERTIES.put("age_0_5", BlockStateProperties.AGE_0_5);
        PROPERTIES.put("age_0_7", BlockStateProperties.AGE_0_7);
        PROPERTIES.put("age_0_15", BlockStateProperties.AGE_0_15);
        PROPERTIES.put("age_0_25", BlockStateProperties.AGE_0_25);
        PROPERTIES.put("bites_0_6", BlockStateProperties.BITES_0_6);
        PROPERTIES.put("delay_1_4", BlockStateProperties.DELAY_1_4);
        PROPERTIES.put("distance_1_7", BlockStateProperties.DISTANCE_1_7);
        PROPERTIES.put("eggs_1_4", BlockStateProperties.EGGS_1_4);
        PROPERTIES.put("hatch_0_2", BlockStateProperties.HATCH_0_2);
        PROPERTIES.put("layers_1_8", BlockStateProperties.LAYERS_1_8);
        PROPERTIES.put("level_0_3", BlockStateProperties.LEVEL_0_3);
        PROPERTIES.put("level_0_8", BlockStateProperties.LEVEL_0_8);
        PROPERTIES.put("level_1_8", BlockStateProperties.LEVEL_1_8);
        PROPERTIES.put("honey_level", BlockStateProperties.HONEY_LEVEL);
        PROPERTIES.put("level_0_15", BlockStateProperties.LEVEL_0_15);
        PROPERTIES.put("moisture_0_7", BlockStateProperties.MOISTURE_0_7);
        PROPERTIES.put("note_0_24", BlockStateProperties.NOTE_0_24);
        PROPERTIES.put("pickles_1_4", BlockStateProperties.PICKLES_1_4);
        PROPERTIES.put("power_0_15", BlockStateProperties.POWER_0_15);
        PROPERTIES.put("stage_0_1", BlockStateProperties.STAGE_0_1);
        PROPERTIES.put("distance_0_7", BlockStateProperties.DISTANCE_0_7);
        PROPERTIES.put("charges", BlockStateProperties.CHARGES);
        PROPERTIES.put("rotation_0_15", BlockStateProperties.ROTATION_0_15);
        PROPERTIES.put("bed_part", BlockStateProperties.BED_PART);
        PROPERTIES.put("chest_type", BlockStateProperties.CHEST_TYPE);
        PROPERTIES.put("comparator_mode", BlockStateProperties.COMPARATOR_MODE);
        PROPERTIES.put("door_hinge", BlockStateProperties.DOOR_HINGE);
        PROPERTIES.put("note_block_instrument", BlockStateProperties.NOTE_BLOCK_INSTRUMENT);
        PROPERTIES.put("piston_type", BlockStateProperties.PISTON_TYPE);
        PROPERTIES.put("slab_type", BlockStateProperties.SLAB_TYPE);
        PROPERTIES.put("stairs_shape", BlockStateProperties.STAIRS_SHAPE);
        PROPERTIES.put("structure_block_mode", BlockStateProperties.STRUCTURE_BLOCK_MODE);
        PROPERTIES.put("bamboo_leaves", BlockStateProperties.BAMBOO_LEAVES);

        PROPERTY_TYPES.put("boolean", new PropertyType.BoolType());
        PROPERTY_TYPES.put("int", new PropertyType.RangeType<>(IntegerProperty.class, IntegerProperty::create, js -> js.getAsJsonPrimitive().getAsInt()));
        PROPERTY_TYPES.put("string", new PropertyType.StringType());
        PROPERTY_TYPES.put("direction", new PropertyType.DirectionType());
        PROPERTY_TYPES.put("enum", new PropertyType.EnumType());
    }
}
