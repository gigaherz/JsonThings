package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SoundTypeBuilder extends BaseBuilder<SoundType, SoundTypeBuilder>
{
    public static SoundTypeBuilder begin(ThingParser<SoundType, SoundTypeBuilder> ownerParser, Identifier registryName)
    {
        return new SoundTypeBuilder(ownerParser, registryName);
    }

    private float volume = 1.0f;
    private float pitch = 1.0f;
    private Identifier breakSound;
    private Identifier stepSound;
    private Identifier placeSound;
    private Identifier hitSound;
    private Identifier fallSound;

    private SoundTypeBuilder(ThingParser<SoundType, SoundTypeBuilder> ownerParser, Identifier registryName)
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

    public void setBreakSound(Identifier resourceLocation)
    {
        breakSound = resourceLocation;
    }

    public void setStepSound(Identifier resourceLocation)
    {
        stepSound = resourceLocation;
    }

    public void setHitSound(Identifier resourceLocation)
    {
        hitSound = resourceLocation;
    }

    public void setFallSound(Identifier resourceLocation)
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
        breakSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, breakSound);
        stepSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, stepSound);
        placeSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, placeSound);
        hitSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, hitSound);
        fallSoundEvent = DeferredHolder.create(Registries.SOUND_EVENT, fallSound);
        return new DeferredSoundType(volume, pitch, breakSoundEvent, stepSoundEvent, placeSoundEvent, hitSoundEvent, fallSoundEvent);
    }

    private DeferredHolder<SoundEvent, SoundEvent> breakSoundEvent;
    private DeferredHolder<SoundEvent, SoundEvent> stepSoundEvent;
    private DeferredHolder<SoundEvent, SoundEvent> placeSoundEvent;
    private DeferredHolder<SoundEvent, SoundEvent> hitSoundEvent;
    private DeferredHolder<SoundEvent, SoundEvent> fallSoundEvent;

    @Override
    public void validate()
    {
        if (breakSoundEvent != null) breakSoundEvent.value();
        if (stepSoundEvent != null) stepSoundEvent.value();
        if (placeSoundEvent != null) placeSoundEvent.value();
        if (hitSoundEvent != null) hitSoundEvent.value();
        if (fallSoundEvent != null) fallSoundEvent.value();
    }
}
