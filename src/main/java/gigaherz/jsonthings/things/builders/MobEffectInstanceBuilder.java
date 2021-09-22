package gigaherz.jsonthings.things.builders;

import gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class MobEffectInstanceBuilder
{
    private ResourceLocation effect;
    private int duration;
    private int amplifier;
    private boolean isAmbient;
    private boolean visible;
    private boolean showParticles;
    private boolean showIcon;

    public ResourceLocation getEffect()
    {
        return effect;
    }

    public void setEffect(ResourceLocation effect)
    {
        this.effect = effect;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public int getAmplifier()
    {
        return amplifier;
    }

    public void setAmplifier(int amplifier)
    {
        this.amplifier = amplifier;
    }

    public boolean isAmbient()
    {
        return isAmbient;
    }

    public void setAmbient(boolean ambient)
    {
        isAmbient = ambient;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isShowParticles()
    {
        return showParticles;
    }

    public void setShowParticles(boolean showParticles)
    {
        this.showParticles = showParticles;
    }

    public boolean isShowIcon()
    {
        return showIcon;
    }

    public void setShowIcon(boolean showIcon)
    {
        this.showIcon = showIcon;
    }

    public MobEffectInstance build()
    {
        return new MobEffectInstance(Utils.getOrCrash(ForgeRegistries.MOB_EFFECTS, effect), duration, amplifier, isAmbient, showParticles, showIcon);
    }
}
