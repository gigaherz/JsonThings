package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.blocks.*;
import dev.gigaherz.jsonthings.things.parsers.ThingParseException;
import dev.gigaherz.jsonthings.util.Utils;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.FastColor;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FlowingFluid;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;

public class FlexBlockType<T extends Block & IFlexBlock>
{

    public static final FlexBlockType<FlexBlock> PLAIN = register("plain", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder());

    public static final FlexBlockType<FlexFallingBlock> FALLING = register("falling", data -> {

        MutableObject<ColorRGBA> dustColor = new MutableObject<>(new ColorRGBA(-1));

        JParse.begin(data)
                .ifKey("dust_color", val -> val
                        .ifInteger(num -> num.handle(color -> dustColor.setValue(new ColorRGBA(color))))
                        .ifObj(num -> num.map((JsonObject obj) -> {
                            var r = GsonHelper.getAsInt(obj, "r");
                            var g = GsonHelper.getAsInt(obj, "g");
                            var b = GsonHelper.getAsInt(obj, "b");
                            var a = GsonHelper.getAsInt(obj, "a", 255);
                            return new ColorRGBA(FastColor.ARGB32.color(a, r, g, b));
                        }).handle(dustColor::setValue))
                        .typeError()
                );

        return (props, builder) -> {
            List<Property<?>> _properties = builder.getProperties();
            Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();

            return new FlexFallingBlock(dustColor.getValue(), props, propertyDefaultValues)
            {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
                {
                    super.createBlockStateDefinition(builder1);
                    _properties.forEach(builder1::add);
                }
            };
        };
    }, DefaultTypeProperties.builder().defaultLayer("cutout"));

