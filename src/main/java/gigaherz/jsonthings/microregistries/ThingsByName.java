package gigaherz.jsonthings.microregistries;

import com.mojang.serialization.Lifecycle;
import gigaherz.jsonthings.util.DynamicShape;
import net.minecraft.item.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

public class ThingsByName
{
    public static RegistryKey<Registry<IItemTier>> ITEM_TIER_REGISTRY = RegistryKey.getOrCreateRootKey(new ResourceLocation("jsonthings:item_tier"));
    public static RegistryKey<Registry<IArmorMaterial>> ARMOR_MATERIAL_REGISTRY = RegistryKey.getOrCreateRootKey(new ResourceLocation("jsonthings:armor_material"));
    public static RegistryKey<Registry<Food>> FOOD_REGISTRY = RegistryKey.getOrCreateRootKey(new ResourceLocation("jsonthings:food"));
    public static RegistryKey<Registry<PropertyType>> PROPERTY_TYPE_REGISTRY = RegistryKey.getOrCreateRootKey(new ResourceLocation("jsonthings:property_type"));
    public static RegistryKey<Registry<Property<?>>> PROPERTY_REGISTRY = RegistryKey.getOrCreateRootKey(new ResourceLocation("jsonthings:property"));
    public static RegistryKey<Registry<IBooleanFunction>> BOOLEAN_FUNCTION_REGISTRY = RegistryKey.getOrCreateRootKey(new ResourceLocation("jsonthings:boolean_function"));
    public static RegistryKey<Registry<DynamicShape>> DYNAMIC_SHAPE_REGISTRY = RegistryKey.getOrCreateRootKey(new ResourceLocation("jsonthings:dynamic_shapes"));

    public static SimpleRegistry<IItemTier> ITEM_TIERS = new SimpleRegistry<>(ITEM_TIER_REGISTRY, Lifecycle.experimental());
    public static SimpleRegistry<IArmorMaterial> ARMOR_TIERS = new SimpleRegistry<>(ARMOR_MATERIAL_REGISTRY, Lifecycle.experimental());
    public static SimpleRegistry<Food> FOODS = new SimpleRegistry<>(FOOD_REGISTRY, Lifecycle.experimental());
    public static SimpleRegistry<PropertyType> PROPERTY_TYPES = new SimpleRegistry<>(PROPERTY_TYPE_REGISTRY, Lifecycle.experimental());
    public static SimpleRegistry<Property<?>> PROPERTIES = new SimpleRegistry<>(PROPERTY_REGISTRY, Lifecycle.experimental());
    public static SimpleRegistry<DynamicShape> DYNAMIC_SHAPES = new SimpleRegistry<>(DYNAMIC_SHAPE_REGISTRY, Lifecycle.experimental());
    public static SimpleRegistry<IBooleanFunction> BOOLEAN_FUNCTIONS = new SimpleRegistry<>(BOOLEAN_FUNCTION_REGISTRY, Lifecycle.experimental());

    public static void initRegistries()
    {
        registerItemTiers();

        registerArmorMaterials();

        registerFoods();

        registerPropertyTypes();

        registerProperties();

        registerBooleanFunctions();

        registerDynamicShapes();
    }

    private static void registerDynamicShapes()
    {
        Registry.register(DYNAMIC_SHAPES, "empty", DynamicShape.empty());
    }

    private static void registerBooleanFunctions()
    {
        Registry.register(BOOLEAN_FUNCTIONS,"false", IBooleanFunction.FALSE);
        Registry.register(BOOLEAN_FUNCTIONS,"not_or", IBooleanFunction.NOT_OR);
        Registry.register(BOOLEAN_FUNCTIONS,"only_second", IBooleanFunction.ONLY_SECOND);
        Registry.register(BOOLEAN_FUNCTIONS,"not_first", IBooleanFunction.NOT_FIRST);
        Registry.register(BOOLEAN_FUNCTIONS,"only_first", IBooleanFunction.ONLY_FIRST);
        Registry.register(BOOLEAN_FUNCTIONS,"not_second", IBooleanFunction.NOT_SECOND);
        Registry.register(BOOLEAN_FUNCTIONS,"not_same", IBooleanFunction.NOT_SAME);
        Registry.register(BOOLEAN_FUNCTIONS,"not_and", IBooleanFunction.NOT_AND);
        Registry.register(BOOLEAN_FUNCTIONS,"and", IBooleanFunction.AND);
        Registry.register(BOOLEAN_FUNCTIONS,"same", IBooleanFunction.SAME);
        Registry.register(BOOLEAN_FUNCTIONS,"second", IBooleanFunction.SECOND);
        Registry.register(BOOLEAN_FUNCTIONS,"causes", IBooleanFunction.CAUSES);
        Registry.register(BOOLEAN_FUNCTIONS,"first", IBooleanFunction.FIRST);
        Registry.register(BOOLEAN_FUNCTIONS,"caused_by", IBooleanFunction.CAUSED_BY);
        Registry.register(BOOLEAN_FUNCTIONS,"or", IBooleanFunction.OR);
        Registry.register(BOOLEAN_FUNCTIONS,"true", IBooleanFunction.TRUE);
    }

