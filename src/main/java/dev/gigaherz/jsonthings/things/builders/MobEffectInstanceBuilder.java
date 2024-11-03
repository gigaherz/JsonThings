package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class MobEffectInstanceBuilder extends BaseBuilder<MobEffectInstance, MobEffectInstanceBuilder>
{
    private ResourceLocation effect;
    private int duration;
    private int amplifier = 0;
    private boolean isAmbient = false;
    private boolean showParticles = true;
    private boolean showIcon = true;
    private FoodPropertiesBuilder owner;

    public MobEffectInstanceBuilder(ThingParser<MobEffectInstance, MobEffectInstanceBuilder> ownerParser, ResourceLocation name)
    {
        super(ownerParser, name);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Mob Effect Instance";
    }

    @Override
    protected CrashReportCategory fillReport(CrashReport crashReport)
    {
        CrashReportCategory reportCategory = super.fillReport(crashReport);
        reportCategory.setDetail("Contained in", owner.getThingTypeDisplayName());
        return reportCategory;
    }

    public void setOwner(FoodPropertiesBuilder owner)
    {
        this.owner = owner;
    }

    public FoodPropertiesBuilder getOwner()
    {
        return owner;
    }

    public ResourceLocation getEffect()
    {
        return effect;
    }

    public void setEffect(ResourceLocation effect)
    {
        this.effect = effect;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public void setAmplifier(int amplifier)
    {
        this.amplifier = amplifier;
    }

    public void setAmbient(boolean ambient)
    {
        isAmbient = ambient;
    }

    public void setShowParticles(boolean showParticles)
    {
        this.showParticles = showParticles;
    }

    public void setShowIcon(boolean showIcon)
    {
        this.showIcon = showIcon;
    }

    public void setVisible(boolean visible)
    {
        this.showParticles = this.showIcon = visible;
    }

    @Override
    protected MobEffectInstance buildInternal()
    {
        return new MobEffectInstance(Utils.getHolderOrCrash(BuiltInRegistries.MOB_EFFECT, effect), duration, amplifier, isAmbient, showParticles, showIcon);
    }
}
