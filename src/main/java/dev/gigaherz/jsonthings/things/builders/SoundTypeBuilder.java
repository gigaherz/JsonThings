package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SoundTypeBuilder extends BaseBuilder<SoundType, SoundTypeBuilder>
{
    public static SoundTypeBuilder begin(ThingParser<SoundTypeBuilder> ownerParser, ResourceLocation registryName)
    {
        return new SoundTypeBuilder(ownerParser, registryName);
    }

    private float volume = 1.0f;
    private float pitch = 1.0f;
    private ResourceLocation breakSound;
    private ResourceLocation stepSound;
    private ResourceLocation placeSound;
    private ResourceLocation hitSound;
    private ResourceLocation fallSound;

    private SoundTypeBuilder(ThingParser<SoundTypeBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    public void setVolume(float volume)
    {
        this.volume = volume;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public void setBreakSound(ResourceLocation resourceLocation)
    {
        breakSound = resourceLocation;
    }

    public void setStepSound(ResourceLocation resourceLocation)
    {
        stepSound = resourceLocation;
    }

    public void setHitSound(ResourceLocation resourceLocation)
    {
        hitSound = resourceLocation;
    }

    public void setFallSound(ResourceLocation resourceLocation)
    {
        fallSound = resourceLocation;
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Sound Type";
    }

    @Override
    protected SoundType buildInternal()
    {
        var breakSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, breakSound);
        var stepSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, stepSound);
        var placeSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, placeSound);
        var hitSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, hitSound);
        var fallSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, fallSound);
        return new DeferredSoundType(volume, pitch, breakSoundEvent, stepSoundEvent, placeSoundEvent, hitSoundEvent, fallSoundEvent);
    }
}