    public static final FlexBlockType<FlexSaplingBlock> SAPLING = register("sapling", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        var featureId = ResourceLocation.parse(GsonHelper.getAsString(data, "tree_feature"));
        var featureKey = ResourceKey.create(Registries.CONFIGURED_FEATURE, featureId);
        // TODO: "mega" tree, and flower of the TreeGrower?
        var treeGrower = new TreeGrower(builder.getRegistryName().toString(), Optional.empty(), Optional.of(featureKey), Optional.empty());
        return new FlexSaplingBlock(treeGrower, props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().defaultLayer("cutout").defaultTicksRandomly(true));

    public static final FlexBlockType<FlexDirectionalBlock> DIRECTIONAL = register("directional", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexDirectionalBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().stockProperties(DirectionalBlock.FACING));

    public static final FlexBlockType<FlexHorizontalDirectionalBlock> HORIZONTAL_DIRECTIONAL = register("horizontal_directional", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexHorizontalDirectionalBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().stockProperties(HorizontalDirectionalBlock.FACING));

    public static final FlexBlockType<FlexRotatedPillarBlock> ROTATED_PILLAR = register("rotated_pillar", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexRotatedPillarBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().stockProperties(RotatedPillarBlock.AXIS));

    public static final FlexBlockType<FlexSlabBlock> SLAB = register("slab", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexSlabBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().stockProperties(SlabBlock.TYPE, SlabBlock.WATERLOGGED));

    public static final FlexBlockType<FlexStairsBlock> STAIRS = register("stairs", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexStairsBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().stockProperties(StairBlock.FACING, StairBlock.HALF, StairBlock.SHAPE, StairBlock.WATERLOGGED));

    public static final FlexBlockType<FlexWallBlock> WALL = register("wall", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexWallBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().stockProperties(WallBlock.UP, WallBlock.EAST_WALL, WallBlock.NORTH_WALL, WallBlock.SOUTH_WALL, WallBlock.WEST_WALL, WallBlock.WATERLOGGED));

    public static final FlexBlockType<FlexFenceBlock> FENCE = register("fence", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexFenceBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().stockProperties(FenceBlock.NORTH, FenceBlock.EAST, FenceBlock.SOUTH, FenceBlock.WEST, FenceBlock.WATERLOGGED));

    public static final FlexBlockType<FlexFenceGateBlock> FENCE_GATE = register("fence_gate", data -> {
        var blockSetType = new MutableObject<ResourceLocation>();
        JParse.begin(data)
                .key("wood_type", any -> any.string().map(ResourceLocation::parse).handle(blockSetType::setValue));
        return (props, builder) -> {
            List<Property<?>> _properties = builder.getProperties();
            Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
            var woodTypeName = blockSetType.getValue().toString();
            var woodType = WoodType.values().filter(w -> Objects.equals(w.name(), woodTypeName)).findFirst().orElseThrow();
            return new FlexFenceGateBlock(props, woodType, propertyDefaultValues)
            {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
                {
                    super.createBlockStateDefinition(builder1);
                    _properties.forEach(builder1::add);
                }
            };
        };
    }, DefaultTypeProperties.builder().stockProperties(FenceGateBlock.OPEN, FenceGateBlock.POWERED, FenceGateBlock.IN_WALL));

    public static final FlexBlockType<FlexLeavesBlock> LEAVES = register("leaves", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexLeavesBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, DefaultTypeProperties.builder().defaultLayer("cutout_mipped").defaultSeeThrough(true).stockProperties(LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT));

    public static final FlexBlockType<FlexDoorBlock> DOOR = register("door", data -> {
        var blockSetType = new MutableObject<ResourceLocation>();
        JParse.begin(data)
                .key("block_set_type", any -> any.string().map(ResourceLocation::parse).handle(blockSetType::setValue));
        return (props, builder) -> {
            List<Property<?>> _properties = builder.getProperties();
            Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
            var woodTypeName = blockSetType.getValue().toString();
            var woodType = BlockSetType.values().filter(w -> Objects.equals(w.name(), woodTypeName)).findFirst()
                    .orElseThrow(() -> new ThingParseException("Block set type not found: " + woodTypeName));
            return new FlexDoorBlock(props, woodType, propertyDefaultValues)
            {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
                {
                    super.createBlockStateDefinition(builder1);
                    _properties.forEach(builder1::add);
                }
            };
        };
    }, DefaultTypeProperties.builder().defaultLayer("cutout").defaultSeeThrough(true).stockProperties(DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE, DoorBlock.POWERED, DoorBlock.HALF));

    public static final FlexBlockType<FlexTrapdoorBlock> TRAPDOOR = register("trapdoor", data -> {
        var blockSetType = new MutableObject<ResourceLocation>();
        JParse.begin(data)
                .key("block_set_type", any -> any.string().map(ResourceLocation::parse).handle(blockSetType::setValue));
        return (props, builder) -> {
            List<Property<?>> _properties = builder.getProperties();
            Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
            var woodTypeName = blockSetType.getValue().toString();
            var woodType = BlockSetType.values().filter(w -> Objects.equals(w.name(), woodTypeName)).findFirst().orElseThrow();
            return new FlexTrapdoorBlock(props, woodType, propertyDefaultValues)
            {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
                {
                    super.createBlockStateDefinition(builder1);
                    _properties.forEach(builder1::add);
                }
            };
        };
    }, DefaultTypeProperties.builder().defaultLayer("cutout").defaultSeeThrough(true).stockProperties(TrapDoorBlock.OPEN, TrapDoorBlock.HALF, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED));

    public static final FlexBlockType<FlexLiquidBlock> LIQUID = register("liquid", data -> {
        var extras = JParse.begin(data);
        var fluid = new MutableObject<ResourceLocation>();
        extras.key("fluid", any -> any.string().map(ResourceLocation::parse).handle(fluid::setValue));
        return (props, builder) -> {
            List<Property<?>> _properties = builder.getProperties();
            Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
            var fluidName = fluid.getValue() != null ? fluid.getValue() : builder.getRegistryName();
            var fluidObj = Utils.getOrCrash(BuiltInRegistries.FLUID, fluidName);
            if (!(fluidObj instanceof FlowingFluid flowingFluid))
                throw new RuntimeException("LiquidBlock requires a flowing fluid");
            return new FlexLiquidBlock(props.liquid(), propertyDefaultValues, flowingFluid)
            {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
                {
                    super.createBlockStateDefinition(builder1);
                    _properties.forEach(builder1::add);
                }
            };
        };
    }, DefaultTypeProperties.builder().defaultLayer("translucent").defaultSeeThrough(true).stockProperties(LiquidBlock.LEVEL));

    public static void init()
    {
        /* do nothing */
    }

    public static <T extends Block & IFlexBlock> FlexBlockType<T> register(String name, IBlockSerializer<T> factory, DefaultTypeProperties defaults)
    {
        return Registry.register(ThingRegistries.BLOCK_TYPES, name, new FlexBlockType<>(factory, defaults));
    }

    private final IBlockSerializer<T> factory;
    private final DefaultTypeProperties defaults;

    private FlexBlockType(IBlockSerializer<T> factory, DefaultTypeProperties defaults)
    {
        this.factory = factory;
        this.defaults = defaults;
    }

    public IBlockFactory<T> getFactory(JsonObject data)
    {
        return factory.createFactory(data);
    }

    public DefaultTypeProperties getDefaults()
    {
        return defaults;
    }

    public String toString()
    {
        return "BlockType{" + ThingRegistries.BLOCK_TYPES.getKey(this) + "}";
    }

    public static final class DefaultTypeProperties
    {
        public static DefaultTypeProperties builder()
        {
            return new DefaultTypeProperties();
        }

        private String defaultLayer = "solid";
        private boolean defaultSeeThrough = false;
        private boolean defaultIgnitedByLava = false;
        private boolean defaultReplaceable = false;
        private boolean defaultTicksRandomly = false;
        private Property<?>[] stockProperties;

        private DefaultTypeProperties()
        {
        }

        public List<Property<?>> getStockProperties()
        {
            return stockProperties != null ? Arrays.asList(stockProperties) : Collections.emptyList();
        }

        public String getDefaultLayer()
        {
            return defaultLayer;
        }

        public boolean isDefaultSeeThrough()
        {
            return defaultSeeThrough;
        }

        public boolean isDefaultIgnitedByLava()
        {
            return defaultIgnitedByLava;
        }

        public boolean isDefaultReplaceable()
        {
            return defaultReplaceable;
        }

        public boolean isDefaultTicksRandomly()
        {
            return defaultTicksRandomly;
        }

        public DefaultTypeProperties defaultLayer(String defaultLayer)
        {
            this.defaultLayer = defaultLayer;
            return this;
        }

        public DefaultTypeProperties defaultSeeThrough(boolean defaultSeeThrough)
        {
            this.defaultSeeThrough = defaultSeeThrough;
            return this;
        }

        public DefaultTypeProperties defaultIgnitedByLava(boolean defaultIgnitedByLava)
        {
            this.defaultIgnitedByLava = defaultIgnitedByLava;
            return this;
        }

        public DefaultTypeProperties defaultReplaceable(boolean defaultReplaceable)
        {
            this.defaultReplaceable = defaultReplaceable;
            return this;
        }

        public DefaultTypeProperties defaultTicksRandomly(boolean defaultTicksRandomly)
        {
            this.defaultTicksRandomly = defaultTicksRandomly;
            return this;
        }

        public DefaultTypeProperties stockProperties(Property<?>... stockProperties)
        {
            this.stockProperties = stockProperties;
            return this;
        }
    }
}
