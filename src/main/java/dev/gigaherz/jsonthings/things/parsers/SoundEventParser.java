package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.SoundEventBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Consumer;

public class SoundEventParser extends ThingParser<SoundEventBuilder>
{
    public SoundEventParser(IEventBus bus)
    {
        super(GSON, "sound_event");

        bus.addListener(this::register);
    }

    public void register(RegisterEvent event)
    {
        event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> {
            LOGGER.info("Started registering SoundEvent things, errors about unexpected registry domains are harmless...");
            processAndConsumeErrors(getThingType(), getBuilders(), thing -> helper.register(thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
            LOGGER.info("Done processing thingpack SoundEvents.");
        });
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
