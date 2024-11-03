package dev.gigaherz.jsonthings.things;

import com.mojang.serialization.Lifecycle;
import dev.gigaherz.jsonthings.things.properties.PropertyType;
import dev.gigaherz.jsonthings.things.properties.PropertyTypes;
import dev.gigaherz.jsonthings.things.serializers.FlexBlockType;
import dev.gigaherz.jsonthings.things.serializers.FlexFluidType;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.things.serializers.MapColors;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import java.lang.reflect.AccessFlag;

public class ThingRegistries
{
    public static void staticInit()
    {
        /* do nothing */
    }

    public static final ResourceKey<Registry<Registry<?>>> THING_REGISTRIES_REGISTRY = createKey("jsonthings:registries");
    public static final ResourceKey<Registry<ToolMaterial>> TOOL_MATERIAL_REGISTRY = createKey("jsonthings:tool_material");
    public static final ResourceKey<Registry<ArmorMaterial>> ARMOR_MATERIAL_REGISTRY = createKey("jsonthings:armor_material");
    public static final ResourceKey<Registry<FoodProperties>> FOOD_REGISTRY = createKey("jsonthings:food");
    public static final ResourceKey<Registry<PropertyType>> PROPERTY_TYPE_REGISTRY = createKey("jsonthings:property_type");
    public static final ResourceKey<Registry<Property<?>>> PROPERTY_REGISTRY = createKey("jsonthings:property");
    public static final ResourceKey<Registry<DynamicShape>> DYNAMIC_SHAPE_REGISTRY = createKey("jsonthings:dynamic_shapes");
    public static final ResourceKey<Registry<FlexBlockType<?>>> BLOCK_TYPE_REGISTRY = createKey("jsonthings:block_types");
    public static final ResourceKey<Registry<FlexItemType<?>>> ITEM_TYPE_REGISTRY = createKey("jsonthings:item_types");
    public static final ResourceKey<Registry<SoundType>> SOUND_TYPE_REGISTRY = createKey("jsonthings:sound_types");
    public static final ResourceKey<Registry<FlexFluidType<?>>> FLUID_TYPE_REGISTRY = createKey("jsonthings:fluid_types");

    public static final Registry<Registry<?>> THING_REGISTRIES = new MappedRegistry<>(THING_REGISTRIES_REGISTRY, Lifecycle.experimental(), false);
    public static final Registry<ToolMaterial> TOOL_MATERIAL = makeRegistry(TOOL_MATERIAL_REGISTRY);
    public static final Registry<ArmorMaterial> ARMOR_MATERIAL = makeRegistry(ARMOR_MATERIAL_REGISTRY);
    public static final Registry<FoodProperties> FOOD = makeRegistry(FOOD_REGISTRY);
    public static final Registry<PropertyType> PROPERTY_TYPE = makeRegistry(PROPERTY_TYPE_REGISTRY);
    public static final Registry<Property<?>> PROPERTY = makeRegistry(PROPERTY_REGISTRY);
    public static final Registry<DynamicShape> DYNAMIC_SHAPE = makeRegistry(DYNAMIC_SHAPE_REGISTRY);
    public static final Registry<FlexItemType<?>> ITEM_TYPE = makeRegistry(ITEM_TYPE_REGISTRY);
    public static final Registry<FlexBlockType<?>> BLOCK_TYPE = makeRegistry(BLOCK_TYPE_REGISTRY);
    public static final Registry<SoundType> SOUND_TYPE = makeRegistry(SOUND_TYPE_REGISTRY);
    public static final Registry<FlexFluidType<?>> FLUID_TYPE = makeRegistry(FLUID_TYPE_REGISTRY);

    static
    {
        registerToolMaterials();

        registerArmorMaterials();

        registerFoods();

        registerProperties();

        registerDynamicShapes();

        registerSoundTypes();

        PropertyTypes.init();

        FlexBlockType.init();

        FlexItemType.init();

        MapColors.init();

        FlexFluidType.init();
    }

    private static <T> ResourceKey<Registry<T>> createKey(String name)
    {
        return ResourceKey.createRegistryKey(ResourceLocation.parse(name));
    }

