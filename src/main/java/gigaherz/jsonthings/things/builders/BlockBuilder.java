package gigaherz.jsonthings.things.builders;

import com.google.common.collect.*;
import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.blocks.*;
import gigaherz.jsonthings.things.parsers.ThingResourceManager;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class BlockBuilder
{
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private Block builtBlock = null;

    private final List<Property<?>> properties = Lists.newArrayList();
    private final Map<String, Property<?>> propertiesByName = Maps.newHashMap();
    private final Map<Property<?>, Comparable<?>> propertyDefaultValues = Maps.newHashMap();
    private BlockType blockType = BlockType.PLAIN;
    private Material blockMaterial = Material.STONE;
    private MaterialColor blockMaterialColor = null;
    private ResourceLocation registryName;
    private ItemBuilder itemBuilder;
    private ResourceLocation parentBuilder;
    private BlockBuilder parentBuilderObj;
    private RegistryObject<Block> parentBlock;
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;

    private BlockBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static BlockBuilder begin(ResourceLocation registryName)
    {
        return new BlockBuilder(registryName);
    }

    public BlockBuilder withItem(ItemBuilder itemBuilder)
    {
        this.itemBuilder = itemBuilder;
        return this;
    }

    public BlockBuilder withType(String typeName)
    {
        BlockType blockType = BlockType.byName(typeName);
        if (blockType == null)
            throw new IllegalStateException("No known block type with name " + typeName);
        this.blockType = blockType;
        return this;
    }

    public BlockBuilder withParentBlock(ResourceLocation parentName)
    {
        if (this.parentBuilder != null)
            throw new IllegalStateException("Cannot set parent block and parent builder at the same time");
        if (this.parentBlock != null)
            throw new IllegalStateException("Parent block already set");
        this.parentBlock = RegistryObject.of(parentName, ForgeRegistries.BLOCKS);
        return this;
    }

    public BlockBuilder withParentBuilder(ResourceLocation parentName)
    {
        if (this.parentBuilder != null)
            throw new IllegalStateException("Parent builder already set");
        if (this.parentBlock != null)
            throw new IllegalStateException("Cannot set parent block and parent builder at the same time");
        this.parentBlock = RegistryObject.of(parentName, ForgeRegistries.BLOCKS);
        this.parentBuilder = parentName;
        return this;
    }

    public BlockBuilder withProperty(Property<?> property)
    {
        String propertyName = property.getName();
        if (propertiesByName.containsKey(propertyName))
            throw new IllegalStateException("A property with name " + propertyName + " has already been declared");
        this.properties.add(property);
        this.propertiesByName.put(propertyName, property);
        return this;
    }

    public BlockBuilder withDefaultState(String name, String value)
    {
        Property<?> prop = propertiesByName.get(name);
        if (prop == null)
            throw new IllegalStateException("No property declared with name " + name);
        this.propertyDefaultValues.put(prop, Utils.getPropertyValue(prop, value));
        return this;
    }

    public BlockBuilder withGeneralShape(DynamicShape shape)
    {
        this.generalShape = shape;
        return this;
    }

    public BlockBuilder withCollisionShape(DynamicShape shape)
    {
        this.collisionShape = shape;
        return this;
    }

    public BlockBuilder withRaytraceShape(DynamicShape shape)
    {
        this.raytraceShape = shape;
        return this;
    }

    public BlockBuilder withRenderShape(DynamicShape shape)
    {
        this.renderShape = shape;
        return this;
    }

    public Block build()
    {
        Block.Properties props;

        if (parentBuilder != null)
        {
            Block parent = getParentBuilder().builtBlock;
            props = AbstractBlock.Properties.copy(parent);
        }
        else
        {
            props = blockMaterialColor != null ?
                    Block.Properties.of(blockMaterial, blockMaterialColor) :
                    Block.Properties.of(blockMaterial);
        }

        final Block baseBlock;
        final List<Property<?>> stockProperties;

        //TODO: make into a registry with factories
        switch(blockType)
        {
            case PLAIN:
                baseBlock = new FlexBlock(props, propertyDefaultValues)
                {
                    @Override
                    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                    {
                        super.createBlockStateDefinition(builder);
                        BlockBuilder.this.properties.forEach(builder::add);
                    }
                };
                stockProperties = Collections.emptyList();
                break;
            case SLAB:
                baseBlock = new FlexSlabBlock(props, propertyDefaultValues)
                {
                    @Override
                    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                    {
                        super.createBlockStateDefinition(builder);
                        BlockBuilder.this.properties.forEach(builder::add);
                    }
                };
                stockProperties = Arrays.asList(SlabBlock.TYPE, SlabBlock.WATERLOGGED);
                break;
            case STAIRS:
                if (parentBlock == null)
                    throw new IllegalStateException("StairsBlock needs a parent block, but none has been declared.");
                baseBlock = new FlexStairsBlock(props, propertyDefaultValues, () -> parentBlock.get().defaultBlockState())
                {
                    @Override
                    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                    {
                        super.createBlockStateDefinition(builder);
                        BlockBuilder.this.properties.forEach(builder::add);
                    }
                };
                stockProperties = Arrays.asList(WallBlock.UP, WallBlock.EAST_WALL, WallBlock.NORTH_WALL, WallBlock.SOUTH_WALL, WallBlock.WEST_WALL, WallBlock.WATERLOGGED);
                break;
            case WALL:
                baseBlock = new FlexWallBlock(props, propertyDefaultValues)
                {
                    @Override
                    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                    {
                        super.createBlockStateDefinition(builder);
                        BlockBuilder.this.properties.forEach(builder::add);
                    }
                };
                stockProperties = Arrays.asList(WallBlock.UP, WallBlock.EAST_WALL, WallBlock.NORTH_WALL, WallBlock.SOUTH_WALL, WallBlock.WEST_WALL, WallBlock.WATERLOGGED);
                break;
            case FENCE:
                baseBlock = new FlexFenceBlock(props, propertyDefaultValues)
                {
                    @Override
                    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                    {
                        super.createBlockStateDefinition(builder);
                        BlockBuilder.this.properties.forEach(builder::add);
                    }
                };
                stockProperties = Arrays.asList(FenceBlock.NORTH,FenceBlock.EAST,FenceBlock.SOUTH,FenceBlock.WEST,FenceBlock.WATERLOGGED);
                break;
            case FENCE_GATE:
                baseBlock = new FlexFenceGateBlock(props, propertyDefaultValues)
                {
                    @Override
                    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                    {
                        super.createBlockStateDefinition(builder);
                        BlockBuilder.this.properties.forEach(builder::add);
                    }
                };
                stockProperties = Arrays.asList( FenceGateBlock.OPEN, FenceGateBlock.POWERED, FenceGateBlock.IN_WALL);
                break;
            case ROTATED_PILLAR:
                baseBlock = new FlexRotatedPillarBlock(props, propertyDefaultValues)
                {
                    @Override
                    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                    {
                        super.createBlockStateDefinition(builder);
                        BlockBuilder.this.properties.forEach(builder::add);
                    }
                };
                stockProperties = Collections.singletonList(RotatedPillarBlock.AXIS);
                break;
            case LEAVES:
                baseBlock = new FlexLeavesBlock(props, propertyDefaultValues)
                {
                    @Override
                    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                    {
                        super.createBlockStateDefinition(builder);
                        BlockBuilder.this.properties.forEach(builder::add);
                    }
                };
                stockProperties = Arrays.asList(LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT);
                break;
            default:
                throw new IllegalStateException("Block Type not implemented: " + blockType);
        }

        List<Property<?>> badProperties = properties.stream().filter(prop -> {
            for(Property<?> p : stockProperties)
            {
                if (p == prop) continue;
                if (p.getName().equals(prop.getName())) return true;
            }
            return false;
        }).collect(Collectors.toList());
        if (badProperties.size() > 0)
        {
            throw new IllegalStateException("The block of type " + blockType.getSerializedName() + " cannot define non-duplicate properties with clashing names: " + badProperties.stream().map(Property::getName).collect(Collectors.joining(" ")));
        }

        IFlexBlock flexBlock = (IFlexBlock) baseBlock;

        baseBlock.setRegistryName(registryName);

        // TODO
        flexBlock.setGeneralShape(getGeneralShape());
        flexBlock.setCollisionShape(getCollisionShape());
        flexBlock.setRaytraceShape(getRaytraceShape());
        flexBlock.setRenderShape(getRenderShape());

        builtBlock = baseBlock;
        return baseBlock;
    }

    public BlockBuilder getParentBuilder()
    {
        if (parentBuilder == null)
            throw new IllegalStateException("Parent builder not set");
        if (parentBuilderObj == null)
        {
            parentBuilderObj = ThingResourceManager.INSTANCE.blockParser.getBuildersMap().get(parentBuilder);
        }
        if (parentBuilderObj == null)
            throw new IllegalStateException("Parent builder not found");
        return parentBuilderObj;
    }

    @Nullable
    public DynamicShape getGeneralShape()
    {
        if (parentBuilder != null)
        {
            BlockBuilder parent = getParentBuilder();
            DynamicShape shape = parent.getGeneralShape();
            if (shape != null) return shape;
        }
        return generalShape;
    }

    @Nullable
    public DynamicShape getCollisionShape()
    {
        if (parentBuilder != null)
        {
            BlockBuilder parent = getParentBuilder();
            DynamicShape shape = parent.getCollisionShape();
            if (shape != null) return shape;
        }
        return collisionShape;
    }

    @Nullable
    public DynamicShape getRaytraceShape()
    {
        if (parentBuilder != null)
        {
            BlockBuilder parent = getParentBuilder();
            DynamicShape shape = parent.getRaytraceShape();
            if (shape != null) return shape;
        }
        return raytraceShape;
    }

    @Nullable
    public DynamicShape getRenderShape()
    {
        if (parentBuilder != null)
        {
            BlockBuilder parent = getParentBuilder();
            DynamicShape shape = parent.getRenderShape();
            if (shape != null) return shape;
        }
        return renderShape;
    }

    public Block getBuiltBlock()
    {
        if (builtBlock == null)
            throw new IllegalStateException("getBuiltBlock called too early!");
        return builtBlock;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public Map<String, Property<?>> getPropertiesByName()
    {
        return Collections.unmodifiableMap(propertiesByName);
    }

    //TODO: make into a registry with factories
    public enum BlockType implements IStringSerializable
    {
        PLAIN("plain"),
        SLAB("slab"),
        STAIRS("stairs"),
        WALL("wall"),
        FENCE("fence"),
        FENCE_GATE("fence_gate"),
        ROTATED_PILLAR("rotated_pillar"),
        LEAVES("leaves");

        private final String name;

        BlockType(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        private static BlockType[] values = values();

        @Nullable
        public static BlockType byName(String name)
        {
            for(BlockType value : values)
            {
                if (value.getSerializedName().equals(name))
                    return value;
            }
            return null;
        }
    }
}
