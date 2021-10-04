package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.blocks.*;
import dev.gigaherz.jsonthings.things.misc.FlexTreeGrower;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public class BlockType<T extends Block & IFlexBlock>
{
    public static final BlockType<FlexBlock> PLAIN = register("plain", data -> (props, builder) -> {
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

    public static final BlockType<FlexSaplingBlock> SAPLING = register("sapling", data -> (props, builder) -> {
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

    public static final BlockType<FlexDirectionalBlock> DIRECTIONAL = register("directional", data -> (props, builder) -> {
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

    public static final BlockType<FlexHorizontalDirectionalBlock> HORIZONTAL_DIRECTIONAL = register("horizontal_directional", data -> (props, builder) -> {
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

    public static final BlockType<FlexRotatedPillarBlock> ROTATED_PILLAR = register("rotated_pillar", data -> (props, builder) -> {
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

    public static final BlockType<FlexSlabBlock> SLAB = register("slab", data -> (props, builder) -> {
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

    public static final BlockType<FlexStairsBlock> STAIRS = register("stairs", data -> (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        final RegistryObject<Block> parentBlock = builder.getParentBlock();
        if (parentBlock == null)
            throw new IllegalStateException("Stairs blocks need a parent block, but none has been declared.");
        return new FlexStairsBlock(props, propertyDefaultValues, () -> parentBlock.get().defaultBlockState())
        {
            @Override
            protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false, Material.STONE, StairBlock.FACING, StairBlock.HALF, StairBlock.SHAPE, StairBlock.WATERLOGGED);

    public static final BlockType<FlexWallBlock> WALL = register("wall", data -> (props, builder) -> {
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

    public static final BlockType<FlexFenceBlock> FENCE = register("fence", data -> (props, builder) -> {
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

    public static final BlockType<FlexFenceGateBlock> FENCE_GATE = register("fence_gate", data -> (props, builder) -> {
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

    public static final BlockType<FlexLeavesBlock> LEAVES = register("leaves", data -> (props, builder) -> {
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

    public static final BlockType<FlexDoorBlock> DOOR = register("door", data -> (props, builder) -> {
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

    public static final BlockType<FlexTrapdoorBlock> TRAPDOOR = register("trapdoor", data -> (props, builder) -> {
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

    public static void init()
    {
        /* do nothing */
    }

    public static <T extends Block & IFlexBlock> BlockType<T> register(String name, IBlockSerializer<T> factory, String defaultLayer, boolean defaultSeeThrough, Material defaultMaterial, Property<?>... stockProperties)
    {
        return Registry.register(ThingRegistries.BLOCK_TYPES, name, new BlockType<>(factory, Arrays.asList(stockProperties), defaultLayer, defaultSeeThrough, defaultMaterial));
    }

    private final IBlockSerializer<T> factory;
    private final List<Property<?>> stockProperties;
    private final String defaultLayer;
    private final boolean defaultSeeThrough;
    private final Material defaultMaterial;

    private BlockType(IBlockSerializer<T> factory, List<Property<?>> stockProperties, String defaultLayer, boolean defaultSeeThrough, Material defaultMaterial)
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
