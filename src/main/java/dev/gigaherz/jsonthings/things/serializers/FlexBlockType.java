package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.blocks.*;
import dev.gigaherz.jsonthings.things.misc.FlexTreeGrower;
import dev.gigaherz.jsonthings.util.Utils;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
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
    }, "solid", false, Material.STONE);

    public static final FlexBlockType<FlexSaplingBlock> SAPLING = register("sapling", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        var featureId = new ResourceLocation(GsonHelper.getAsString(data, "tree_feature"));
        var featureKey = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, featureId);
        var treeGrower = new FlexTreeGrower(featureKey);
        return new FlexSaplingBlock(treeGrower, props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "cutout", false, Material.PLANT);

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
    }, "solid", false, Material.STONE, DirectionalBlock.FACING);

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
    }, "solid", false, Material.STONE, HorizontalDirectionalBlock.FACING);

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
    }, "solid", false, Material.STONE, RotatedPillarBlock.AXIS);

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
    }, "solid", false, Material.STONE, SlabBlock.TYPE, SlabBlock.WATERLOGGED);

    public static final FlexBlockType<FlexStairsBlock> STAIRS = register("stairs", data -> {

        MutableObject<ResourceLocation> parent = new MutableObject<>();

        JParse.begin(data)
                .ifKey("stairs_parent", val -> val.string().map(ResourceLocation::new).handle(parent::setValue));

        return (props, builder) -> {

            var parentName = parent.getValue();

            if (parentName == null)
            {
                var parentBuilder = builder.getParent();
                if (parentBuilder == null)
                    throw new IllegalStateException("Stairs blocks need a parent block, but none has been declared.");
                parentName = parentBuilder.getRegistryName();
            }

            var parentBlock = RegistryObject.create(parentName, ForgeRegistries.BLOCKS);

            List<Property<?>> _properties = builder.getProperties();
            Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
            return new FlexStairsBlock(props, propertyDefaultValues, () -> parentBlock.get().defaultBlockState())
            {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
                {
                    super.createBlockStateDefinition(builder1);
                    _properties.forEach(builder1::add);
                }
            };
        };
    }, "solid", false, Material.STONE, StairBlock.FACING, StairBlock.HALF, StairBlock.SHAPE, StairBlock.WATERLOGGED);

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
    }, "solid", false, Material.STONE, WallBlock.UP, WallBlock.EAST_WALL, WallBlock.NORTH_WALL, WallBlock.SOUTH_WALL, WallBlock.WEST_WALL, WallBlock.WATERLOGGED);

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
    }, "solid", false, Material.STONE, FenceBlock.NORTH, FenceBlock.EAST, FenceBlock.SOUTH, FenceBlock.WEST, FenceBlock.WATERLOGGED);

    public static final FlexBlockType<FlexFenceGateBlock> FENCE_GATE = register("fence_gate", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexFenceGateBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false, Material.STONE, FenceGateBlock.OPEN, FenceGateBlock.POWERED, FenceGateBlock.IN_WALL);

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
    }, "cutout_mipped", true, Material.LEAVES, LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT);

    public static final FlexBlockType<FlexDoorBlock> DOOR = register("door", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexDoorBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "cutout", true, Material.STONE, DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE, DoorBlock.POWERED, DoorBlock.HALF);

    public static final FlexBlockType<FlexTrapdoorBlock> TRAPDOOR = register("trapdoor", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexTrapdoorBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "cutout", true, Material.STONE, TrapDoorBlock.OPEN, TrapDoorBlock.HALF, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);

    public static final FlexBlockType<FlexLiquidBlock> LIQUID = register("liquid", data -> {
        var extras = JParse.begin(data);
        var fluid = new MutableObject<ResourceLocation>();
        extras.key("fluid", any -> any.string().map(ResourceLocation::new).handle(fluid::setValue));
        return (props, builder) -> {
            List<Property<?>> _properties = builder.getProperties();
            Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
            var fluidName = fluid.getValue() != null ? fluid.getValue() : builder.getRegistryName();
            var fluidSupplier = Lazy.<FlowingFluid>of(() -> {
                var fluidObj = Utils.getOrCrash(ForgeRegistries.FLUIDS, fluidName);
                if (!(fluidObj instanceof FlowingFluid flowing))
                    throw new RuntimeException("LiquidBlock requires a flowing fluid");
                return flowing;
            });
            return new FlexLiquidBlock(props, propertyDefaultValues, fluidSupplier)
            {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
                {
                    super.createBlockStateDefinition(builder1);
                    _properties.forEach(builder1::add);
                }
            };
        };
    }, "translucent", true, Material.WATER);

    public static void init()
    {
        /* do nothing */
    }

    public static <T extends Block & IFlexBlock> FlexBlockType<T> register(String name, IBlockSerializer<T> factory, String defaultLayer, boolean defaultSeeThrough, Material defaultMaterial, Property<?>... stockProperties)
    {
        return Registry.register(ThingRegistries.BLOCK_TYPES, name, new FlexBlockType<>(factory, Arrays.asList(stockProperties), defaultLayer, defaultSeeThrough, defaultMaterial));
    }

    private final IBlockSerializer<T> factory;
    private final List<Property<?>> stockProperties;
    private final String defaultLayer;
    private final boolean defaultSeeThrough;
    private final Material defaultMaterial;

    private FlexBlockType(IBlockSerializer<T> factory, List<Property<?>> stockProperties, String defaultLayer, boolean defaultSeeThrough, Material defaultMaterial)
    {
        this.factory = factory;
        this.stockProperties = stockProperties;
        this.defaultLayer = defaultLayer;
        this.defaultSeeThrough = defaultSeeThrough;
        this.defaultMaterial = defaultMaterial;
    }

    public IBlockFactory<T> getFactory(JsonObject data)
    {
        return factory.createFactory(data);
    }

    public List<Property<?>> getStockProperties()
    {
        return stockProperties;
    }

    public String getDefaultLayer()
    {
        return defaultLayer;
    }

    public boolean isDefaultSeeThrough()
    {
        return defaultSeeThrough;
    }

    public Material getDefaultMaterial()
    {
        return defaultMaterial;
    }

    public String toString()
    {
        return "BlockType{" + ThingRegistries.BLOCK_TYPES.getKey(this) + "}";
    }
}
