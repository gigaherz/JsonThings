package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
        RegistryObject<SoundEvent> breakSoundEvent = RegistryObject.create(breakSound, ForgeRegistries.SOUND_EVENTS);
        RegistryObject<SoundEvent> stepSoundEvent = RegistryObject.create(stepSound, ForgeRegistries.SOUND_EVENTS);
        RegistryObject<SoundEvent> placeSoundEvent = RegistryObject.create(placeSound, ForgeRegistries.SOUND_EVENTS);
        RegistryObject<SoundEvent> hitSoundEvent = RegistryObject.create(hitSound, ForgeRegistries.SOUND_EVENTS);
        RegistryObject<SoundEvent> fallSoundEvent = RegistryObject.create(fallSound, ForgeRegistries.SOUND_EVENTS);
        return new ForgeSoundType(volume, pitch, breakSoundEvent, stepSoundEvent, placeSoundEvent, hitSoundEvent, fallSoundEvent);
    }
}
