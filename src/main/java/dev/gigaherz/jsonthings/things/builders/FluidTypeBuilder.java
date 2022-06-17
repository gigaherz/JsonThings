package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class FluidTypeBuilder extends BaseBuilder<FluidType>
{
    private ResourceLocation parentBuilderName;
    private FluidTypeBuilder parentBuilder;
    private RegistryObject<Fluid> parentFluid;

    private ResourceLocation stillTexture;
    private ResourceLocation flowingTexture;
    private ResourceLocation sideTexture;
    private Rarity rarity = Rarity.COMMON;
    private Integer color;
    private Integer density;
    private Integer lightLevel;
    private Integer temperature;
    private Integer viscosity;
    private String translationKey;
    private Boolean isGaseous;
    private ResourceLocation fillSound;
    private ResourceLocation emptySound;
    private ResourceLocation vaporizeSound;

    private FluidTypeBuilder(ResourceLocation registryName)
    {
        super(registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Fluid";
    }

    public static FluidTypeBuilder begin(ResourceLocation registryName)
    {
        return new FluidTypeBuilder(registryName);
    }

    public void setParent(ResourceLocation parentName)
    {
        if (this.parentFluid != null)
            throw new IllegalStateException("Parent Fluid already set");
        this.parentBuilderName = parentName; // maybe
        this.parentFluid = RegistryObject.create(parentName, ForgeRegistries.FLUIDS);
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

    public void setLightLevel(Integer lightLevel)
    {
        this.lightLevel = lightLevel;
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

    public void setVaporizeSound(ResourceLocation vaporizeSound)
    {
        this.vaporizeSound = vaporizeSound;
    }

    @Override
    protected FluidType buildInternal()
    {
        FluidType.Properties props = FluidType.Properties.create();

        if (getLightLevel() != null) props.lightLevel(getLightLevel());
        if (getDensity() != null) props.density(getDensity());
        if (getTemperature() != null) props.temperature(getTemperature());
        if (getViscosity() != null) props.viscosity(getViscosity());
        if (getRarity() != null) props.rarity(getRarity());
        var fillSound = getFillSound() != null ? Utils.getOrCrash(ForgeRegistries.SOUND_EVENTS, getFillSound()) : null;
        if (fillSound != null) props.sound(SoundActions.BUCKET_FILL, fillSound);
        var emptySound = getEmptySound() != null ? Utils.getOrCrash(ForgeRegistries.SOUND_EVENTS, getEmptySound()) : null;
        if (emptySound != null) props.sound(SoundActions.BUCKET_EMPTY, emptySound);
        var vaporizeSound = getVaporizeSound() != null ? Utils.getOrCrash(ForgeRegistries.SOUND_EVENTS, getVaporizeSound()) : null;
        if (vaporizeSound != null) props.sound(SoundActions.FLUID_VAPORIZE, emptySound);
        if (getTranslationKey() != null) props.descriptionId(getTranslationKey());
        //if (getIsGaseous() != null && getIsGaseous()) props.gaseous();

        /*
        public FluidType.Properties motionScale(double motionScale)
        public FluidType.Properties canPushEntity(boolean canPushEntity)
        public FluidType.Properties canSwim(boolean canSwim)
        public FluidType.Properties canDrown(boolean canDrown)
        public FluidType.Properties fallDistanceModifier(float fallDistanceModifier)
        public FluidType.Properties canExtinguish(boolean canExtinguish)
        public FluidType.Properties canConvertToSource(boolean canConvertToSource)
        public FluidType.Properties supportsBoating(boolean supportsBoating)
        public FluidType.Properties pathType(@org.jetbrains.annotations.Nullable BlockPathTypes pathType)
        public FluidType.Properties adjacentPathType(@org.jetbrains.annotations.Nullable BlockPathTypes adjacentPathType)
        public FluidType.Properties canHydrate(boolean canHydrate)
         */

        final int color = getColor() != null ? getColor() : 0xFFFFFFFF;
        final ResourceLocation stillTexture = getStillTexture();
        final ResourceLocation flowingTexture = getFlowingTexture();
        final ResourceLocation sideTexture = getSideTexture();

        return new FluidType(props) {
            @Override
            public void initializeClient(Consumer<IFluidTypeRenderProperties> consumer)
            {
                consumer.accept(new IFluidTypeRenderProperties()
                {
                    @Override
                    public int getColorTint()
                    {
                        return color;
                    }

                    @Override
                    public ResourceLocation getStillTexture()
                    {
                        return stillTexture;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture()
                    {
                        return flowingTexture;
                    }

                    @Override
                    public @Nullable ResourceLocation getOverlayTexture()
                    {
                        return sideTexture;
                    }
                });
            }
        };
    }

    public FluidTypeBuilder requireParent()
    {
        if (parentBuilder == null)
        {
            if (parentBuilderName == null)
                throw new IllegalStateException("Parent not set");
            parentBuilder = JsonThings.fluidTypeParser.getBuildersMap().get(parentBuilderName);
            if (parentBuilder == null)
                throw new IllegalStateException("The specified parent " + parentBuilderName + " is not a Json Things defined Fluid");
        }
        return parentBuilder;
    }

    @Nullable
    public FluidTypeBuilder getParent()
    {
        if (parentBuilderName == null) return null;
        if (parentBuilder == null)
        {
            parentBuilder = JsonThings.fluidTypeParser.getBuildersMap().get(parentFluid.getId());
            if (parentBuilder == null)
            {
                parentBuilderName = null;
                return null;
            }
        }
        return parentBuilder;
    }

    @Nullable
    private <T> T getValueWithParent(@Nullable T thisValue, Function<FluidTypeBuilder, T> parentGetter)
    {
        if (thisValue != null) return thisValue;
        if (getParent() != null)
        {
            FluidTypeBuilder parent = requireParent();
            return parentGetter.apply(parent);
        }
        return null;
    }

    @Nullable
    public FluidTypeBuilder getParentBuilder()
    {
        return parentBuilder;
    }

    @Nullable
    public RegistryObject<Fluid> getParentFluid()
    {
        return parentFluid;
    }

    @Nullable
    private ResourceLocation getStillTexture()
    {
        return getValueWithParent(stillTexture, FluidTypeBuilder::getStillTexture);
    }

    @Nullable
    private ResourceLocation getFlowingTexture()
    {
        return getValueWithParent(flowingTexture, FluidTypeBuilder::getFlowingTexture);
    }

    @Nullable
    private Rarity getRarity()
    {
        return getValueWithParent(rarity, FluidTypeBuilder::getRarity);
    }

    @Nullable
    private ResourceLocation getSideTexture()
    {
        return getValueWithParent(sideTexture, FluidTypeBuilder::getSideTexture);
    }

    @Nullable
    private Integer getColor()
    {
        return getValueWithParent(color, FluidTypeBuilder::getColor);
    }

    @Nullable
    private Integer getDensity()
    {
        return getValueWithParent(density, FluidTypeBuilder::getDensity);
    }

    @Nullable
    private Integer getLightLevel()
    {
        return getValueWithParent(lightLevel, FluidTypeBuilder::getLightLevel);
    }

    @Nullable
    private Integer getTemperature()
    {
        return getValueWithParent(temperature, FluidTypeBuilder::getTemperature);
    }

    @Nullable
    private Integer getViscosity()
    {
        return getValueWithParent(viscosity, FluidTypeBuilder::getViscosity);
    }

    @Nullable
    private String getTranslationKey()
    {
        return getValueWithParent(translationKey, FluidTypeBuilder::getTranslationKey);
    }

    @Nullable
    private Boolean getIsGaseous()
    {
        return getValueWithParent(isGaseous, FluidTypeBuilder::getIsGaseous);
    }

    @Nullable
    private ResourceLocation getFillSound()
    {
        return getValueWithParent(fillSound, FluidTypeBuilder::getFillSound);
    }

    @Nullable
    private ResourceLocation getEmptySound()
    {
        return getValueWithParent(emptySound, FluidTypeBuilder::getEmptySound);
    }

    @Nullable
    private ResourceLocation getVaporizeSound()
    {
        return getValueWithParent(vaporizeSound, FluidTypeBuilder::getVaporizeSound);
    }
}
