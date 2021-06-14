package gigaherz.jsonthings.things;

import com.mojang.serialization.Lifecycle;
import gigaherz.jsonthings.things.properties.PropertyType;
import gigaherz.jsonthings.things.properties.PropertyTypes;
import gigaherz.jsonthings.things.serializers.BlockType;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.item.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

public class ThingRegistries
{
    public static final RegistryKey<Registry<Registry<?>>> THING_REGISTRIES_REGISTRY = createKey("jsonthings:registries");
    public static final RegistryKey<Registry<IItemTier>> ITEM_TIER_REGISTRY = createKey("jsonthings:item_tier");
    public static final RegistryKey<Registry<IArmorMaterial>> ARMOR_MATERIAL_REGISTRY = createKey("jsonthings:armor_material");
    public static final RegistryKey<Registry<Food>> FOOD_REGISTRY = createKey("jsonthings:food");
    public static final RegistryKey<Registry<PropertyType>> PROPERTY_TYPE_REGISTRY = createKey("jsonthings:property_type");
    public static final RegistryKey<Registry<Property<?>>> PROPERTY_REGISTRY = createKey("jsonthings:property");
    public static final RegistryKey<Registry<IBooleanFunction>> BOOLEAN_FUNCTION_REGISTRY = createKey("jsonthings:boolean_function");
    public static final RegistryKey<Registry<DynamicShape>> DYNAMIC_SHAPE_REGISTRY = createKey("jsonthings:dynamic_shapes");
    public static final RegistryKey<Registry<BlockType>> BLOCK_TYPE_REGISTRY = createKey("jsonthings:block_types");

    public static final Registry<Registry<?>> THING_REGISTRIES = new SimpleRegistry<>(THING_REGISTRIES_REGISTRY, Lifecycle.experimental());
    public static final Registry<IItemTier> ITEM_TIERS = makeRegistry(ITEM_TIER_REGISTRY);
    public static final Registry<IArmorMaterial> ARMOR_TIERS = makeRegistry(ARMOR_MATERIAL_REGISTRY);
    public static final Registry<Food> FOODS = makeRegistry(FOOD_REGISTRY);
    public static final Registry<PropertyType> PROPERTY_TYPES = makeRegistry(PROPERTY_TYPE_REGISTRY);
    public static final Registry<Property<?>> PROPERTIES = makeRegistry(PROPERTY_REGISTRY);
    public static final Registry<DynamicShape> DYNAMIC_SHAPES = makeRegistry(DYNAMIC_SHAPE_REGISTRY);
    public static final Registry<IBooleanFunction> BOOLEAN_FUNCTIONS = makeRegistry(BOOLEAN_FUNCTION_REGISTRY);
    public static final Registry<BlockType> BLOCK_TYPES = makeRegistry(BLOCK_TYPE_REGISTRY);

    static {
        registerItemTiers();

        registerArmorMaterials();

        registerFoods();

        registerProperties();

        registerBooleanFunctions();

        registerDynamicShapes();

        PropertyTypes.init();

        BlockType.init();
    }

    private static <T> RegistryKey<Registry<T>> createKey(String name) {
        return RegistryKey.createRegistryKey(new ResourceLocation(name));
    }

    private static <T> Registry<T> makeRegistry(RegistryKey<Registry<T>> key) {
        SimpleRegistry<T> registry = new SimpleRegistry<T>(key, Lifecycle.experimental());
        return Registry.register(THING_REGISTRIES, key.location().toString(), registry);
    }

    public static void staticInit()
    {
        /* do nothing */
    }

    private static void registerDynamicShapes()
    {
        Registry.register(DYNAMIC_SHAPES, "empty", DynamicShape.empty());
    }

