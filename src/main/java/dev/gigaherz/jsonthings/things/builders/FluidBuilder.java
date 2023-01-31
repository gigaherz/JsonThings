package dev.gigaherz.jsonthings.things.builders;

import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.serializers.FlexFluidType;
import dev.gigaherz.jsonthings.things.serializers.IFluidFactory;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FluidBuilder extends BaseBuilder<IFlexFluid, FluidBuilder>
{
    public static FluidBuilder begin(ThingParser<FluidBuilder> ownerParser, ResourceLocation registryName)
    {
        return new FluidBuilder(ownerParser, registryName);
    }

    private FlexFluidType<?> fluidType;
    private List<Property<?>> properties;
    private Map<String, Property<?>> propertiesByName;
    private Map<String, String> propertyDefaultValues;
    private Map<Property<?>, Comparable<?>> propertyDefaultValuesMap;

    private ItemBuilder itemBuilder;
    private ResourceLocation parentBuilderName;
    private FluidBuilder parentBuilder;
    private RegistryObject<Fluid> parentFluid;

    private ResourceLocation stillTexture;
    private ResourceLocation flowingTexture;
    private ResourceLocation sideTexture;
    private Rarity rarity = Rarity.COMMON;
    private Integer color;
    private Integer density;
    private Integer luminosity;
    private Integer temperature;
    private Integer viscosity;
    private String translationKey;
    private Boolean isGaseous;
    private ResourceLocation fillSound;
    private ResourceLocation emptySound;
    private Set<String> renderLayers;

    private IFluidFactory<? extends Fluid> factory;

    private FluidBuilder(ThingParser<FluidBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Fluid";
    }

    public void setFluidType(ResourceLocation typeName)
    {
        FlexFluidType<?> FluidType = ThingRegistries.FLUID_TYPES.get(typeName);
        if (FluidType == null)
            throw new IllegalStateException("No known Fluid type with name " + typeName);
        this.fluidType = FluidType;
    }

    public void setBucket(ItemBuilder itemBuilder)
    {
        this.itemBuilder = itemBuilder;
    }

    public void setParentFluid(ResourceLocation parentName)
    {
        if (this.parentFluid != null)
            throw new IllegalStateException("Parent Fluid already set");
        this.parentBuilderName = parentName; // maybe
        this.parentFluid = RegistryObject.create(parentName, ForgeRegistries.FLUIDS);
    }

    public void setProperties(Map<String, Property<?>> properties)
    {
        this.properties = properties.values().stream().toList();
        this.propertiesByName = properties;
    }

    public void setPropertyDefaultValue(String name, String value)
    {
        if (propertyDefaultValues == null) propertyDefaultValues = new HashMap<>();
        this.propertyDefaultValues.put(name, value);
    }

    public void setStillTexture(ResourceLocation stillTexture)
    {
        this.stillTexture = stillTexture;
    }

    public void setFlowingTexture(ResourceLocation flowingTexture)
    {
        this.flowingTexture = flowingTexture;
    }

    public void setSideTexture(ResourceLocation overlay)
    {
        this.sideTexture = overlay;
    }

    public void setRarity(Rarity rarity)
    {
        this.rarity = rarity;
    }

    public void setColor(Integer color)
    {
        this.color = color;
    }

    public void setDensity(Integer density)
    {
        this.density = density;
    }

    public void setLuminosity(Integer luminosity)
    {
        this.luminosity = luminosity;
    }

    public void setTemperature(Integer temperature)
    {
        this.temperature = temperature;
    }

    public void setViscosity(Integer viscosity)
    {
        this.viscosity = viscosity;
    }

    public void setTranslationKey(String translationKey)
    {
        this.translationKey = translationKey;
    }

    public void setGaseous(Boolean gaseous)
    {
        isGaseous = gaseous;
    }

    public void setFillSound(ResourceLocation fillSound)
    {
        this.fillSound = fillSound;
    }

    public void setEmptySound(ResourceLocation emptySound)
    {
        this.emptySound = emptySound;
    }

    public void setRenderLayers(Set<String> renderLayers)
    {
        this.renderLayers = renderLayers;
    }

    @Override
    protected IFlexFluid buildInternal()
    {
        var fluidType = getFluidType();
        //if (Utils.orElse(getDestroyTime(), 0.0f) > 0.0f) props.destroyTime(getDestroyTime());

        //if (getSoundType() != null) props.sound(Utils.getOrCrash(ThingRegistries.SOUND_TYPES, getSoundType()));

        final List<Property<?>> stockProperties = fluidType.getStockProperties();

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
            throw new IllegalStateException("The fluid of type " + fluidType + " cannot define non-duplicate properties with clashing names: " + badProperties.stream().map(Property::getName).collect(Collectors.joining(" ")));
        }

        IFlexFluid flexFluid = factory.construct(this);

        var attrsBuilder = NonNullLazy.of(() -> {
            var attrs = FluidAttributes.builder(getStillTexture(), getFlowingTexture());
            if (getColor() != null) attrs.color(getColor());
            if (getDensity() != null) attrs.density(getDensity());
            if (getLuminosity() != null) attrs.luminosity(getLuminosity());
            if (getSideTexture() != null) attrs.overlay(getSideTexture());
            if (getRarity() != null) attrs.rarity(getRarity());
            var fillSound = getFillSound() != null ? Utils.getOrCrash(ForgeRegistries.SOUND_EVENTS, getFillSound()) : null;
            var emptySound = getEmptySound() != null ? Utils.getOrCrash(ForgeRegistries.SOUND_EVENTS, getEmptySound()) : null;
            if (getFillSound() != null && getEmptySound() != null) attrs.sound(fillSound, emptySound);
            if (getTemperature() != null) attrs.temperature(getTemperature());
            if (getViscosity() != null) attrs.viscosity(getViscosity());
            if (getTranslationKey() != null) attrs.translationKey(getTranslationKey());
            if (getIsGaseous() != null && getIsGaseous()) attrs.gaseous();
            return attrs.build(flexFluid.self());
        });

        if (getBucketBuilder() != null)
            flexFluid.setBucketItem(Lazy.of(() -> getBucketBuilder().get().self()));
        flexFluid.setAttributesBuilder(attrsBuilder);

        constructEventHandlers(flexFluid);

        return flexFluid;
    }

    public FluidBuilder getParentBuilderName()
    {
        if (parentBuilder == null)
        {
            if (parentBuilderName == null)
                throw new IllegalStateException("Parent not set");
            parentBuilder = JsonThings.fluidParser.getBuildersMap().get(parentBuilderName);
            if (parentBuilder == null)
                throw new IllegalStateException("The specified parent " + parentBuilderName + " is not a Json Things defined Fluid");
        }
        return parentBuilder;
    }

    @Nullable
    public FluidBuilder getParent()
    {
        if (parentBuilderName == null) return null;
        if (parentBuilder == null)
        {
            parentBuilder = JsonThings.fluidParser.getBuildersMap().get(parentFluid.getId());
            if (parentBuilder == null)
            {
                parentBuilderName = null;
                return null;
            }
        }
        return parentBuilder;
    }

    @Nullable
    public FlexFluidType<?> getFluidTypeRaw()
    {
        return getValue(fluidType, FluidBuilder::getFluidTypeRaw);
    }

    public FlexFluidType<?> getFluidType()
    {
        return Utils.orElseGet(getFluidTypeRaw(), () -> FlexFluidType.PLAIN);
    }

    @Nullable
    public List<Property<?>> getPropertiesRaw()
    {
        return getValue(properties, FluidBuilder::getPropertiesRaw);
    }

    public List<Property<?>> getProperties()
    {
        return Utils.orElseGet(getPropertiesRaw(), List::of);
    }

    @Nullable
    public Map<String, String> getPropertyDefaultValuesRaw()
    {
        return getValue(propertyDefaultValues, FluidBuilder::getPropertyDefaultValuesRaw);
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
    public FluidBuilder getParentBuilder()
    {
        return parentBuilder;
    }

    public Map<String, Property<?>> getPropertiesByName()
    {
        return Collections.unmodifiableMap(propertiesByName);
    }

    @Nullable
    public ItemBuilder getBucketBuilder()
    {
        return itemBuilder;
    }

    @Nullable
    public RegistryObject<Fluid> getParentFluid()
    {
        return parentFluid;
    }

    @Nullable
    private ResourceLocation getStillTexture()
    {
        return getValue(stillTexture, FluidBuilder::getStillTexture);
    }

    @Nullable
    private ResourceLocation getFlowingTexture()
    {
        return getValue(flowingTexture, FluidBuilder::getFlowingTexture);
    }

    @Nullable
    private Rarity getRarity()
    {
        return getValue(rarity, FluidBuilder::getRarity);
    }

    @Nullable
    private ResourceLocation getSideTexture()
    {
        return getValue(sideTexture, FluidBuilder::getSideTexture);
    }

    @Nullable
    private Integer getColor()
    {
        return getValue(color, FluidBuilder::getColor);
    }

    @Nullable
    private Integer getDensity()
    {
        return getValue(density, FluidBuilder::getDensity);
    }

    @Nullable
    private Integer getLuminosity()
    {
        return getValue(luminosity, FluidBuilder::getLuminosity);
    }

    @Nullable
    private Integer getTemperature()
    {
        return getValue(temperature, FluidBuilder::getTemperature);
    }

    @Nullable
    private Integer getViscosity()
    {
        return getValue(viscosity, FluidBuilder::getViscosity);
    }

    @Nullable
    private String getTranslationKey()
    {
        return getValue(translationKey, FluidBuilder::getTranslationKey);
    }

    @Nullable
    private Boolean getIsGaseous()
    {
        return getValue(isGaseous, FluidBuilder::getIsGaseous);
    }

    @Nullable
    private ResourceLocation getFillSound()
    {
        return getValue(fillSound, FluidBuilder::getFillSound);
    }

    @Nullable
    private ResourceLocation getEmptySound()
    {
        return getValue(emptySound, FluidBuilder::getEmptySound);
    }

    @Nullable
    public Set<String> getRenderLayersRaw()
    {
        return getValue(renderLayers, FluidBuilder::getRenderLayersRaw);
    }

    public Set<String> getRenderLayers()
    {
        return Utils.orElseGet(getRenderLayersRaw(), () -> Collections.singleton(getFluidType().getDefaultLayer()));
    }

    public ResourceLocation getDefaultRenderLayer()
    {
        return new ResourceLocation(getFluidType().getDefaultLayer());
    }

    public void register(IForgeRegistry<Fluid> registry)
    {
        if (isInErrorState()) return;
        get();
        factory.register(this, (name, obj) -> registry.register(obj.setRegistryName(name)));
    }

    public void setFactory(IFluidFactory<?> factory)
    {
        this.factory = factory;
    }

    public Iterable<Fluid> getAllSiblings()
    {
        return factory.getAllSiblings(this);
    }
}
