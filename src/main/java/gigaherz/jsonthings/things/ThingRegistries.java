package gigaherz.jsonthings.things;

import com.mojang.serialization.Lifecycle;
import gigaherz.jsonthings.things.properties.PropertyType;
import gigaherz.jsonthings.things.properties.PropertyTypes;
import gigaherz.jsonthings.things.serializers.BlockType;
import gigaherz.jsonthings.things.serializers.ItemType;
import gigaherz.jsonthings.things.serializers.MaterialColors;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;

public class ThingRegistries
{
    public static final ResourceKey<Registry<Registry<?>>> THING_REGISTRIES_REGISTRY = createKey("jsonthings:registries");
    public static final ResourceKey<Registry<Tier>> ITEM_TIER_REGISTRY = createKey("jsonthings:item_tier");
    public static final ResourceKey<Registry<ArmorMaterial>> ARMOR_MATERIAL_REGISTRY = createKey("jsonthings:armor_material");
    public static final ResourceKey<Registry<FoodProperties>> FOOD_REGISTRY = createKey("jsonthings:food");
    public static final ResourceKey<Registry<PropertyType>> PROPERTY_TYPE_REGISTRY = createKey("jsonthings:property_type");
    public static final ResourceKey<Registry<Property<?>>> PROPERTY_REGISTRY = createKey("jsonthings:property");
    public static final ResourceKey<Registry<BooleanOp>> BOOLEAN_FUNCTION_REGISTRY = createKey("jsonthings:boolean_function");
    public static final ResourceKey<Registry<DynamicShape>> DYNAMIC_SHAPE_REGISTRY = createKey("jsonthings:dynamic_shapes");
    public static final ResourceKey<Registry<BlockType<?>>> BLOCK_TYPE_REGISTRY = createKey("jsonthings:block_types");
    public static final ResourceKey<Registry<Material>> BLOCK_MATERIAL_REGISTRY = createKey("jsonthings:block_materials");
    public static final ResourceKey<Registry<ItemType<?>>> ITEM_TYPE_REGISTRY = createKey("jsonthings:item_types");

    public static final Registry<Registry<?>> THING_REGISTRIES = new MappedRegistry<>(THING_REGISTRIES_REGISTRY, Lifecycle.experimental());
    public static final Registry<Tier> ITEM_TIERS = makeRegistry(ITEM_TIER_REGISTRY);
    public static final Registry<ArmorMaterial> ARMOR_TIERS = makeRegistry(ARMOR_MATERIAL_REGISTRY);
    public static final Registry<FoodProperties> FOODS = makeRegistry(FOOD_REGISTRY);
    public static final Registry<PropertyType> PROPERTY_TYPES = makeRegistry(PROPERTY_TYPE_REGISTRY);
    public static final Registry<Property<?>> PROPERTIES = makeRegistry(PROPERTY_REGISTRY);
    public static final Registry<DynamicShape> DYNAMIC_SHAPES = makeRegistry(DYNAMIC_SHAPE_REGISTRY);
    public static final Registry<BooleanOp> BOOLEAN_FUNCTIONS = makeRegistry(BOOLEAN_FUNCTION_REGISTRY);
    public static final Registry<ItemType<?>> ITEM_TYPES = makeRegistry(ITEM_TYPE_REGISTRY);
    public static final Registry<BlockType<?>> BLOCK_TYPES = makeRegistry(BLOCK_TYPE_REGISTRY);
    public static final Registry<Material> BLOCK_MATERIALS = makeRegistry(BLOCK_MATERIAL_REGISTRY);

    static
    {
        registerItemTiers();

        registerArmorMaterials();

        registerFoods();

        registerProperties();

        registerBooleanFunctions();

        registerDynamicShapes();

        registerBlockMaterials();

        PropertyTypes.init();

        BlockType.init();

        ItemType.init();

        MaterialColors.init();
    }

    private static <T> ResourceKey<Registry<T>> createKey(String name)
    {
        return ResourceKey.createRegistryKey(new ResourceLocation(name));
    }