    private static void registerBooleanFunctions()
    {
        Registry.register(BOOLEAN_FUNCTIONS, "false", IBooleanFunction.FALSE);
        Registry.register(BOOLEAN_FUNCTIONS, "not_or", IBooleanFunction.NOT_OR);
        Registry.register(BOOLEAN_FUNCTIONS, "only_second", IBooleanFunction.ONLY_SECOND);
        Registry.register(BOOLEAN_FUNCTIONS, "not_first", IBooleanFunction.NOT_FIRST);
        Registry.register(BOOLEAN_FUNCTIONS, "only_first", IBooleanFunction.ONLY_FIRST);
        Registry.register(BOOLEAN_FUNCTIONS, "not_second", IBooleanFunction.NOT_SECOND);
        Registry.register(BOOLEAN_FUNCTIONS, "not_same", IBooleanFunction.NOT_SAME);
        Registry.register(BOOLEAN_FUNCTIONS, "not_and", IBooleanFunction.NOT_AND);
        Registry.register(BOOLEAN_FUNCTIONS, "and", IBooleanFunction.AND);
        Registry.register(BOOLEAN_FUNCTIONS, "same", IBooleanFunction.SAME);
        Registry.register(BOOLEAN_FUNCTIONS, "second", IBooleanFunction.SECOND);
        Registry.register(BOOLEAN_FUNCTIONS, "causes", IBooleanFunction.CAUSES);
        Registry.register(BOOLEAN_FUNCTIONS, "first", IBooleanFunction.FIRST);
        Registry.register(BOOLEAN_FUNCTIONS, "caused_by", IBooleanFunction.CAUSED_BY);
        Registry.register(BOOLEAN_FUNCTIONS, "or", IBooleanFunction.OR);
        Registry.register(BOOLEAN_FUNCTIONS, "true", IBooleanFunction.TRUE);
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
        Registry.register(PROPERTIES, "facing_except_up", BlockStateProperties.FACING_HOPPER);
        Registry.register(PROPERTIES, "horizontal_facing", BlockStateProperties.HORIZONTAL_FACING);
        Registry.register(PROPERTIES, "orientation", BlockStateProperties.ORIENTATION);
        Registry.register(PROPERTIES, "face", BlockStateProperties.ATTACH_FACE);
        Registry.register(PROPERTIES, "bell_attachment", BlockStateProperties.BELL_ATTACHMENT);
        Registry.register(PROPERTIES, "wall_height_east", BlockStateProperties.EAST_WALL);
        Registry.register(PROPERTIES, "wall_height_north", BlockStateProperties.NORTH_WALL);
        Registry.register(PROPERTIES, "wall_height_south", BlockStateProperties.SOUTH_WALL);
        Registry.register(PROPERTIES, "wall_height_west", BlockStateProperties.WEST_WALL);
        Registry.register(PROPERTIES, "redstone_east", BlockStateProperties.EAST_REDSTONE);
        Registry.register(PROPERTIES, "redstone_north", BlockStateProperties.NORTH_REDSTONE);
        Registry.register(PROPERTIES, "redstone_south", BlockStateProperties.SOUTH_REDSTONE);
        Registry.register(PROPERTIES, "redstone_west", BlockStateProperties.WEST_REDSTONE);
        Registry.register(PROPERTIES, "double_block_half", BlockStateProperties.DOUBLE_BLOCK_HALF);
        Registry.register(PROPERTIES, "half", BlockStateProperties.HALF);
        Registry.register(PROPERTIES, "rail_shape", BlockStateProperties.RAIL_SHAPE);
        Registry.register(PROPERTIES, "rail_shape_straight", BlockStateProperties.RAIL_SHAPE_STRAIGHT);
        Registry.register(PROPERTIES, "age_0_1", BlockStateProperties.AGE_1);
        Registry.register(PROPERTIES, "age_0_2", BlockStateProperties.AGE_2);
        Registry.register(PROPERTIES, "age_0_3", BlockStateProperties.AGE_3);
        Registry.register(PROPERTIES, "age_0_5", BlockStateProperties.AGE_5);
        Registry.register(PROPERTIES, "age_0_7", BlockStateProperties.AGE_7);
        Registry.register(PROPERTIES, "age_0_15", BlockStateProperties.AGE_15);
        Registry.register(PROPERTIES, "age_0_25", BlockStateProperties.AGE_25);
        Registry.register(PROPERTIES, "bites_0_6", BlockStateProperties.BITES);
        Registry.register(PROPERTIES, "delay_1_4", BlockStateProperties.DELAY);
        Registry.register(PROPERTIES, "distance_1_7", BlockStateProperties.DISTANCE);
        Registry.register(PROPERTIES, "eggs_1_4", BlockStateProperties.EGGS);
        Registry.register(PROPERTIES, "hatch_0_2", BlockStateProperties.HATCH);
        Registry.register(PROPERTIES, "layers_1_8", BlockStateProperties.LAYERS);
        Registry.register(PROPERTIES, "level_0_3", BlockStateProperties.LEVEL_CAULDRON);
        Registry.register(PROPERTIES, "level_0_8", BlockStateProperties.LEVEL_COMPOSTER);
        Registry.register(PROPERTIES, "level_1_8", BlockStateProperties.LEVEL_FLOWING);
        Registry.register(PROPERTIES, "honey_level", BlockStateProperties.LEVEL_HONEY);
        Registry.register(PROPERTIES, "level_0_15", BlockStateProperties.LEVEL);
        Registry.register(PROPERTIES, "moisture_0_7", BlockStateProperties.MOISTURE);
        Registry.register(PROPERTIES, "note_0_24", BlockStateProperties.NOTE);
        Registry.register(PROPERTIES, "pickles_1_4", BlockStateProperties.PICKLES);
        Registry.register(PROPERTIES, "power_0_15", BlockStateProperties.POWER);
        Registry.register(PROPERTIES, "stage_0_1", BlockStateProperties.STAGE);
        Registry.register(PROPERTIES, "distance_0_7", BlockStateProperties.STABILITY_DISTANCE);
        Registry.register(PROPERTIES, "charges", BlockStateProperties.RESPAWN_ANCHOR_CHARGES);
        Registry.register(PROPERTIES, "rotation_0_15", BlockStateProperties.ROTATION_16);
        Registry.register(PROPERTIES, "bed_part", BlockStateProperties.BED_PART);
        Registry.register(PROPERTIES, "chest_type", BlockStateProperties.CHEST_TYPE);
        Registry.register(PROPERTIES, "comparator_mode", BlockStateProperties.MODE_COMPARATOR);
        Registry.register(PROPERTIES, "door_hinge", BlockStateProperties.DOOR_HINGE);
        Registry.register(PROPERTIES, "note_block_instrument", BlockStateProperties.NOTEBLOCK_INSTRUMENT);
        Registry.register(PROPERTIES, "piston_type", BlockStateProperties.PISTON_TYPE);
        Registry.register(PROPERTIES, "slab_type", BlockStateProperties.SLAB_TYPE);
        Registry.register(PROPERTIES, "stairs_shape", BlockStateProperties.STAIRS_SHAPE);
        Registry.register(PROPERTIES, "structure_block_mode", BlockStateProperties.STRUCTUREBLOCK_MODE);
        Registry.register(PROPERTIES, "bamboo_leaves", BlockStateProperties.BAMBOO_LEAVES);
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
        for (ArmorMaterial mat : ArmorMaterial.values())
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
