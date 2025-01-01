package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.SoundTypeBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class SoundTypeParser extends ThingParser<SoundTypeBuilder>
{
    public SoundTypeParser()
    {
        super(GSON, "sound_type");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(ThingRegistries.SOUND_TYPES, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }

    @Override
    public SoundTypeBuilder processThing(ResourceLocation key, JsonObject data, Consumer<SoundTypeBuilder> builderModification)
    {
        final SoundTypeBuilder builder = SoundTypeBuilder.begin(this, key);

        JParse.begin(data)
                .ifKey("volume", val -> val.floatValue().range(0, 1).handle(builder::setVolume))
                .ifKey("pitch", val -> val.floatValue().min(0).handle(builder::setPitch))
                .key("break_sound", val -> val.string().map(ResourceLocation::new).handle(builder::setBreakSound))
                .key("step_sound", val -> val.string().map(ResourceLocation::new).handle(builder::setStepSound))
                .key("hit_sound", val -> val.string().map(ResourceLocation::new).handle(builder::setHitSound))
                .key("fall_sound", val -> val.string().map(ResourceLocation::new).handle(builder::setFallSound))
        ;

        builderModification.accept(builder);

        return builder;
    }
}
