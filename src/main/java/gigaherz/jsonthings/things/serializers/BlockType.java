package gigaherz.jsonthings.things.serializers;

import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.blocks.*;
import net.minecraft.block.*;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlockType
{
    public static final BlockType PLAIN = register("plain", (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false);

    public static final BlockType SLAB = register("slab", (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexSlabBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false, SlabBlock.TYPE, SlabBlock.WATERLOGGED);

    public static final BlockType STAIRS = register("stairs", (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        final RegistryObject<Block> parentBlock = builder.getParentBlock();
        if (parentBlock == null)
            throw new IllegalStateException("Stairs blocks need a parent block, but none has been declared.");
        return new FlexStairsBlock(props, propertyDefaultValues, () -> parentBlock.get().defaultBlockState())
        {
            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false, WallBlock.UP, WallBlock.EAST_WALL, WallBlock.NORTH_WALL, WallBlock.SOUTH_WALL, WallBlock.WEST_WALL, WallBlock.WATERLOGGED);

    public static final BlockType WALL = register("wall", (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexWallBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false, WallBlock.UP, WallBlock.EAST_WALL, WallBlock.NORTH_WALL, WallBlock.SOUTH_WALL, WallBlock.WEST_WALL, WallBlock.WATERLOGGED);

    public static final BlockType FENCE = register("fence", (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexFenceBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false, FenceBlock.NORTH, FenceBlock.EAST, FenceBlock.SOUTH, FenceBlock.WEST, FenceBlock.WATERLOGGED);

    public static final BlockType FENCE_GATE = register("fence_gate", (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexFenceGateBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false, FenceGateBlock.OPEN, FenceGateBlock.POWERED, FenceGateBlock.IN_WALL);

    public static final BlockType ROTATED_PILLAR = register("rotated_pillar", (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexRotatedPillarBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "solid", false, RotatedPillarBlock.AXIS);

    public static final BlockType LEAVES = register("leaves", (props, builder) -> {
        List<Property<?>> _properties = builder.getProperties();
        Map<Property<?>, Comparable<?>> propertyDefaultValues = builder.getPropertyDefaultValues();
        return new FlexLeavesBlock(props, propertyDefaultValues)
        {
            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder1)
            {
                super.createBlockStateDefinition(builder1);
                _properties.forEach(builder1::add);
            }
        };
    }, "cutout_mipped", true, LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT);

    public static void init()
    {
        /* do nothing */
    }

    public static BlockType register(String name, IBlockFactory factory, String defaultLayer, boolean defaultSeeThrough, Property<?>... stockProperties)
    {
        return Registry.register(ThingRegistries.BLOCK_TYPES, name, new BlockType(factory, Arrays.asList(stockProperties), defaultLayer, defaultSeeThrough));
    }

    private final IBlockFactory factory;
    private final List<Property<?>> stockProperties;
    private String defaultLayer;

    private boolean defaultSeeThrough;

    public BlockType(IBlockFactory factory, List<Property<?>> stockProperties, String defaultLayer, boolean defaultSeeThrough)
    {
        this.factory = factory;
        this.stockProperties = stockProperties;
        this.defaultLayer = defaultLayer;
        this.defaultSeeThrough = defaultSeeThrough;
    }

    public IBlockFactory getFactory()
    {
        return factory;
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

    public String toString() {
        return "BlockType{" + ThingRegistries.BLOCK_TYPES.getKey(this) + "}";
    }
}