    private static <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> key)
    {
        MappedRegistry<T> registry = new MappedRegistry<>(key, Lifecycle.experimental(), false);
        return Registry.register(THING_REGISTRIES, key.location().toString(), registry);
    }

    private static void registerToolMaterials()
    {
        for(var field : ToolMaterial.class.getDeclaredFields())
        {
            if (!field.accessFlags().contains(AccessFlag.STATIC)
                    || !field.accessFlags().contains(AccessFlag.PUBLIC)
                    || field.getType() != ToolMaterial.class)
                continue;

            try
            {
                Registry.register(TOOL_MATERIAL, field.getName().toLowerCase(), (ToolMaterial)field.get(null));
            }
            catch (IllegalAccessException e)
            {
                // Ignore
            }
        }
    }

    private static void registerArmorMaterials()
    {
        for(var field : ArmorMaterials.class.getDeclaredFields())
        {
            if (!field.accessFlags().contains(AccessFlag.STATIC)
                    || !field.accessFlags().contains(AccessFlag.PUBLIC)
                    || field.getType() != ArmorMaterial.class)
                continue;

            try
            {
                Registry.register(ARMOR_MATERIAL, field.getName().toLowerCase(), (ArmorMaterial)field.get(null));
            }
            catch (IllegalAccessException e)
            {
                // Ignore
            }
        }
    }

    private static void registerDynamicShapes()
    {
        Registry.register(DYNAMIC_SHAPE, "empty", DynamicShape.empty());
    }

    private static void registerProperties()
    {
        Registry.register(PROPERTY, "attached", BlockStateProperties.ATTACHED);
        Registry.register(PROPERTY, "bottom", BlockStateProperties.BOTTOM);
        Registry.register(PROPERTY, "conditional", BlockStateProperties.CONDITIONAL);
        Registry.register(PROPERTY, "disarmed", BlockStateProperties.DISARMED);
        Registry.register(PROPERTY, "drag", BlockStateProperties.DRAG);
        Registry.register(PROPERTY, "enabled", BlockStateProperties.ENABLED);
        Registry.register(PROPERTY, "extended", BlockStateProperties.EXTENDED);
        Registry.register(PROPERTY, "eye", BlockStateProperties.EYE);
        Registry.register(PROPERTY, "falling", BlockStateProperties.FALLING);
        Registry.register(PROPERTY, "hanging", BlockStateProperties.HANGING);
        Registry.register(PROPERTY, "has_bottle_0", BlockStateProperties.HAS_BOTTLE_0);
        Registry.register(PROPERTY, "has_bottle_1", BlockStateProperties.HAS_BOTTLE_1);
        Registry.register(PROPERTY, "has_bottle_2", BlockStateProperties.HAS_BOTTLE_2);
        Registry.register(PROPERTY, "has_record", BlockStateProperties.HAS_RECORD);
        Registry.register(PROPERTY, "has_book", BlockStateProperties.HAS_BOOK);
        Registry.register(PROPERTY, "inverted", BlockStateProperties.INVERTED);
        Registry.register(PROPERTY, "in_wall", BlockStateProperties.IN_WALL);
        Registry.register(PROPERTY, "lit", BlockStateProperties.LIT);
        Registry.register(PROPERTY, "locked", BlockStateProperties.LOCKED);
        Registry.register(PROPERTY, "occupied", BlockStateProperties.OCCUPIED);
        Registry.register(PROPERTY, "open", BlockStateProperties.OPEN);
        Registry.register(PROPERTY, "persistent", BlockStateProperties.PERSISTENT);
        Registry.register(PROPERTY, "powered", BlockStateProperties.POWERED);
        Registry.register(PROPERTY, "short", BlockStateProperties.SHORT);
        Registry.register(PROPERTY, "signal_fire", BlockStateProperties.SIGNAL_FIRE);
        Registry.register(PROPERTY, "snowy", BlockStateProperties.SNOWY);
        Registry.register(PROPERTY, "triggered", BlockStateProperties.TRIGGERED);
        Registry.register(PROPERTY, "unstable", BlockStateProperties.UNSTABLE);
        Registry.register(PROPERTY, "waterlogged", BlockStateProperties.WATERLOGGED);
        Registry.register(PROPERTY, "berries", BlockStateProperties.BERRIES);
        Registry.register(PROPERTY, "bloom", BlockStateProperties.BLOOM);
        Registry.register(PROPERTY, "shrieking", BlockStateProperties.SHRIEKING);
        Registry.register(PROPERTY, "can_summon", BlockStateProperties.CAN_SUMMON);
        Registry.register(PROPERTY, "horizontal_axis", BlockStateProperties.HORIZONTAL_AXIS);
        Registry.register(PROPERTY, "axis", BlockStateProperties.AXIS);
        Registry.register(PROPERTY, "up", BlockStateProperties.UP);
        Registry.register(PROPERTY, "down", BlockStateProperties.DOWN);
        Registry.register(PROPERTY, "north", BlockStateProperties.NORTH);
        Registry.register(PROPERTY, "east", BlockStateProperties.EAST);
        Registry.register(PROPERTY, "south", BlockStateProperties.SOUTH);
        Registry.register(PROPERTY, "west", BlockStateProperties.WEST);
        Registry.register(PROPERTY, "facing", BlockStateProperties.FACING);
        Registry.register(PROPERTY, "facing_except_up", BlockStateProperties.FACING_HOPPER);
        Registry.register(PROPERTY, "horizontal_facing", BlockStateProperties.HORIZONTAL_FACING);
        Registry.register(PROPERTY, "flower_amount", BlockStateProperties.FLOWER_AMOUNT);
        Registry.register(PROPERTY, "orientation", BlockStateProperties.ORIENTATION);
        Registry.register(PROPERTY, "face", BlockStateProperties.ATTACH_FACE);
        Registry.register(PROPERTY, "bell_attachment", BlockStateProperties.BELL_ATTACHMENT);
        Registry.register(PROPERTY, "wall_height_east", BlockStateProperties.EAST_WALL);
        Registry.register(PROPERTY, "wall_height_north", BlockStateProperties.NORTH_WALL);
        Registry.register(PROPERTY, "wall_height_south", BlockStateProperties.SOUTH_WALL);
        Registry.register(PROPERTY, "wall_height_west", BlockStateProperties.WEST_WALL);
        Registry.register(PROPERTY, "redstone_east", BlockStateProperties.EAST_REDSTONE);
        Registry.register(PROPERTY, "redstone_north", BlockStateProperties.NORTH_REDSTONE);
        Registry.register(PROPERTY, "redstone_south", BlockStateProperties.SOUTH_REDSTONE);
        Registry.register(PROPERTY, "redstone_west", BlockStateProperties.WEST_REDSTONE);
        Registry.register(PROPERTY, "double_block_half", BlockStateProperties.DOUBLE_BLOCK_HALF);
        Registry.register(PROPERTY, "half", BlockStateProperties.HALF);
        Registry.register(PROPERTY, "rail_shape", BlockStateProperties.RAIL_SHAPE);
        Registry.register(PROPERTY, "rail_shape_straight", BlockStateProperties.RAIL_SHAPE_STRAIGHT);
        Registry.register(PROPERTY, "age_0_1", BlockStateProperties.AGE_1);
        Registry.register(PROPERTY, "age_0_2", BlockStateProperties.AGE_2);
        Registry.register(PROPERTY, "age_0_3", BlockStateProperties.AGE_3);
        Registry.register(PROPERTY, "age_0_4", BlockStateProperties.AGE_4);
        Registry.register(PROPERTY, "age_0_5", BlockStateProperties.AGE_5);
        Registry.register(PROPERTY, "age_0_7", BlockStateProperties.AGE_7);
        Registry.register(PROPERTY, "age_0_15", BlockStateProperties.AGE_15);
        Registry.register(PROPERTY, "age_0_25", BlockStateProperties.AGE_25);
        Registry.register(PROPERTY, "bites_0_6", BlockStateProperties.BITES);
        Registry.register(PROPERTY, "candles", BlockStateProperties.CANDLES);
        Registry.register(PROPERTY, "delay_1_4", BlockStateProperties.DELAY);
        Registry.register(PROPERTY, "distance_1_7", BlockStateProperties.DISTANCE);
        Registry.register(PROPERTY, "eggs_1_4", BlockStateProperties.EGGS);
        Registry.register(PROPERTY, "hatch_0_2", BlockStateProperties.HATCH);
        Registry.register(PROPERTY, "layers_1_8", BlockStateProperties.LAYERS);
        Registry.register(PROPERTY, "level_0_3", BlockStateProperties.LEVEL_CAULDRON);
        Registry.register(PROPERTY, "level_0_8", BlockStateProperties.LEVEL_COMPOSTER);
        Registry.register(PROPERTY, "level_1_8", BlockStateProperties.LEVEL_FLOWING);
        Registry.register(PROPERTY, "honey_level", BlockStateProperties.LEVEL_HONEY);
        Registry.register(PROPERTY, "level_0_15", BlockStateProperties.LEVEL);
        Registry.register(PROPERTY, "moisture_0_7", BlockStateProperties.MOISTURE);
        Registry.register(PROPERTY, "note_0_24", BlockStateProperties.NOTE);
        Registry.register(PROPERTY, "pickles_1_4", BlockStateProperties.PICKLES);
        Registry.register(PROPERTY, "power_0_15", BlockStateProperties.POWER);
        Registry.register(PROPERTY, "stage_0_1", BlockStateProperties.STAGE);
        Registry.register(PROPERTY, "distance_0_7", BlockStateProperties.STABILITY_DISTANCE);
        Registry.register(PROPERTY, "charges", BlockStateProperties.RESPAWN_ANCHOR_CHARGES);
        Registry.register(PROPERTY, "rotation_0_15", BlockStateProperties.ROTATION_16);
        Registry.register(PROPERTY, "bed_part", BlockStateProperties.BED_PART);
        Registry.register(PROPERTY, "chest_type", BlockStateProperties.CHEST_TYPE);
        Registry.register(PROPERTY, "comparator_mode", BlockStateProperties.MODE_COMPARATOR);
        Registry.register(PROPERTY, "door_hinge", BlockStateProperties.DOOR_HINGE);
        Registry.register(PROPERTY, "note_block_instrument", BlockStateProperties.NOTEBLOCK_INSTRUMENT);
        Registry.register(PROPERTY, "piston_type", BlockStateProperties.PISTON_TYPE);
        Registry.register(PROPERTY, "slab_type", BlockStateProperties.SLAB_TYPE);
        Registry.register(PROPERTY, "stairs_shape", BlockStateProperties.STAIRS_SHAPE);
        Registry.register(PROPERTY, "structure_block_mode", BlockStateProperties.STRUCTUREBLOCK_MODE);
        Registry.register(PROPERTY, "bamboo_leaves", BlockStateProperties.BAMBOO_LEAVES);
        Registry.register(PROPERTY, "tilt", BlockStateProperties.TILT);
        Registry.register(PROPERTY, "vertical_direction", BlockStateProperties.VERTICAL_DIRECTION);
        Registry.register(PROPERTY, "dripstone_thickness", BlockStateProperties.DRIPSTONE_THICKNESS);
        Registry.register(PROPERTY, "sculk_sensor_phase", BlockStateProperties.SCULK_SENSOR_PHASE);
        Registry.register(PROPERTY, "chiseled_bookshelf_slot_0_occupied", BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED);
        Registry.register(PROPERTY, "chiseled_bookshelf_slot_1_occupied", BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED);
        Registry.register(PROPERTY, "chiseled_bookshelf_slot_2_occupied", BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED);
        Registry.register(PROPERTY, "chiseled_bookshelf_slot_3_occupied", BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED);
        Registry.register(PROPERTY, "chiseled_bookshelf_slot_4_occupied", BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED);
        Registry.register(PROPERTY, "chiseled_bookshelf_slot_5_occupied", BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED);
        Registry.register(PROPERTY, "dusted", BlockStateProperties.DUSTED);
    }

    private static void registerFoods()
    {
        Registry.register(FOOD, "apple", Foods.APPLE);
        Registry.register(FOOD, "baked_potato", Foods.BAKED_POTATO);
        Registry.register(FOOD, "beef", Foods.BEEF);
        Registry.register(FOOD, "beetroot", Foods.BEETROOT);
        Registry.register(FOOD, "beetroot_soup", Foods.BEETROOT_SOUP);
        Registry.register(FOOD, "bread", Foods.BREAD);
        Registry.register(FOOD, "carrot", Foods.CARROT);
        Registry.register(FOOD, "chicken", Foods.CHICKEN);
        Registry.register(FOOD, "chorus_fruit", Foods.CHORUS_FRUIT);
        Registry.register(FOOD, "cod", Foods.COD);
        Registry.register(FOOD, "cooked_beef", Foods.COOKED_BEEF);
        Registry.register(FOOD, "cooked_chicken", Foods.COOKED_CHICKEN);
        Registry.register(FOOD, "cooked_cod", Foods.COOKED_COD);
        Registry.register(FOOD, "cooked_mutton", Foods.COOKED_MUTTON);
        Registry.register(FOOD, "cooked_porkchop", Foods.COOKED_PORKCHOP);
        Registry.register(FOOD, "cooked_rabbit", Foods.COOKED_RABBIT);
        Registry.register(FOOD, "cooked_salmon", Foods.COOKED_SALMON);
        Registry.register(FOOD, "cookie", Foods.COOKIE);
        Registry.register(FOOD, "dried_kelp", Foods.DRIED_KELP);
        Registry.register(FOOD, "enchanted_golden_apple", Foods.ENCHANTED_GOLDEN_APPLE);
        Registry.register(FOOD, "golden_apple", Foods.GOLDEN_APPLE);
        Registry.register(FOOD, "golden_carrot", Foods.GOLDEN_CARROT);
        Registry.register(FOOD, "honey_bottle", Foods.HONEY_BOTTLE);
        Registry.register(FOOD, "melon_slice", Foods.MELON_SLICE);
        Registry.register(FOOD, "mushroom_stew", Foods.MUSHROOM_STEW);
        Registry.register(FOOD, "mutton", Foods.MUTTON);
        Registry.register(FOOD, "poisonous_potato", Foods.POISONOUS_POTATO);
        Registry.register(FOOD, "porkchop", Foods.PORKCHOP);
        Registry.register(FOOD, "potato", Foods.POTATO);
        Registry.register(FOOD, "pufferfish", Foods.PUFFERFISH);
        Registry.register(FOOD, "pumpkin_pie", Foods.PUMPKIN_PIE);
        Registry.register(FOOD, "rabbit", Foods.RABBIT);
        Registry.register(FOOD, "rabbit_stew", Foods.RABBIT_STEW);
        Registry.register(FOOD, "rotten_flesh", Foods.ROTTEN_FLESH);
        Registry.register(FOOD, "salmon", Foods.SALMON);
        Registry.register(FOOD, "spider_eye", Foods.SPIDER_EYE);
        Registry.register(FOOD, "suspicious_stew", Foods.SUSPICIOUS_STEW);
        Registry.register(FOOD, "sweet_berries", Foods.SWEET_BERRIES);
        Registry.register(FOOD, "glow_berries", Foods.GLOW_BERRIES);
        Registry.register(FOOD, "tropical_fish", Foods.TROPICAL_FISH);
    }

    private static void registerSoundTypes()
    {
        Registry.register(SOUND_TYPE, "wood", SoundType.WOOD);
        Registry.register(SOUND_TYPE, "gravel", SoundType.GRAVEL);
        Registry.register(SOUND_TYPE, "grass", SoundType.GRASS);
        Registry.register(SOUND_TYPE, "lily_pad", SoundType.LILY_PAD);
        Registry.register(SOUND_TYPE, "stone", SoundType.STONE);
        Registry.register(SOUND_TYPE, "metal", SoundType.METAL);
        Registry.register(SOUND_TYPE, "glass", SoundType.GLASS);
        Registry.register(SOUND_TYPE, "wool", SoundType.WOOL);
        Registry.register(SOUND_TYPE, "sand", SoundType.SAND);
        Registry.register(SOUND_TYPE, "snow", SoundType.SNOW);
        Registry.register(SOUND_TYPE, "powder_snow", SoundType.POWDER_SNOW);
        Registry.register(SOUND_TYPE, "ladder", SoundType.LADDER);
        Registry.register(SOUND_TYPE, "anvil", SoundType.ANVIL);
        Registry.register(SOUND_TYPE, "slime_block", SoundType.SLIME_BLOCK);
        Registry.register(SOUND_TYPE, "honey_block", SoundType.HONEY_BLOCK);
        Registry.register(SOUND_TYPE, "wet_grass", SoundType.WET_GRASS);
        Registry.register(SOUND_TYPE, "coral_block", SoundType.CORAL_BLOCK);
        Registry.register(SOUND_TYPE, "bamboo", SoundType.BAMBOO);
        Registry.register(SOUND_TYPE, "bamboo_sapling", SoundType.BAMBOO_SAPLING);
        Registry.register(SOUND_TYPE, "scaffolding", SoundType.SCAFFOLDING);
        Registry.register(SOUND_TYPE, "sweet_berry_bush", SoundType.SWEET_BERRY_BUSH);
        Registry.register(SOUND_TYPE, "crop", SoundType.CROP);
        Registry.register(SOUND_TYPE, "hard_crop", SoundType.HARD_CROP);
        Registry.register(SOUND_TYPE, "vine", SoundType.VINE);
        Registry.register(SOUND_TYPE, "nether_wart", SoundType.NETHER_WART);
        Registry.register(SOUND_TYPE, "lantern", SoundType.LANTERN);
        Registry.register(SOUND_TYPE, "stem", SoundType.STEM);
        Registry.register(SOUND_TYPE, "nylium", SoundType.NYLIUM);
        Registry.register(SOUND_TYPE, "fungus", SoundType.FUNGUS);
        Registry.register(SOUND_TYPE, "roots", SoundType.ROOTS);
        Registry.register(SOUND_TYPE, "shroomlight", SoundType.SHROOMLIGHT);
        Registry.register(SOUND_TYPE, "weeping_vines", SoundType.WEEPING_VINES);
        Registry.register(SOUND_TYPE, "twisting_vines", SoundType.TWISTING_VINES);
        Registry.register(SOUND_TYPE, "soul_sand", SoundType.SOUL_SAND);
        Registry.register(SOUND_TYPE, "soul_soil", SoundType.SOUL_SOIL);
        Registry.register(SOUND_TYPE, "basalt", SoundType.BASALT);
        Registry.register(SOUND_TYPE, "wart_block", SoundType.WART_BLOCK);
        Registry.register(SOUND_TYPE, "netherrack", SoundType.NETHERRACK);
        Registry.register(SOUND_TYPE, "nether_bricks", SoundType.NETHER_BRICKS);
        Registry.register(SOUND_TYPE, "nether_sprouts", SoundType.NETHER_SPROUTS);
        Registry.register(SOUND_TYPE, "nether_ore", SoundType.NETHER_ORE);
        Registry.register(SOUND_TYPE, "bone_block", SoundType.BONE_BLOCK);
        Registry.register(SOUND_TYPE, "netherite_block", SoundType.NETHERITE_BLOCK);
        Registry.register(SOUND_TYPE, "ancient_debris", SoundType.ANCIENT_DEBRIS);
        Registry.register(SOUND_TYPE, "lodestone", SoundType.LODESTONE);
        Registry.register(SOUND_TYPE, "chain", SoundType.CHAIN);
        Registry.register(SOUND_TYPE, "nether_gold_ore", SoundType.NETHER_GOLD_ORE);
        Registry.register(SOUND_TYPE, "gilded_blackstone", SoundType.GILDED_BLACKSTONE);
        Registry.register(SOUND_TYPE, "candle", SoundType.CANDLE);
        Registry.register(SOUND_TYPE, "amethyst", SoundType.AMETHYST);
        Registry.register(SOUND_TYPE, "amethyst_cluster", SoundType.AMETHYST_CLUSTER);
        Registry.register(SOUND_TYPE, "small_amethyst_bud", SoundType.SMALL_AMETHYST_BUD);
        Registry.register(SOUND_TYPE, "medium_amethyst_bud", SoundType.MEDIUM_AMETHYST_BUD);
        Registry.register(SOUND_TYPE, "large_amethyst_bud", SoundType.LARGE_AMETHYST_BUD);
        Registry.register(SOUND_TYPE, "tuff", SoundType.TUFF);
        Registry.register(SOUND_TYPE, "calcite", SoundType.CALCITE);
        Registry.register(SOUND_TYPE, "dripstone_block", SoundType.DRIPSTONE_BLOCK);
        Registry.register(SOUND_TYPE, "pointed_dripstone", SoundType.POINTED_DRIPSTONE);
        Registry.register(SOUND_TYPE, "copper", SoundType.COPPER);
        Registry.register(SOUND_TYPE, "cave_vines", SoundType.CAVE_VINES);
        Registry.register(SOUND_TYPE, "spore_blossom", SoundType.SPORE_BLOSSOM);
        Registry.register(SOUND_TYPE, "azalea", SoundType.AZALEA);
        Registry.register(SOUND_TYPE, "flowering_azalea", SoundType.FLOWERING_AZALEA);
        Registry.register(SOUND_TYPE, "moss_carpet", SoundType.MOSS_CARPET);
        Registry.register(SOUND_TYPE, "pink_petals", SoundType.PINK_PETALS);
        Registry.register(SOUND_TYPE, "moss", SoundType.MOSS);
        Registry.register(SOUND_TYPE, "big_dripleaf", SoundType.BIG_DRIPLEAF);
        Registry.register(SOUND_TYPE, "small_dripleaf", SoundType.SMALL_DRIPLEAF);
        Registry.register(SOUND_TYPE, "rooted_dirt", SoundType.ROOTED_DIRT);
        Registry.register(SOUND_TYPE, "hanging_roots", SoundType.HANGING_ROOTS);
        Registry.register(SOUND_TYPE, "azalea_leaves", SoundType.AZALEA_LEAVES);
        Registry.register(SOUND_TYPE, "sculk_sensor", SoundType.SCULK_SENSOR);
        Registry.register(SOUND_TYPE, "sculk_catalyst", SoundType.SCULK_CATALYST);
        Registry.register(SOUND_TYPE, "sculk", SoundType.SCULK);
        Registry.register(SOUND_TYPE, "sculk_vein", SoundType.SCULK_VEIN);
        Registry.register(SOUND_TYPE, "sculk_shrieker", SoundType.SCULK_SHRIEKER);
        Registry.register(SOUND_TYPE, "glow_lichen", SoundType.GLOW_LICHEN);
        Registry.register(SOUND_TYPE, "deepslate", SoundType.DEEPSLATE);
        Registry.register(SOUND_TYPE, "deepslate_bricks", SoundType.DEEPSLATE_BRICKS);
        Registry.register(SOUND_TYPE, "deepslate_tiles", SoundType.DEEPSLATE_TILES);
        Registry.register(SOUND_TYPE, "polished_deepslate", SoundType.POLISHED_DEEPSLATE);
        Registry.register(SOUND_TYPE, "froglight", SoundType.FROGLIGHT);
        Registry.register(SOUND_TYPE, "frogspawn", SoundType.FROGSPAWN);
        Registry.register(SOUND_TYPE, "mangrove_roots", SoundType.MANGROVE_ROOTS);
        Registry.register(SOUND_TYPE, "muddy_mangrove_roots", SoundType.MUDDY_MANGROVE_ROOTS);
        Registry.register(SOUND_TYPE, "mud", SoundType.MUD);
        Registry.register(SOUND_TYPE, "mud_bricks", SoundType.MUD_BRICKS);
        Registry.register(SOUND_TYPE, "packed_mud", SoundType.PACKED_MUD);
        Registry.register(SOUND_TYPE, "hanging_sign", SoundType.HANGING_SIGN);
        Registry.register(SOUND_TYPE, "nether_wood_hanging_sign", SoundType.NETHER_WOOD_HANGING_SIGN);
        Registry.register(SOUND_TYPE, "bamboo_wood_hanging_sign", SoundType.BAMBOO_WOOD_HANGING_SIGN);
        Registry.register(SOUND_TYPE, "bamboo_wood", SoundType.BAMBOO_WOOD);
        Registry.register(SOUND_TYPE, "nether_wood", SoundType.NETHER_WOOD);
        Registry.register(SOUND_TYPE, "cherry_wood", SoundType.CHERRY_WOOD);
        Registry.register(SOUND_TYPE, "cherry_sapling", SoundType.CHERRY_SAPLING);
        Registry.register(SOUND_TYPE, "cherry_leaves", SoundType.CHERRY_LEAVES);
        Registry.register(SOUND_TYPE, "cherry_wood_hanging_sign", SoundType.CHERRY_WOOD_HANGING_SIGN);
        Registry.register(SOUND_TYPE, "chiseled_bookshelf", SoundType.CHISELED_BOOKSHELF);
        Registry.register(SOUND_TYPE, "suspicious_sand", SoundType.SUSPICIOUS_SAND);
        Registry.register(SOUND_TYPE, "decorated_pot", SoundType.DECORATED_POT);
    }
}
