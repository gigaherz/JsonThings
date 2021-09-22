package gigaherz.jsonthings.things.builders;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.JsonThings;
import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.serializers.BlockType;
import gigaherz.jsonthings.things.serializers.IBlockFactory;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockBuilder
{
    private IFlexBlock builtBlock = null;

    private final List<Property<?>> properties = Lists.newArrayList();
    private final Map<String, Property<?>> propertiesByName = Maps.newHashMap();
    private final Map<Property<?>, Comparable<?>> propertyDefaultValues = Maps.newHashMap();
    private final ResourceLocation registryName;
    private final BlockType<?> blockType;
    private final IBlockFactory<?> factory;
    private Material blockMaterial = Material.STONE;
    private MaterialColor blockMaterialColor = null;
    private ItemBuilder itemBuilder;
    private ResourceLocation parentBuilder;
    private BlockBuilder parentBuilderObj;
    private RegistryObject<Block> parentBlock;
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;
    private Boolean seeThrough;
    private Set<String> renderLayers;
    private String colorHandler;
    private Boolean requiresToolForDrops;
    private Boolean isAir;
    private Boolean hasCollision;
    private Boolean randomTicks;
    private Integer lightEmission;
    private Integer explosionResistance;
    private Integer destroyTime;
    private Float friction;
    private Float speedFactor;
    private Float jumpFactor;
    private ResourceLocation soundType;

    private BlockBuilder(ResourceLocation registryName, BlockType<?> blockType, IBlockFactory<?> factory)
    {
        this.registryName = registryName;
        this.blockType = blockType;
        this.factory = factory;
    }

    public static BlockBuilder begin(ResourceLocation registryName, String typeName, JsonObject data)
    {
        BlockType<?> blockType = ThingRegistries.BLOCK_TYPES.get(new ResourceLocation(typeName));
        if (blockType == null)
            throw new IllegalStateException("No known block type with name " + typeName);
        return new BlockBuilder(registryName, blockType, blockType.getFactory(data));
    }

    public BlockBuilder withItem(ItemBuilder itemBuilder)
    {
        this.itemBuilder = itemBuilder;
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

    public BlockBuilder withRenderLayers(Set<String> renderLayers)
    {
        this.renderLayers = renderLayers;
        return this;
    }

    public BlockBuilder withSeeThrough(boolean seeThrough)
    {
        this.seeThrough = seeThrough;
        return this;
    }

    public BlockBuilder withColorHandler(String colorHandler)
    {
        this.colorHandler = colorHandler;
        return this;
    }

    public BlockBuilder withMaterial(String material)
    {
        blockMaterial = Utils.getOrCrash(ThingRegistries.BLOCK_MATERIALS, material);
        return this;
    }

    public BlockBuilder withMaterialColor(MaterialColor mapColor)
    {
        blockMaterialColor = mapColor;
        return this;
    }

    public BlockBuilder withRequiresToolForDrops(boolean requiresToolForDrops)
    {
        this.requiresToolForDrops = requiresToolForDrops;
        return this;
    }

    public BlockBuilder withIsAir(boolean isAir)
    {
        this.isAir = isAir;
        return this;
    }

    public BlockBuilder withCollision(boolean hasCollision)
    {
        this.hasCollision = hasCollision;
        return this;
    }

    public BlockBuilder withRandomTicks(boolean randomTicks)
    {
        this.randomTicks = randomTicks;
        return this;
    }

    public BlockBuilder withLightEmission(int lightEmission)
    {
        this.lightEmission = lightEmission;
        return this;
    }

    public BlockBuilder withExplosionResistance(int explosionResistance)
    {
        this.explosionResistance = explosionResistance;
        return this;
    }

    public BlockBuilder withDestroyTime(int destroyTime)
    {
        this.destroyTime = destroyTime;
        return this;
    }

    public BlockBuilder withFriction(float friction)
    {
        this.friction = friction;
        return this;
    }

    public BlockBuilder withSpeedFactor(float speedFactor)
    {
        this.speedFactor = speedFactor;
        return this;
    }

    public BlockBuilder withJumpFactor(float jumpFactor)
    {
        this.jumpFactor = jumpFactor;
        return this;
    }

    public BlockBuilder withSoundType(ResourceLocation loc)
    {
        this.soundType = loc;
        return this;
    }

    public IFlexBlock build()
    {
        Material material = Utils.orElse(getBlockMaterial(), Material.STONE);
        MaterialColor blockMaterialColor = getBlockMaterialColor();
        Block.Properties props = blockMaterialColor != null ?
                Block.Properties.of(material, blockMaterialColor) :
                Block.Properties.of(material);

        if (Utils.orElse(isSeeThrough(), blockType.isDefaultSeeThrough())) props.noOcclusion();
        if (Utils.orElse(requiresToolForDrops(), false)) props.requiresCorrectToolForDrops();
        if (Utils.orElse(isAir(), false)) props.air();
        if (!Utils.orElse(hasCollision(), true)) props.noCollission();
        if (Utils.orElse(hasRandomTicks(), false)) props.randomTicks();
        if (Utils.orElse(getLightEmission(), 0) > 0) props.lightLevel(state -> getLightEmission());
        if (Utils.orElse(getExplosionResistance(), 0) > 0) props.explosionResistance(getExplosionResistance());
        if (Utils.orElse(getDestroyTime(), 0) > 0) props.destroyTime(getDestroyTime());
        if (Utils.orElse(getFriction(), 0.6f) != 0.6f) props.friction(getFriction());
        if (Utils.orElse(getSpeedFactor(), 1.0f) != 1) props.speedFactor(getSpeedFactor());
        if (Utils.orElse(getJumpFactor(), 1.0f) != 1) props.jumpFactor(getSpeedFactor());

        if (getSoundType() != null) props.sound(Utils.getOrCrash(ThingRegistries.SOUND_TYPES, getSoundType()));

        final List<Property<?>> stockProperties = blockType.getStockProperties();

        List<Property<?>> badProperties = properties.stream().filter(prop -> {
            for (Property<?> p : stockProperties)
            {
                if (p == prop) continue;
                if (p.getName().equals(prop.getName())) return true;
            }
            return false;
        }).collect(Collectors.toList());
        if (badProperties.size() > 0)
        {
            throw new IllegalStateException("The block of type " + blockType + " cannot define non-duplicate properties with clashing names: " + badProperties.stream().map(Property::getName).collect(Collectors.joining(" ")));
        }

        IFlexBlock flexBlock = factory.construct(props, this);

        flexBlock.setGeneralShape(getGeneralShape());
        flexBlock.setCollisionShape(getCollisionShape());
        flexBlock.setRaytraceShape(getRaytraceShape());
        flexBlock.setRenderShape(getRenderShape());

        builtBlock = flexBlock;
        return flexBlock;
    }

    public BlockBuilder getParentBuilder()
    {
        if (parentBuilder == null)
            throw new IllegalStateException("Parent builder not set");
        if (parentBuilderObj == null)
        {
            parentBuilderObj = JsonThings.blockParser.getBuildersMap().get(parentBuilder);
        }
        if (parentBuilderObj == null)
            throw new IllegalStateException("Parent builder not found");
        return parentBuilderObj;
    }

    @Nullable
    private <T> T getValueWithParent(@Nullable T thisValue, Function<BlockBuilder, T> parentGetter)
    {
        if (thisValue != null) return thisValue;
        if (parentBuilder != null)
        {
            BlockBuilder parent = getParentBuilder();
            return parentGetter.apply(parent);
        }
        return null;
    }

    @Nullable
    public DynamicShape getGeneralShape()
    {
        return getValueWithParent(generalShape, BlockBuilder::getGeneralShape);
    }

    @Nullable
    public DynamicShape getCollisionShape()
    {
        return getValueWithParent(collisionShape, BlockBuilder::getCollisionShape);
    }

    @Nullable
    public DynamicShape getRaytraceShape()
    {
        return getValueWithParent(raytraceShape, BlockBuilder::getRaytraceShape);
    }

    @Nullable
    public DynamicShape getRenderShape()
    {
        return getValueWithParent(renderShape, BlockBuilder::getRenderShape);
    }

    @Nullable
    public Material getBlockMaterial()
    {
        return getValueWithParent(blockMaterial, BlockBuilder::getBlockMaterial);
    }

    @Nullable
    public MaterialColor getBlockMaterialColor()
    {
        return getValueWithParent(blockMaterialColor, BlockBuilder::getBlockMaterialColor);
    }

    @Nullable
    public Set<String> getRenderLayers()
    {
        return getValueWithParent(renderLayers, BlockBuilder::getRenderLayers);
    }

    @Nullable
    public String getColorHandler()
    {
        return getValueWithParent(colorHandler, BlockBuilder::getColorHandler);
    }

    public List<Property<?>> getProperties()
    {
        return properties;
    }

    public Map<Property<?>, Comparable<?>> getPropertyDefaultValues()
    {
        return propertyDefaultValues;
    }

    @Nullable
    public BlockBuilder getParentBuilderObj()
    {
        return parentBuilderObj;
    }

    @Nullable
    public Boolean isSeeThrough()
    {
        return getValueWithParent(seeThrough, BlockBuilder::isSeeThrough);
    }

    @Nullable
    public Boolean requiresToolForDrops() { return getValueWithParent(requiresToolForDrops, BlockBuilder::requiresToolForDrops); }
    @Nullable
    public Boolean isAir() { return getValueWithParent(isAir, BlockBuilder::isAir); }
    @Nullable
    public Boolean hasCollision() { return getValueWithParent(hasCollision, BlockBuilder::hasCollision); }
    @Nullable
    public Boolean hasRandomTicks() { return getValueWithParent(randomTicks, BlockBuilder::hasRandomTicks); }

    @Nullable
    public Integer getLightEmission() { return getValueWithParent(lightEmission, BlockBuilder::getLightEmission); }
    @Nullable
    public Integer getExplosionResistance() { return getValueWithParent(explosionResistance, BlockBuilder::getExplosionResistance); }
    @Nullable
    public Integer getDestroyTime() { return getValueWithParent(destroyTime, BlockBuilder::getDestroyTime); }

    @Nullable
    public Float getFriction() { return getValueWithParent(friction, BlockBuilder::getFriction); }
    @Nullable
    public Float getSpeedFactor() { return getValueWithParent(speedFactor, BlockBuilder::getSpeedFactor); }
    @Nullable
    public Float getJumpFactor() { return getValueWithParent(jumpFactor, BlockBuilder::getJumpFactor); }

    @Nullable
    public ResourceLocation getSoundType() { return getValueWithParent(soundType, BlockBuilder::getSoundType); }

    public IFlexBlock getBuiltBlock()
    {
        if (builtBlock == null)
            return build();
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

    @Nullable
    public RegistryObject<Block> getParentBlock()
    {
        return parentBlock;
    }

    public Set<String> getRenderLayersOrDefault()
    {
        return Utils.orElse(getRenderLayers(), () -> Collections.singleton(blockType.getDefaultLayer()));
    }
}
