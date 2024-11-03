package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.SoundEventBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class SoundEventParser extends ThingParser<SoundEvent, SoundEventBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public SoundEventParser(IEventBus bus)
    {
        super(GSON, "sound_event");

        register(bus, Registries.SOUND_EVENT);
    }

    @Override
    public SoundEventBuilder processThing(ResourceLocation key, JsonObject data, Consumer<SoundEventBuilder> builderModification)
    {
        final SoundEventBuilder builder = SoundEventBuilder.begin(this, key);

        JParse.begin(data)
                .ifKey("range", val -> val.floatValue().handle(builder::setRange))
        ;

        builderModification.accept(builder);

        return builder;
    }
}