    private static void registerProperties()
    {
        Registry.register(PROPERTIES, "attached", BlockStateProperties.ATTACHED);
        Registry.register(PROPERTIES, "bottom", BlockStateProperties.BOTTOM);
        Registry.register(PROPERTIES, "conditional", BlockStateProperties.CONDITIONAL);
        Registry.register(PROPERTIES, "disarmed", BlockStateProperties.DISARMED);
        Registry.register(PROPERTIES, "drag", BlockStateProperties.DRAG);
        Registry.register(PROPERTIES, "enabled", BlockStateProperties.ENABLED);
        Registry.register(PROPERTIES, "extended", BlockStateProperties.EXTENDED);
        Registry.register(PROPERTIES, "eye", BlockStateProperties.EYE);
        Registry.register(PROPERTIES, "falling", BlockStateProperties.FALLING);
        Registry.register(PROPERTIES, "hanging", BlockStateProperties.HANGING);
        Registry.register(PROPERTIES, "has_bottle_0", BlockStateProperties.HAS_BOTTLE_0);
        Registry.register(PROPERTIES, "has_bottle_1", BlockStateProperties.HAS_BOTTLE_1);
        Registry.register(PROPERTIES, "has_bottle_2", BlockStateProperties.HAS_BOTTLE_2);
        Registry.register(PROPERTIES, "has_record", BlockStateProperties.HAS_RECORD);
        Registry.register(PROPERTIES, "has_book", BlockStateProperties.HAS_BOOK);
        Registry.register(PROPERTIES, "inverted", BlockStateProperties.INVERTED);
        Registry.register(PROPERTIES, "in_wall", BlockStateProperties.IN_WALL);
        Registry.register(PROPERTIES, "lit", BlockStateProperties.LIT);
        Registry.register(PROPERTIES, "locked", BlockStateProperties.LOCKED);
        Registry.register(PROPERTIES, "occupied", BlockStateProperties.OCCUPIED);
        Registry.register(PROPERTIES, "open", BlockStateProperties.OPEN);
        Registry.register(PROPERTIES, "persistent", BlockStateProperties.PERSISTENT);
        Registry.register(PROPERTIES, "powered", BlockStateProperties.POWERED);
        Registry.register(PROPERTIES, "short", BlockStateProperties.SHORT);
        Registry.register(PROPERTIES, "signal_fire", BlockStateProperties.SIGNAL_FIRE);
        Registry.register(PROPERTIES, "snowy", BlockStateProperties.SNOWY);
        Registry.register(PROPERTIES, "triggered", BlockStateProperties.TRIGGERED);
        Registry.register(PROPERTIES, "unstable", BlockStateProperties.UNSTABLE);
        Registry.register(PROPERTIES, "waterlogged", BlockStateProperties.WATERLOGGED);
        Registry.register(PROPERTIES, "vine_end", BlockStateProperties.VINE_END);
        Registry.register(PROPERTIES, "horizontal_axis", BlockStateProperties.HORIZONTAL_AXIS);
        Registry.register(PROPERTIES, "axis", BlockStateProperties.AXIS);
        Registry.register(PROPERTIES, "up", BlockStateProperties.UP);
        Registry.register(PROPERTIES, "down", BlockStateProperties.DOWN);
        Registry.register(PROPERTIES, "north", BlockStateProperties.NORTH);
        Registry.register(PROPERTIES, "east", BlockStateProperties.EAST);
        Registry.register(PROPERTIES, "south", BlockStateProperties.SOUTH);
        Registry.register(PROPERTIES, "west", BlockStateProperties.WEST);
        Registry.register(PROPERTIES, "facing", BlockStateProperties.FACING);
        Registry.register(PROPERTIES, "facing_except_up", BlockStateProperties.FACING_EXCEPT_UP);
        Registry.register(PROPERTIES, "horizontal_facing", BlockStateProperties.HORIZONTAL_FACING);
        Registry.register(PROPERTIES, "orientation", BlockStateProperties.ORIENTATION);
        Registry.register(PROPERTIES, "face", BlockStateProperties.FACE);
        Registry.register(PROPERTIES, "bell_attachment", BlockStateProperties.BELL_ATTACHMENT);
        Registry.register(PROPERTIES, "wall_height_east", BlockStateProperties.WALL_HEIGHT_EAST);
        Registry.register(PROPERTIES, "wall_height_north", BlockStateProperties.WALL_HEIGHT_NORTH);
        Registry.register(PROPERTIES, "wall_height_south", BlockStateProperties.WALL_HEIGHT_SOUTH);
        Registry.register(PROPERTIES, "wall_height_west", BlockStateProperties.WALL_HEIGHT_WEST);
        Registry.register(PROPERTIES, "redstone_east", BlockStateProperties.REDSTONE_EAST);
        Registry.register(PROPERTIES, "redstone_north", BlockStateProperties.REDSTONE_NORTH);
        Registry.register(PROPERTIES, "redstone_south", BlockStateProperties.REDSTONE_SOUTH);
        Registry.register(PROPERTIES, "redstone_west", BlockStateProperties.REDSTONE_WEST);
        Registry.register(PROPERTIES, "double_block_half", BlockStateProperties.DOUBLE_BLOCK_HALF);
        Registry.register(PROPERTIES, "half", BlockStateProperties.HALF);
        Registry.register(PROPERTIES, "rail_shape", BlockStateProperties.RAIL_SHAPE);
        Registry.register(PROPERTIES, "rail_shape_straight", BlockStateProperties.RAIL_SHAPE_STRAIGHT);
        Registry.register(PROPERTIES, "age_0_1", BlockStateProperties.AGE_0_1);
        Registry.register(PROPERTIES, "age_0_2", BlockStateProperties.AGE_0_2);
        Registry.register(PROPERTIES, "age_0_3", BlockStateProperties.AGE_0_3);
        Registry.register(PROPERTIES, "age_0_5", BlockStateProperties.AGE_0_5);
        Registry.register(PROPERTIES, "age_0_7", BlockStateProperties.AGE_0_7);
        Registry.register(PROPERTIES, "age_0_15", BlockStateProperties.AGE_0_15);
        Registry.register(PROPERTIES, "age_0_25", BlockStateProperties.AGE_0_25);
        Registry.register(PROPERTIES, "bites_0_6", BlockStateProperties.BITES_0_6);
        Registry.register(PROPERTIES, "delay_1_4", BlockStateProperties.DELAY_1_4);
        Registry.register(PROPERTIES, "distance_1_7", BlockStateProperties.DISTANCE_1_7);
        Registry.register(PROPERTIES, "eggs_1_4", BlockStateProperties.EGGS_1_4);
        Registry.register(PROPERTIES, "hatch_0_2", BlockStateProperties.HATCH_0_2);
        Registry.register(PROPERTIES, "layers_1_8", BlockStateProperties.LAYERS_1_8);
        Registry.register(PROPERTIES, "level_0_3", BlockStateProperties.LEVEL_0_3);
        Registry.register(PROPERTIES, "level_0_8", BlockStateProperties.LEVEL_0_8);
        Registry.register(PROPERTIES, "level_1_8", BlockStateProperties.LEVEL_1_8);
        Registry.register(PROPERTIES, "honey_level", BlockStateProperties.HONEY_LEVEL);
        Registry.register(PROPERTIES, "level_0_15", BlockStateProperties.LEVEL_0_15);
        Registry.register(PROPERTIES, "moisture_0_7", BlockStateProperties.MOISTURE_0_7);
        Registry.register(PROPERTIES, "note_0_24", BlockStateProperties.NOTE_0_24);
        Registry.register(PROPERTIES, "pickles_1_4", BlockStateProperties.PICKLES_1_4);
        Registry.register(PROPERTIES, "power_0_15", BlockStateProperties.POWER_0_15);
        Registry.register(PROPERTIES, "stage_0_1", BlockStateProperties.STAGE_0_1);
        Registry.register(PROPERTIES, "distance_0_7", BlockStateProperties.DISTANCE_0_7);
        Registry.register(PROPERTIES, "charges", BlockStateProperties.CHARGES);
        Registry.register(PROPERTIES, "rotation_0_15", BlockStateProperties.ROTATION_0_15);
        Registry.register(PROPERTIES, "bed_part", BlockStateProperties.BED_PART);
        Registry.register(PROPERTIES, "chest_type", BlockStateProperties.CHEST_TYPE);
        Registry.register(PROPERTIES, "comparator_mode", BlockStateProperties.COMPARATOR_MODE);
        Registry.register(PROPERTIES, "door_hinge", BlockStateProperties.DOOR_HINGE);
        Registry.register(PROPERTIES, "note_block_instrument", BlockStateProperties.NOTE_BLOCK_INSTRUMENT);
        Registry.register(PROPERTIES, "piston_type", BlockStateProperties.PISTON_TYPE);
        Registry.register(PROPERTIES, "slab_type", BlockStateProperties.SLAB_TYPE);
        Registry.register(PROPERTIES, "stairs_shape", BlockStateProperties.STAIRS_SHAPE);
        Registry.register(PROPERTIES, "structure_block_mode", BlockStateProperties.STRUCTURE_BLOCK_MODE);
        Registry.register(PROPERTIES, "bamboo_leaves", BlockStateProperties.BAMBOO_LEAVES);
    }

