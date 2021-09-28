package gigaherz.jsonthings.things.builders;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import gigaherz.jsonthings.JsonThings;
import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.serializers.BlockType;
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
import java.util.*;
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
    private Map<String, String> propertyDefaultValues;
    private Map<Property<?>, Comparable<?>> propertyDefaultValuesMap;
    private BlockType<?> blockType;
    private ResourceLocation blockMaterial;
    private MaterialColor blockMaterialColor;

    private ItemBuilder itemBuilder;
    private ResourceLocation parentBuilderName;
    private BlockBuilder parentBuilder;
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
        this.parentBuilderName = parentName; // maybe
        this.parentBlock = RegistryObject.of(parentName, ForgeRegistries.BLOCKS);
    }

    public void setProperties(Map<String,Property<?>> properties)
    {
        this.properties = properties.values().stream().toList();
        this.propertiesByName = properties;
    }

    public void withDefaultState(String name, String value)
    {
        if (propertyDefaultValues == null) propertyDefaultValues = new HashMap<>();
        this.propertyDefaultValues.put(name, value);
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
        Material material = getBlockMaterial();
        MaterialColor blockMaterialColor = getBlockMaterialColor();
        Block.Properties props = blockMaterialColor != null ?
                Block.Properties.of(material, blockMaterialColor) :
                Block.Properties.of(material);

        var blockType = getBlockType();
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

        List<Property<?>> properties = getProperties();
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

    public BlockBuilder getParentBuilderName()
    {
        if (parentBuilder == null)
        {
            if (parentBuilderName == null)
                throw new IllegalStateException("Parent not set");
            parentBuilder = JsonThings.blockParser.getBuildersMap().get(parentBuilderName);
            if (parentBuilder == null)
                throw new IllegalStateException("The specified parent " + parentBuilderName + " is not a Json Things defined Block");
        }
        return parentBuilder;
    }

    @Nullable
    public BlockBuilder getParent()
    {
        if (parentBuilderName == null) return null;
        if (parentBuilder == null)
        {
            parentBuilder = JsonThings.blockParser.getBuildersMap().get(parentBlock.getId());
            if (parentBuilder == null)
            {
                parentBuilderName = null;
                return null;
            }
        }
        return parentBuilder;
    }

    @Nullable
    private <T> T getValueWithParent(@Nullable T thisValue, Function<BlockBuilder, T> parentGetter)
    {
        if (thisValue != null) return thisValue;
        if (getParent() != null)
        {
            BlockBuilder parent = getParentBuilderName();
            return parentGetter.apply(parent);
        }
        return null;
    }

    @Nullable
    public BlockType<?> getBlockTypeRaw()
    {
        return getValueWithParent(blockType, BlockBuilder::getBlockTypeRaw);
    }

    public BlockType<?> getBlockType()
    {
        return Utils.orElse(getBlockTypeRaw(), () -> BlockType.PLAIN);
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
    public ResourceLocation getBlockMaterialRaw()
    {
        return getValueWithParent(blockMaterial, BlockBuilder::getBlockMaterialRaw);
    }

    public Material getBlockMaterial()
    {
        var matName = getBlockMaterialRaw();
        var mat = matName != null ? ThingRegistries.BLOCK_MATERIALS.get(matName) : null;
        return Utils.orElse(mat, getBlockType().getDefaultMaterial());
    }

    @Nullable
    public MaterialColor getBlockMaterialColor()
    {
        return getValueWithParent(blockMaterialColor, BlockBuilder::getBlockMaterialColor);
    }

    @Nullable
    public Set<String> getRenderLayersRaw()
    {
        return getValueWithParent(renderLayers, BlockBuilder::getRenderLayersRaw);
    }

    public Set<String> getRenderLayers()
    {
        return Utils.orElse(getRenderLayersRaw(), () -> Collections.singleton(getBlockType().getDefaultLayer()));
    }

    @Nullable
    public String getColorHandler()
    {
        return getValueWithParent(colorHandler, BlockBuilder::getColorHandler);
    }

    @Nullable
    public List<Property<?>> getPropertiesRaw()
    {
        return getValueWithParent(properties, BlockBuilder::getPropertiesRaw);
    }

    public List<Property<?>> getProperties()
    {
        return Utils.orElse(getPropertiesRaw(), List::of);
    }

    @Nullable
    public Map<String, String> getPropertyDefaultValuesRaw()
    {
        return getValueWithParent(propertyDefaultValues, BlockBuilder::getPropertyDefaultValuesRaw);
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

    @Nullable
    public BlockBuilder getParentBuilder()
    {
        return parentBuilder;
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
    public ItemBuilder getItemBuilder()
    {
        return itemBuilder;
    }

    @Nullable
    public RegistryObject<Block> getParentBlock()
    {
        return parentBlock;
    }
}
