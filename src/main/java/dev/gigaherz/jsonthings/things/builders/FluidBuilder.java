package dev.gigaherz.jsonthings.things.builders;

import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.serializers.FlexFluidType;
import dev.gigaherz.jsonthings.things.serializers.IFluidFactory;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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

    private Supplier<FluidType> attributesType;

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

    public void setAttributesType(Supplier<FluidType> attributesType)
    {
        this.attributesType = attributesType;
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

        if (getBucketBuilder() != null)
            flexFluid.setBucketItem(Lazy.of(() -> getBucketBuilder().get()));

        constructEventHandlers(flexFluid);

        return flexFluid;
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

    public Map<String, Property<?>> getPropertiesByName()
    {
        return Collections.unmodifiableMap(propertiesByName);
    }

    @Nullable
    public ItemBuilder getBucketBuilder()
    {
        return itemBuilder;
    }

    public Supplier<FluidType> getAttributesType()
    {
        var val = getValue(attributesType, FluidBuilder::getAttributesType);
        if (val == null)
            throw new IllegalStateException("fluid_type not set!");
        return val;
    }

    public ResourceLocation getDefaultRenderLayer()
    {
        return ResourceLocation.parse(getFluidType().getDefaultLayer());
    }

    public void register(BiConsumer<ResourceLocation, Fluid> register)
    {
        if (isInErrorState()) return;
        get();
        factory.register(this, register);
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
