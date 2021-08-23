package gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.blocks.*;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
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
    }, "solid", false);

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
    }, "solid", false, SlabBlock.TYPE, SlabBlock.WATERLOGGED);

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
    }, "solid", false, WallBlock.UP, WallBlock.EAST_WALL, WallBlock.NORTH_WALL, WallBlock.SOUTH_WALL, WallBlock.WEST_WALL, WallBlock.WATERLOGGED);

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
    }, "solid", false, WallBlock.UP, WallBlock.EAST_WALL, WallBlock.NORTH_WALL, WallBlock.SOUTH_WALL, WallBlock.WEST_WALL, WallBlock.WATERLOGGED);

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
    }, "solid", false, FenceBlock.NORTH, FenceBlock.EAST, FenceBlock.SOUTH, FenceBlock.WEST, FenceBlock.WATERLOGGED);

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
    }, "solid", false, FenceGateBlock.OPEN, FenceGateBlock.POWERED, FenceGateBlock.IN_WALL);

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
    }, "solid", false, RotatedPillarBlock.AXIS);

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
    }, "cutout_mipped", true, LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT);

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
    }, "cutout", true, DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE, DoorBlock.POWERED, DoorBlock.HALF);

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
    }, "cutout", true, TrapDoorBlock.OPEN, TrapDoorBlock.HALF, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);

    public static void init()
    {
        /* do nothing */
    }

    public static <T extends Block & IFlexBlock> BlockType<T> register(String name, IBlockSerializer<T> factory, String defaultLayer, boolean defaultSeeThrough, Property<?>... stockProperties)
    {
        return Registry.register(ThingRegistries.BLOCK_TYPES, name, new BlockType<>(factory, Arrays.asList(stockProperties), defaultLayer, defaultSeeThrough));
    }

    private final IBlockSerializer<T> factory;
    private final List<Property<?>> stockProperties;
    private final String defaultLayer;
    private final boolean defaultSeeThrough;

    private BlockType(IBlockSerializer<T> factory, List<Property<?>> stockProperties, String defaultLayer, boolean defaultSeeThrough)
    {
        this.factory = factory;
        this.stockProperties = stockProperties;
        this.defaultLayer = defaultLayer;
        this.defaultSeeThrough = defaultSeeThrough;
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

    public String toString()
    {
        return "BlockType{" + ThingRegistries.BLOCK_TYPES.getKey(this) + "}";
    }
}
