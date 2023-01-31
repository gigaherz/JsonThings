package dev.gigaherz.jsonthings.things.builders;

import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.serializers.FlexBlockType;
import dev.gigaherz.jsonthings.things.serializers.IBlockFactory;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class BlockBuilder extends BaseBuilder<IFlexBlock, BlockBuilder>
{
    public static BlockBuilder begin(ThingParser<BlockBuilder> ownerParser, ResourceLocation registryName)
    {
        return new BlockBuilder(ownerParser, registryName);
    }

    private List<Property<?>> properties;
    private Map<String, Property<?>> propertiesByName;
    private Map<String, String> propertyDefaultValues;
    private Map<Property<?>, Comparable<?>> propertyDefaultValuesMap;
    private FlexBlockType<?> blockType;
    private ResourceLocation blockMaterial;
    private MaterialColor blockMaterialColor;

    private ItemBuilder itemBuilder;
    //private RegistryObject<Block> parentBlock;
    private DynamicShape generalShape;
    private DynamicShape collisionShape;
    private DynamicShape raytraceShape;
    private DynamicShape renderShape;
    private Boolean seeThrough;
    private String colorHandler;
    private Boolean requiresToolForDrops;
    private Boolean isAir;
    private Boolean hasCollision;
    private Boolean randomTicks;
    private Integer lightEmission;
    private Float explosionResistance;
    private Float destroyTime;
    private Float friction;
    private Float speedFactor;
    private Float jumpFactor;
    private ResourceLocation soundType;

    private IBlockFactory<? extends Block> factory;

    private BlockBuilder(ThingParser<BlockBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Block";
    }


    public void setBlockType(ResourceLocation typeName)
    {
        FlexBlockType<?> blockType = ThingRegistries.BLOCK_TYPES.get(typeName);
        if (blockType == null)
            throw new IllegalStateException("No known block type with name " + typeName);
        this.blockType = blockType;
    }

    public void setBlockType(FlexBlockType<?> type)
    {
        if (ThingRegistries.BLOCK_TYPES.getKey(type) == null)
            throw new IllegalStateException("Block type not registered!");
        this.blockType = type;
    }

    @Nullable
    public FlexBlockType<?> getBlockTypeRaw()
    {
        return getValue(blockType, BlockBuilder::getBlockTypeRaw);
    }

    public FlexBlockType<?> getBlockType()
    {
        return Utils.orElseGet(getBlockTypeRaw(), () -> FlexBlockType.PLAIN);
    }

    public boolean hasBlockType()
    {
        return getBlockTypeRaw() != null;
    }

    public void setItem(ItemBuilder itemBuilder)
    {
        this.itemBuilder = itemBuilder;
    }

    public void setProperties(Map<String, Property<?>> properties)
    {
        this.properties = properties.values().stream().toList();
        this.propertiesByName = properties;
    }

    @Nullable
    public List<Property<?>> getPropertiesRaw()
    {
        return getValue(properties, BlockBuilder::getPropertiesRaw);
    }

    public List<Property<?>> getProperties()
    {
        return Utils.orElseGet(getPropertiesRaw(), List::of);
    }

    public void setPropertyDefaultValue(String name, String value)
    {
        if (propertyDefaultValues == null) propertyDefaultValues = new HashMap<>();
        this.propertyDefaultValues.put(name, value);
    }

    @Nullable
    public Map<String, String> getPropertyDefaultValuesRaw()
    {
        return getValue(propertyDefaultValues, BlockBuilder::getPropertyDefaultValuesRaw);
    }

    public Map<Property<?>, Comparable<?>> getPropertyDefaultValues()
    {
        if (propertyDefaultValuesMap == null)
        {
            var raw = getPropertyDefaultValuesRaw();
            propertyDefaultValuesMap = raw == null ? Map.of() : raw.entrySet().stream()
                    .map(e -> {
                        var key = propertiesByName.get(e.getKey());
                        var value = Utils.getPropertyValue(key, e.getValue());
                        return Pair.of(key, value);
                    })
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        }
        return propertyDefaultValuesMap;
    }

    public void setMaterial(ResourceLocation material)
    {
        blockMaterial = material;
    }

    @Nullable
    public ResourceLocation getMaterialRaw()
    {
        return getValue(blockMaterial, BlockBuilder::getMaterialRaw);
    }

    public Material getMaterial()
    {
        var matName = getMaterialRaw();
        var mat = matName != null ? ThingRegistries.BLOCK_MATERIALS.get(matName) : null;
        return Utils.orElse(mat, getBlockType().getDefaultMaterial());
    }

    public void setMaterialColor(MaterialColor mapColor)
    {
        blockMaterialColor = mapColor;
    }

    @Nullable
    public MaterialColor getMaterialColor()
    {
        return getValue(blockMaterialColor, BlockBuilder::getMaterialColor);
    }

    public void setGeneralShape(DynamicShape shape)
    {
        this.generalShape = shape;
    }

    @Nullable
    public DynamicShape getGeneralShape()
    {
        return getValue(generalShape, BlockBuilder::getGeneralShape);
    }

    public void setCollisionShape(DynamicShape shape)
    {
        this.collisionShape = shape;
    }

    @Nullable
    public DynamicShape getCollisionShape()
    {
        return getValue(collisionShape, BlockBuilder::getCollisionShape);
    }

    public void setRaytraceShape(DynamicShape shape)
    {
        this.raytraceShape = shape;
    }

    @Nullable
    public DynamicShape getRaytraceShape()
    {
        return getValue(raytraceShape, BlockBuilder::getRaytraceShape);
    }

    public void setRenderShape(DynamicShape shape)
    {
        this.renderShape = shape;
    }

    @Nullable
    public DynamicShape getRenderShape()
    {
        return getValue(renderShape, BlockBuilder::getRenderShape);
    }

    public ResourceLocation getDefaultRenderLayer()
    {
        return new ResourceLocation(getBlockType().getDefaultLayer());
    }

    public void setColorHandler(String colorHandler)
    {
        this.colorHandler = colorHandler;
    }

    @Nullable
    public String getColorHandler()
    {
        return getValue(colorHandler, BlockBuilder::getColorHandler);
    }

    public void setSeeThrough(boolean seeThrough)
    {
        this.seeThrough = seeThrough;
    }

    @Nullable
    public Boolean isSeeThrough()
    {
        return getValue(seeThrough, BlockBuilder::isSeeThrough);
    }

    public void setRequiresToolForDrops(boolean requiresToolForDrops)
    {
        this.requiresToolForDrops = requiresToolForDrops;
    }

    @Nullable
    public Boolean requiresToolForDrops()
    {
        return getValue(requiresToolForDrops, BlockBuilder::requiresToolForDrops);
    }

    public void setIsAir(boolean isAir)
    {
        this.isAir = isAir;
    }

    @Nullable
    public Boolean getIsAir()
    {
        return getValue(isAir, BlockBuilder::getIsAir);
    }

    public void setHasCollision(boolean hasCollision)
    {
        this.hasCollision = hasCollision;
    }

    @Nullable
    public Boolean getHasCollision()
    {
        return getValue(hasCollision, BlockBuilder::getHasCollision);
    }

    public void setTicksRandom(boolean randomTicks)
    {
        this.randomTicks = randomTicks;
    }

    @Nullable
    public Boolean getTicksRandom()
    {
        return getValue(randomTicks, BlockBuilder::getTicksRandom);
    }

    public void setLightEmission(int lightEmission)
    {
        this.lightEmission = lightEmission;
    }

    @Nullable
    public Integer getLightEmission()
    {
        return getValue(lightEmission, BlockBuilder::getLightEmission);
    }

    public void setExplosionResistance(float explosionResistance)
    {
        this.explosionResistance = explosionResistance;
    }

    @Nullable
    public Float getExplosionResistance()
    {
        return getValue(explosionResistance, BlockBuilder::getExplosionResistance);
    }

    public void setDestroyTime(float destroyTime)
    {
        this.destroyTime = destroyTime;
    }

    @Nullable
    public Float getDestroyTime()
    {
        return getValue(destroyTime, BlockBuilder::getDestroyTime);
    }

    public void setFriction(float friction)
    {
        this.friction = friction;
    }

    @Nullable
    public Float getFriction()
    {
        return getValue(friction, BlockBuilder::getFriction);
    }

    public void setSpeedFactor(float speedFactor)
    {
        this.speedFactor = speedFactor;
    }

    @Nullable
    public Float getSpeedFactor()
    {
        return getValue(speedFactor, BlockBuilder::getSpeedFactor);
    }

    public void setJumpFactor(float jumpFactor)
    {
        this.jumpFactor = jumpFactor;
    }

    @Nullable
    public Float getJumpFactor()
    {
        return getValue(jumpFactor, BlockBuilder::getJumpFactor);
    }

    public void setSoundType(ResourceLocation loc)
    {
        this.soundType = loc;
    }

    @Nullable
    public ResourceLocation getSoundType()
    {
        return getValue(soundType, BlockBuilder::getSoundType);
    }

    @Override
    protected IFlexBlock buildInternal()
    {
        Material material = getMaterial();
        MaterialColor blockMaterialColor = getMaterialColor();
        Block.Properties props = blockMaterialColor != null ?
                Block.Properties.of(material, blockMaterialColor) :
                Block.Properties.of(material);

        var blockType = getBlockType();
        if (Utils.orElse(isSeeThrough(), blockType.isDefaultSeeThrough())) props.noOcclusion();
        if (Utils.orElse(requiresToolForDrops(), false)) props.requiresCorrectToolForDrops();
        if (Utils.orElse(getIsAir(), false)) props.air();
        if (!Utils.orElse(getHasCollision(), true)) props.noCollission();
        if (Utils.orElse(getTicksRandom(), false)) props.randomTicks();
        if (Utils.orElse(getLightEmission(), 0) > 0) props.lightLevel(state -> getLightEmission());
        if (Utils.orElse(getExplosionResistance(), 0.0f) > 0.0f) props.explosionResistance(getExplosionResistance());
        if (Utils.orElse(getDestroyTime(), 0.0f) > 0.0f) props.destroyTime(getDestroyTime());
        if (Utils.orElse(getFriction(), 0.6f) != 0.6f) props.friction(getFriction());
        if (Utils.orElse(getSpeedFactor(), 1.0f) != 1) props.speedFactor(getSpeedFactor());
        if (Utils.orElse(getJumpFactor(), 1.0f) != 1) props.jumpFactor(getSpeedFactor());

        if (getSoundType() != null) props.sound(Utils.getOrCrash(ThingRegistries.SOUND_TYPES, getSoundType()));

        final List<Property<?>> stockProperties = blockType.getStockProperties();

        List<Property<?>> properties = getProperties();
        List<Property<?>> badProperties = properties.stream().filter(prop -> {
            for (Property<?> p : stockProperties)
            {
                if (p == prop) continue;
                if (p.getName().equals(prop.getName())) return true;
            }
            return false;
        }).toList();
        if (badProperties.size() > 0)
        {
            throw new IllegalStateException("The block of type " + blockType + " cannot define non-duplicate properties with clashing names: " + badProperties.stream().map(Property::getName).collect(Collectors.joining(" ")));
        }

        IFlexBlock flexBlock = factory.construct(props, this);

        flexBlock.setGeneralShape(getGeneralShape());
        flexBlock.setCollisionShape(getCollisionShape());
        flexBlock.setRaytraceShape(getRaytraceShape());
        flexBlock.setRenderShape(getRenderShape());

        constructEventHandlers(flexBlock);

        return flexBlock;
    }

    public Map<String, Property<?>> getPropertiesByName()
    {
        return Collections.unmodifiableMap(propertiesByName);
    }

    @Nullable
    public ItemBuilder getItemBuilder()
    {
        return itemBuilder;
    }

    public void setFactory(IBlockFactory<?> factory)
    {
        this.factory = factory;
    }
}