    private static void registerPropertyTypes()
    {
        Registry.register(PROPERTY_TYPES, "boolean", new PropertyType.BoolType());
        Registry.register(PROPERTY_TYPES, "int", new PropertyType.RangeType<>(IntegerProperty.class, IntegerProperty::create, js -> js.getAsJsonPrimitive().getAsInt()));
        Registry.register(PROPERTY_TYPES, "string", new PropertyType.StringType());
        Registry.register(PROPERTY_TYPES, "direction", new PropertyType.DirectionType());
        Registry.register(PROPERTY_TYPES, "enum", new PropertyType.EnumType());
    }

    private static void registerFoods()
    {
        Registry.register(FOODS, "apple", Foods.APPLE);
        Registry.register(FOODS, "baked_potato", Foods.BAKED_POTATO);
        Registry.register(FOODS, "beef", Foods.BEEF);
        Registry.register(FOODS, "beetroot", Foods.BEETROOT);
        Registry.register(FOODS, "beetroot_soup", Foods.BEETROOT_SOUP);
        Registry.register(FOODS, "bread", Foods.BREAD);
        Registry.register(FOODS, "carrot", Foods.CARROT);
        Registry.register(FOODS, "chicken", Foods.CHICKEN);
        Registry.register(FOODS, "chorus_fruit", Foods.CHORUS_FRUIT);
        Registry.register(FOODS, "cod", Foods.COD);
        Registry.register(FOODS, "cooked_beef", Foods.COOKED_BEEF);
        Registry.register(FOODS, "cooked_chicken", Foods.COOKED_CHICKEN);
        Registry.register(FOODS, "cooked_cod", Foods.COOKED_COD);
        Registry.register(FOODS, "cooked_mutton", Foods.COOKED_MUTTON);
        Registry.register(FOODS, "cooked_porkchop", Foods.COOKED_PORKCHOP);
        Registry.register(FOODS, "cooked_rabbit", Foods.COOKED_RABBIT);
        Registry.register(FOODS, "cooked_salmon", Foods.COOKED_SALMON);
        Registry.register(FOODS, "cookie", Foods.COOKIE);
        Registry.register(FOODS, "dried_kelp", Foods.DRIED_KELP);
        Registry.register(FOODS, "enchanted_golden_apple", Foods.ENCHANTED_GOLDEN_APPLE);
        Registry.register(FOODS, "golden_apple", Foods.GOLDEN_APPLE);
        Registry.register(FOODS, "golden_carrot", Foods.GOLDEN_CARROT);
        Registry.register(FOODS, "melon_slice", Foods.MELON_SLICE);
        Registry.register(FOODS, "mushroom_stew", Foods.MUSHROOM_STEW);
        Registry.register(FOODS, "mutton", Foods.MUTTON);
        Registry.register(FOODS, "poisonous_potato", Foods.POISONOUS_POTATO);
        Registry.register(FOODS, "porkchop", Foods.PORKCHOP);
        Registry.register(FOODS, "potato", Foods.POTATO);
        Registry.register(FOODS, "pufferfish", Foods.PUFFERFISH);
        Registry.register(FOODS, "pumpkin_pie", Foods.PUMPKIN_PIE);
        Registry.register(FOODS, "rabbit", Foods.RABBIT);
        Registry.register(FOODS, "rabbit_stew", Foods.RABBIT_STEW);
        Registry.register(FOODS, "rotten_flesh", Foods.ROTTEN_FLESH);
        Registry.register(FOODS, "salmon", Foods.SALMON);
        Registry.register(FOODS, "spider_eye", Foods.SPIDER_EYE);
        Registry.register(FOODS, "suspicious_stew", Foods.SUSPICIOUS_STEW);
        Registry.register(FOODS, "sweet_berries", Foods.SWEET_BERRIES);
        Registry.register(FOODS, "tropical_fish", Foods.TROPICAL_FISH);
    }

    private static void registerArmorMaterials()
    {
        for(ArmorMaterial mat : ArmorMaterial.values())
        {
            Registry.register(ARMOR_TIERS, mat.getName(), mat);
        }
    }

    private static void registerItemTiers()
    {
        // no "name" field in item tiers
        Registry.register(ITEM_TIERS, "wood", ItemTier.WOOD);
        Registry.register(ITEM_TIERS, "stone", ItemTier.STONE);
        Registry.register(ITEM_TIERS, "gold", ItemTier.GOLD);
        Registry.register(ITEM_TIERS, "iron", ItemTier.IRON);
        Registry.register(ITEM_TIERS, "diamond", ItemTier.DIAMOND);
    }
}
