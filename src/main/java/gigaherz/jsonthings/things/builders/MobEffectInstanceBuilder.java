package gigaherz.jsonthings.things.builders;

import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MobEffectInstanceBuilder implements Supplier<MobEffectInstance>
{
    private ResourceLocation effect;
    private int duration;
    private int amplifier;
    private boolean isAmbient;
    private boolean visible;
    private boolean showParticles;
    private boolean showIcon;
    private MobEffectInstance builtEffectInstance;

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

    private MobEffectInstance build()
    {
        return builtEffectInstance = new MobEffectInstance(Utils.getOrCrash(ForgeRegistries.MOB_EFFECTS, effect), duration, amplifier, isAmbient, showParticles, showIcon);
    }

    public MobEffectInstance get()
    {
        if (builtEffectInstance == null)
            return build();
        return builtEffectInstance;
    }
}
