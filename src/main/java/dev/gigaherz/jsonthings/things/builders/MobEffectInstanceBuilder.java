package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MobEffectInstanceBuilder implements Supplier<EffectInstance>
{
    private ResourceLocation effect;
    private int duration;
    private int amplifier;
    private boolean isAmbient;
    private boolean visible;
    private boolean showParticles;
    private boolean showIcon;
    private EffectInstance builtEffectInstance;

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

    private EffectInstance build()
    {
        return builtEffectInstance = new EffectInstance(Utils.getOrCrash(ForgeRegistries.POTIONS, effect), duration, amplifier, isAmbient, showParticles, showIcon);
    }

    public EffectInstance get()
    {
        if (builtEffectInstance == null)
            return build();
        return builtEffectInstance;
    }
}
