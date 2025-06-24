package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilder extends BaseBuilder<SoundEvent, SoundEventBuilder>
{
    public static SoundEventBuilder begin(ThingParser<SoundEvent, SoundEventBuilder> ownerParser, ResourceLocation registryName)
    {
        return new SoundEventBuilder(ownerParser, registryName);
    }

    private Float range;

    private SoundEventBuilder(ThingParser<SoundEvent, SoundEventBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    public void setRange(Float range)
    {
        this.range = range;
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Sound Event";
    }

    @Override
    protected SoundEvent buildInternal()
    {
        return range != null
                ? SoundEvent.createFixedRangeEvent(getRegistryName(), range)
                : SoundEvent.createVariableRangeEvent(getRegistryName());
    }

    @Override
    public void validate()
    {
    }
}
