package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class MobEffectInstanceBuilder extends BaseBuilder<MobEffectInstance, MobEffectInstanceBuilder>
{
    private ResourceLocation effect;
    private int duration;
    private int amplifier;
    private boolean isAmbient;
    private boolean visible;
    private boolean showParticles;
    private boolean showIcon;
    private FoodBuilder owner;

    public MobEffectInstanceBuilder(ThingParser<MobEffectInstanceBuilder> ownerParser, ResourceLocation name)
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

    public void setOwner(FoodBuilder owner)
    {
        this.owner = owner;
    }

    public FoodBuilder getOwner()
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

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public void setShowParticles(boolean showParticles)
    {
        this.showParticles = showParticles;
    }

    public void setShowIcon(boolean showIcon)
    {
        this.showIcon = showIcon;
    }

    @Override
    protected MobEffectInstance buildInternal()
    {
        return new MobEffectInstance(Utils.getOrCrash(ForgeRegistries.MOB_EFFECTS, effect), duration, amplifier, isAmbient, showParticles, showIcon);
    }
}
