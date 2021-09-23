package gigaherz.jsonthings.things.builders;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.JsonThings;
import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.misc.FlexArmorMaterial;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockBuilder implements Supplier<IFlexBlock>
{
    private JsonObject jsonSource;
    private IFlexBlock builtBlock = null;

    private final ResourceLocation registryName;

    private List<Property<?>> properties;
    private Map<String, Property<?>> propertiesByName;
    private Map<Property<?>, Comparable<?>> propertyDefaultValues;
    private BlockType<?> blockType;
    private ResourceLocation blockMaterial;
    private MaterialColor blockMaterialColor;
    private ItemBuilder itemBuilder;
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

    private BlockBuilder(ResourceLocation registryName, JsonObject data)
    {
        this.registryName = registryName;
        this.jsonSource = data;
    }

    public static BlockBuilder begin(ResourceLocation registryName, JsonObject data)
    {
        return new BlockBuilder(registryName, data);
    }

    public void setBlockType(ResourceLocation typeName)
    {
        BlockType<?> blockType = ThingRegistries.BLOCK_TYPES.get(typeName);
        if (blockType == null)
            throw new IllegalStateException("No known block type with name " + typeName);
        this.blockType = blockType;
    }

    public void withItem(ItemBuilder itemBuilder)
    {
        this.itemBuilder = itemBuilder;
    }

    public void setParentBlock(ResourceLocation parentName)
    {
        if (this.parentBlock != null)
            throw new IllegalStateException("Parent block already set");
        this.parentBlock = RegistryObject.of(parentName, ForgeRegistries.BLOCKS);
    }

    public void setProperties(Map<String,Property<?>> properties)
    {
        this.properties = properties.values().stream().toList();
        this.propertiesByName = properties;
    }

    public void withDefaultState(String name, String value)
    {
        Property<?> prop = propertiesByName.get(name);
        if (prop == null)
            throw new IllegalStateException("No property declared with name " + name);
        this.propertyDefaultValues.put(prop, Utils.getPropertyValue(prop, value));
    }

    public void setGeneralShape(DynamicShape shape)
    {
        this.generalShape = shape;
    }

    public void setCollisionShape(DynamicShape shape)
    {
        this.collisionShape = shape;
    }

    public void setRaytraceShape(DynamicShape shape)
    {
        this.raytraceShape = shape;
    }

    public void setRenderShape(DynamicShape shape)
    {
        this.renderShape = shape;
    }

    public void setRenderLayers(Set<String> renderLayers)
    {
        this.renderLayers = renderLayers;
    }

    public void setSeeThrough(boolean seeThrough)
    {
        this.seeThrough = seeThrough;
    }

    public void setColorHandler(String colorHandler)
    {
        this.colorHandler = colorHandler;
    }

    public void setMaterial(ResourceLocation material)
    {
        blockMaterial = material;
    }

    public void setColor(MaterialColor mapColor)
    {
        blockMaterialColor = mapColor;
    }

    public void setRequiresToolForDrops(boolean requiresToolForDrops)
    {
        this.requiresToolForDrops = requiresToolForDrops;
    }

    public void setIsAir(boolean isAir)
    {
        this.isAir = isAir;
    }

    public void setHasCollision(boolean hasCollision)
    {
        this.hasCollision = hasCollision;
    }

    public void setTicksRandom(boolean randomTicks)
    {
        this.randomTicks = randomTicks;
    }

    public void setLightEmission(int lightEmission)
    {
        this.lightEmission = lightEmission;
    }

    public void setExplosionResistance(int explosionResistance)
    {
        this.explosionResistance = explosionResistance;
    }

    public void setDestroyTime(int destroyTime)
    {
        this.destroyTime = destroyTime;
    }

    public void setFriction(float friction)
    {
        this.friction = friction;
    }

    public void setSpeedFactor(float speedFactor)
    {
        this.speedFactor = speedFactor;
    }

    public void setJumpFactor(float jumpFactor)
    {
        this.jumpFactor = jumpFactor;
    }

    public void setSoundType(ResourceLocation loc)
    {
        this.soundType = loc;
    }

    private IFlexBlock build()
    {

        Material material = Utils.getOrCrash(ThingRegistries.BLOCK_MATERIALS, Utils.orElse(getBlockMaterial(), () -> new ResourceLocation("stone")));
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

        var factory = Utils.orElse(getBlockType(), BlockType.PLAIN).getFactory(jsonSource);
        jsonSource = null;

        IFlexBlock flexBlock = factory.construct(props, this);

        flexBlock.setGeneralShape(getGeneralShape());
        flexBlock.setCollisionShape(getCollisionShape());
        flexBlock.setRaytraceShape(getRaytraceShape());
        flexBlock.setRenderShape(getRenderShape());

        builtBlock = flexBlock;
        return flexBlock;
    }

    public IFlexBlock get()
    {
        if (builtBlock == null)
            return build();
        return builtBlock;
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
    public BlockType<?> getBlockType()
    {
        return getValueWithParent(blockType, BlockBuilder::getBlockType);
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
    public ResourceLocation getBlockMaterial()
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
    public Boolean requiresToolForDrops()
    {
        return getValueWithParent(requiresToolForDrops, BlockBuilder::requiresToolForDrops);
    }

    @Nullable
    public Boolean isAir()
    {
        return getValueWithParent(isAir, BlockBuilder::isAir);
    }

    @Nullable
    public Boolean hasCollision()
    {
        return getValueWithParent(hasCollision, BlockBuilder::hasCollision);
    }

    @Nullable
    public Boolean hasRandomTicks()
    {
        return getValueWithParent(randomTicks, BlockBuilder::hasRandomTicks);
    }

    @Nullable
    public Integer getLightEmission()
    {
        return getValueWithParent(lightEmission, BlockBuilder::getLightEmission);
    }

    @Nullable
    public Integer getExplosionResistance()
    {
        return getValueWithParent(explosionResistance, BlockBuilder::getExplosionResistance);
    }

    @Nullable
    public Integer getDestroyTime()
    {
        return getValueWithParent(destroyTime, BlockBuilder::getDestroyTime);
    }

    @Nullable
    public Float getFriction()
    {
        return getValueWithParent(friction, BlockBuilder::getFriction);
    }

    @Nullable
    public Float getSpeedFactor()
    {
        return getValueWithParent(speedFactor, BlockBuilder::getSpeedFactor);
    }

    @Nullable
    public Float getJumpFactor()
    {
        return getValueWithParent(jumpFactor, BlockBuilder::getJumpFactor);
    }

    @Nullable
    public ResourceLocation getSoundType()
    {
        return getValueWithParent(soundType, BlockBuilder::getSoundType);
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
