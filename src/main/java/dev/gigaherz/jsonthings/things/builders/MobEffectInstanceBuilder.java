package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class MobEffectInstanceBuilder extends BaseBuilder<EffectInstance>
{
    private final BaseBuilder<?> owner;
    private ResourceLocation effect;
    private int duration;
    private int amplifier;
    private boolean isAmbient;
    private boolean visible;
    private boolean showParticles;
    private boolean showIcon;

    public MobEffectInstanceBuilder(BaseBuilder<?> owner)
    {
        super(owner.getRegistryName());
        this.owner = owner;
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
    protected EffectInstance buildInternal()
    {
        return new EffectInstance(Utils.getOrCrash(ForgeRegistries.POTIONS, effect), duration, amplifier, isAmbient, showParticles, showIcon);
    }
}