    private static <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> key)
    {
        MappedRegistry<T> registry = new MappedRegistry<T>(key, Lifecycle.experimental());
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
        Registry.register(BOOLEAN_FUNCTIONS, "false", BooleanOp.FALSE);
        Registry.register(BOOLEAN_FUNCTIONS, "not_or", BooleanOp.NOT_OR);
        Registry.register(BOOLEAN_FUNCTIONS, "only_second", BooleanOp.ONLY_SECOND);
        Registry.register(BOOLEAN_FUNCTIONS, "not_first", BooleanOp.NOT_FIRST);
        Registry.register(BOOLEAN_FUNCTIONS, "only_first", BooleanOp.ONLY_FIRST);
        Registry.register(BOOLEAN_FUNCTIONS, "not_second", BooleanOp.NOT_SECOND);
        Registry.register(BOOLEAN_FUNCTIONS, "not_same", BooleanOp.NOT_SAME);
        Registry.register(BOOLEAN_FUNCTIONS, "not_and", BooleanOp.NOT_AND);
        Registry.register(BOOLEAN_FUNCTIONS, "and", BooleanOp.AND);
        Registry.register(BOOLEAN_FUNCTIONS, "same", BooleanOp.SAME);
        Registry.register(BOOLEAN_FUNCTIONS, "second", BooleanOp.SECOND);
        Registry.register(BOOLEAN_FUNCTIONS, "causes", BooleanOp.CAUSES);
        Registry.register(BOOLEAN_FUNCTIONS, "first", BooleanOp.FIRST);
        Registry.register(BOOLEAN_FUNCTIONS, "caused_by", BooleanOp.CAUSED_BY);
        Registry.register(BOOLEAN_FUNCTIONS, "or", BooleanOp.OR);
        Registry.register(BOOLEAN_FUNCTIONS, "true", BooleanOp.TRUE);
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
        for (ArmorMaterials mat : ArmorMaterials.values())
        {
            Registry.register(ARMOR_TIERS, mat.getName(), mat);
        }
    }

    private static void registerItemTiers()
    {
        // no "name" field in item tiers
        Registry.register(ITEM_TIERS, "wood", Tiers.WOOD);
        Registry.register(ITEM_TIERS, "stone", Tiers.STONE);
        Registry.register(ITEM_TIERS, "gold", Tiers.GOLD);
        Registry.register(ITEM_TIERS, "iron", Tiers.IRON);
        Registry.register(ITEM_TIERS, "diamond", Tiers.DIAMOND);
    }

    private static void registerBlockMaterials()
    {
        Registry.register(BLOCK_MATERIALS, "air", Material.AIR);
        Registry.register(BLOCK_MATERIALS, "structural_air", Material.STRUCTURAL_AIR);
        Registry.register(BLOCK_MATERIALS, "portal", Material.PORTAL);
        Registry.register(BLOCK_MATERIALS, "cloth_decoration", Material.CLOTH_DECORATION);
        Registry.register(BLOCK_MATERIALS, "plant", Material.PLANT);
        Registry.register(BLOCK_MATERIALS, "water_plant", Material.WATER_PLANT);
        Registry.register(BLOCK_MATERIALS, "replaceable_plant", Material.REPLACEABLE_PLANT);
        Registry.register(BLOCK_MATERIALS, "replaceable_fireproof_plant", Material.REPLACEABLE_FIREPROOF_PLANT);
        Registry.register(BLOCK_MATERIALS, "replaceable_water_plant", Material.REPLACEABLE_WATER_PLANT);
        Registry.register(BLOCK_MATERIALS, "water", Material.WATER);
        Registry.register(BLOCK_MATERIALS, "bubble_column", Material.BUBBLE_COLUMN);
        Registry.register(BLOCK_MATERIALS, "lava", Material.LAVA);
        Registry.register(BLOCK_MATERIALS, "top_snow", Material.TOP_SNOW);
        Registry.register(BLOCK_MATERIALS, "fire", Material.FIRE);
        Registry.register(BLOCK_MATERIALS, "decoration", Material.DECORATION);
        Registry.register(BLOCK_MATERIALS, "web", Material.WEB);
        Registry.register(BLOCK_MATERIALS, "buildable_glass", Material.BUILDABLE_GLASS);
        Registry.register(BLOCK_MATERIALS, "clay", Material.CLAY);
        Registry.register(BLOCK_MATERIALS, "dirt", Material.DIRT);
        Registry.register(BLOCK_MATERIALS, "grass", Material.GRASS);
        Registry.register(BLOCK_MATERIALS, "ice_solid", Material.ICE_SOLID);
        Registry.register(BLOCK_MATERIALS, "sand", Material.SAND);
        Registry.register(BLOCK_MATERIALS, "sponge", Material.SPONGE);
        Registry.register(BLOCK_MATERIALS, "shulker_shell", Material.SHULKER_SHELL);
        Registry.register(BLOCK_MATERIALS, "wood", Material.WOOD);
        Registry.register(BLOCK_MATERIALS, "nether_wood", Material.NETHER_WOOD);
        Registry.register(BLOCK_MATERIALS, "bamboo_sapling", Material.BAMBOO_SAPLING);
        Registry.register(BLOCK_MATERIALS, "bamboo", Material.BAMBOO);
        Registry.register(BLOCK_MATERIALS, "wool", Material.WOOL);
        Registry.register(BLOCK_MATERIALS, "explosive", Material.EXPLOSIVE);
        Registry.register(BLOCK_MATERIALS, "leaves", Material.LEAVES);
        Registry.register(BLOCK_MATERIALS, "glass", Material.GLASS);
        Registry.register(BLOCK_MATERIALS, "ice", Material.ICE);
        Registry.register(BLOCK_MATERIALS, "cactus", Material.CACTUS);
        Registry.register(BLOCK_MATERIALS, "stone", Material.STONE);
        Registry.register(BLOCK_MATERIALS, "metal", Material.METAL);
        Registry.register(BLOCK_MATERIALS, "snow", Material.SNOW);
        Registry.register(BLOCK_MATERIALS, "heavy_metal", Material.HEAVY_METAL);
        Registry.register(BLOCK_MATERIALS, "barrier", Material.BARRIER);
        Registry.register(BLOCK_MATERIALS, "piston", Material.PISTON);
        Registry.register(BLOCK_MATERIALS, "moss", Material.MOSS);
        Registry.register(BLOCK_MATERIALS, "vegetable", Material.VEGETABLE);
        Registry.register(BLOCK_MATERIALS, "egg", Material.EGG);
        Registry.register(BLOCK_MATERIALS, "cake", Material.CAKE);
        Registry.register(BLOCK_MATERIALS, "sculk", Material.SCULK);
        Registry.register(BLOCK_MATERIALS, "amethyst", Material.AMETHYST);
        Registry.register(BLOCK_MATERIALS, "powder_snow", Material.POWDER_SNOW);
    }
}
