package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FluidTypeBuilder extends BaseBuilder<FluidType, FluidTypeBuilder>
{
    public static FluidTypeBuilder begin(ThingParser<FluidTypeBuilder> ownerParser, ResourceLocation registryName)
    {
        return new FluidTypeBuilder(ownerParser, registryName);
    }

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
    private Double motionScale;
    private Float fallDistanceModifier;
    private Boolean canPushEntity;
    private Boolean canSwim;
    private Boolean canDrown;
    private Boolean canExtinguish;
    private Boolean canHydrate;
    private Boolean canConvertToSource;
    private Boolean supportsBoating;


    private FluidTypeBuilder(ThingParser<FluidTypeBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Fluid";
    }

    public void setStillTexture(ResourceLocation stillTexture)
    {
        this.stillTexture = stillTexture;
    }

    @Nullable
    private ResourceLocation getStillTexture()
    {
        return getValueWithParent(stillTexture, FluidTypeBuilder::getStillTexture);
    }

    public void setFlowingTexture(ResourceLocation flowingTexture)
    {
        this.flowingTexture = flowingTexture;
    }

    @Nullable
    private ResourceLocation getFlowingTexture()
    {
        return getValueWithParent(flowingTexture, FluidTypeBuilder::getFlowingTexture);
    }

    public void setSideTexture(ResourceLocation overlay)
    {
        this.sideTexture = overlay;
    }

    @Nullable
    private ResourceLocation getSideTexture()
    {
        return getValueWithParent(sideTexture, FluidTypeBuilder::getSideTexture);
    }

    public void setRarity(Rarity rarity)
    {
        this.rarity = rarity;
    }

    @Nullable
    private Rarity getRarity()
    {
        return getValueWithParent(rarity, FluidTypeBuilder::getRarity);
    }

    public void setColor(Integer color)
    {
        this.color = color;
    }

    @Nullable
    private Integer getColor()
    {
        return getValueWithParent(color, FluidTypeBuilder::getColor);
    }

    public void setDensity(Integer density)
    {
        this.density = density;
    }

    @Nullable
    private Integer getDensity()
    {
        return getValueWithParent(density, FluidTypeBuilder::getDensity);
    }

    public void setLightLevel(Integer lightLevel)
    {
        this.lightLevel = lightLevel;
    }

    @Nullable
    private Integer getLightLevel()
    {
        return getValueWithParent(lightLevel, FluidTypeBuilder::getLightLevel);
    }

    public void setTemperature(Integer temperature)
    {
        this.temperature = temperature;
    }

    @Nullable
    private Integer getTemperature()
    {
        return getValueWithParent(temperature, FluidTypeBuilder::getTemperature);
    }

    public void setViscosity(Integer viscosity)
    {
        this.viscosity = viscosity;
    }

    @Nullable
    private Integer getViscosity()
    {
        return getValueWithParent(viscosity, FluidTypeBuilder::getViscosity);
    }

    public void setTranslationKey(String translationKey)
    {
        this.translationKey = translationKey;
    }

    @Nullable
    private String getTranslationKey()
    {
        return getValueWithParent(translationKey, FluidTypeBuilder::getTranslationKey);
    }

    public void setGaseous(Boolean gaseous)
    {
        isGaseous = gaseous;
    }

    @Nullable
    private Boolean getIsGaseous()
    {
        return getValueWithParent(isGaseous, FluidTypeBuilder::getIsGaseous);
    }

    public void setFillSound(ResourceLocation fillSound)
    {
        this.fillSound = fillSound;
    }

    @Nullable
    private ResourceLocation getFillSound()
    {
        return getValueWithParent(fillSound, FluidTypeBuilder::getFillSound);
    }

    public void setEmptySound(ResourceLocation emptySound)
    {
        this.emptySound = emptySound;
    }

    @Nullable
    private ResourceLocation getEmptySound()
    {
        return getValueWithParent(emptySound, FluidTypeBuilder::getEmptySound);
    }

    public void setVaporizeSound(ResourceLocation vaporizeSound)
    {
        this.vaporizeSound = vaporizeSound;
    }

    @Nullable
    private ResourceLocation getVaporizeSound()
    {
        return getValueWithParent(vaporizeSound, FluidTypeBuilder::getVaporizeSound);
    }

    public void setMotionScale(double motionScale)
    {
        this.motionScale = motionScale;
    }

    @Nullable
    public Double getMotionScale()
    {
        return getValueWithParent(motionScale, FluidTypeBuilder::getMotionScale);
    }

    public void setFallDistanceModifier(float fallDistanceModifier)
    {
        this.fallDistanceModifier = fallDistanceModifier;
    }

    @Nullable
    public Float getFallDistanceModifier()
    {
        return getValueWithParent(fallDistanceModifier, FluidTypeBuilder::getFallDistanceModifier);
    }

    public void setCanPushEntity(boolean canPushEntity)
    {
        this.canPushEntity = canPushEntity;
    }

    @Nullable
    public Boolean getCanPushEntity()
    {
        return getValueWithParent(canPushEntity, FluidTypeBuilder::getCanPushEntity);
    }

    public void setCanSwim(boolean canSwim)
    {
        this.canSwim = canSwim;
    }

    @Nullable
    public Boolean getCanSwim()
    {
        return getValueWithParent(canSwim, FluidTypeBuilder::getCanSwim);
    }

    public void setCanDrown(boolean canDrown)
    {
        this.canDrown = canDrown;
    }

    @Nullable
    public Boolean getCanDrown()
    {
        return getValueWithParent(canDrown, FluidTypeBuilder::getCanDrown);
    }

    public void setCanExtinguish(boolean canExtinguish)
    {
        this.canExtinguish = canExtinguish;
    }

    @Nullable
    public Boolean getCanExtinguish()
    {
        return getValueWithParent(canExtinguish, FluidTypeBuilder::getCanExtinguish);
    }

    public void setCanHydrate(boolean canHydrate)
    {
        this.canHydrate = canHydrate;
    }

    @Nullable
    public Boolean getCanHydrate()
    {
        return getValueWithParent(canHydrate, FluidTypeBuilder::getCanHydrate);
    }

    public void setCanConvertToSource(boolean canConvertToSource)
    {
        this.canConvertToSource = canConvertToSource;
    }

    @Nullable
    public Boolean getCanConvertToSource()
    {
        return getValueWithParent(canConvertToSource, FluidTypeBuilder::getCanConvertToSource);
    }

    public void setSupportsBoating(boolean supportsBoating)
    {
        this.supportsBoating = supportsBoating;
    }

    @Nullable
    public Boolean getSupportsBoating()
    {
        return getValueWithParent(supportsBoating, FluidTypeBuilder::getSupportsBoating);
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
        if (getMotionScale() != null) props.motionScale(getMotionScale());
        if (getFallDistanceModifier() != null) props.fallDistanceModifier(getFallDistanceModifier());
        if (getCanPushEntity() != null) props.canPushEntity(getCanPushEntity());
        if (getCanSwim() != null) props.canSwim(getCanSwim());
        if (getCanDrown() != null) props.canDrown(getCanDrown());
        if (getCanExtinguish() != null) props.canExtinguish(getCanExtinguish());
        if (getCanConvertToSource() != null) props.canConvertToSource(getCanConvertToSource());
        if (getCanHydrate() != null) props.canHydrate(getCanHydrate());
        if (getSupportsBoating() != null) props.supportsBoating(getSupportsBoating());
        //if (getPathType() != null) props.pathType(@org.jetbrains.annotations.Nullable BlockPathTypes pathType)
        //if (getAdjacentPathType() != null) props.adjacentPathType(@org.jetbrains.annotations.Nullable BlockPathTypes adjacentPathType)

        final int color = getColor() != null ? getColor() : 0xFFFFFFFF;
        final ResourceLocation stillTexture = getStillTexture();
        final ResourceLocation flowingTexture = getFlowingTexture();
        final ResourceLocation sideTexture = getSideTexture();

        if (stillTexture == null)
            throw new IllegalStateException("FluidType requires a still_texture value");
        if (flowingTexture == null)
            throw new IllegalStateException("FluidType requires a flowing_texture value");

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

}
