package dev.gigaherz.jsonthings.things.builders;

import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.scripting.ScriptParser;
import dev.gigaherz.jsonthings.things.serializers.FlexFluidType;
import dev.gigaherz.jsonthings.things.serializers.IFluidFactory;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FluidBuilder extends BaseBuilder<IFlexFluid>
{
    private FlexFluidType<?> fluidType;
    private List<Property<?>> properties;
    private Map<String, Property<?>> propertiesByName;
    private Map<String, String> propertyDefaultValues;
    private Map<Property<?>, Comparable<?>> propertyDefaultValuesMap;

    private ItemBuilder itemBuilder;
    private ResourceLocation parentBuilderName;
    private FluidBuilder parentBuilder;
    private RegistryObject<Fluid> parentFluid;

    private ResourceLocation attributesType;

    private Set<String> renderLayers;

    private IFluidFactory<? extends Fluid> factory;

    private FluidBuilder(ResourceLocation registryName)
    {
        super(registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Fluid";
    }

    public static FluidBuilder begin(ResourceLocation registryName)
    {
        return new FluidBuilder(registryName);
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

    public void setAttributesType(ResourceLocation attributesType)
    {
        this.attributesType = attributesType;
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

        if (getBucketBuilder() != null)
            flexFluid.setBucketItem(Lazy.of(() -> getBucketBuilder().get().self()));

        if (ScriptParser.isEnabled())
        {
            forEachEvent((key, list) -> {
                for (var ev : list)
                {
                    flexFluid.addEventHandler(key, ScriptParser.instance().getEvent(ev));
                }
            });
        }

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
    private <T> T getValueWithParent(@Nullable T thisValue, Function<FluidBuilder, T> parentGetter)
    {
        if (thisValue != null) return thisValue;
        if (getParent() != null)
        {
            FluidBuilder parent = getParentBuilderName();
            return parentGetter.apply(parent);
        }
        return null;
    }

    @Nullable
    public FlexFluidType<?> getFluidTypeRaw()
    {
        return getValueWithParent(fluidType, FluidBuilder::getFluidTypeRaw);
    }

    public FlexFluidType<?> getFluidType()
    {
        return Utils.orElse(getFluidTypeRaw(), () -> FlexFluidType.PLAIN);
    }

    @Nullable
    public List<Property<?>> getPropertiesRaw()
    {
        return getValueWithParent(properties, FluidBuilder::getPropertiesRaw);
    }

    public List<Property<?>> getProperties()
    {
        return Utils.orElse(getPropertiesRaw(), List::of);
    }

    @Nullable
    public Map<String, String> getPropertyDefaultValuesRaw()
    {
        return getValueWithParent(propertyDefaultValues, FluidBuilder::getPropertyDefaultValuesRaw);
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

    public ResourceLocation getAttributesType()
    {
        var val = getValueWithParent(attributesType, FluidBuilder::getAttributesType);
        if (val == null)
            throw new IllegalStateException("fluid_type not set!");
        return val;
    }

    @Nullable
    public Set<String> getRenderLayersRaw()
    {
        return getValueWithParent(renderLayers, FluidBuilder::getRenderLayersRaw);
    }

    public Set<String> getRenderLayers()
    {
        return Utils.orElse(getRenderLayersRaw(), () -> Collections.singleton(getFluidType().getDefaultLayer()));
    }

    private void forEachEvent(BiConsumer<String, List<ResourceLocation>> consumer)
    {
        var ev = getEventMap();
        if (ev != null)
            ev.forEach(consumer);
        FluidBuilder parent = getParent();
        if (parent != null)
        {
            parent.forEachEvent(consumer);
        }
    }

    public void register(IForgeRegistry<Fluid> registry)
    {
        get();
        factory.register(this, registry::register);
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
