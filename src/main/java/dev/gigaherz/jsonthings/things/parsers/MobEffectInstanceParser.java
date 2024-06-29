package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.MobEffectInstanceBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class MobEffectInstanceParser extends ThingParser<MobEffectInstanceBuilder>
{
    public MobEffectInstanceParser()
    {
        super(GSON, "mob_effect_instance");
    }

    @Override
    protected MobEffectInstanceBuilder processThing(ResourceLocation key, JsonObject data, Consumer<MobEffectInstanceBuilder> builderModification)
    {
        var builder = new MobEffectInstanceBuilder(this, key);
        JParse.begin(data)
                .key("effect", val -> val.string().handle(str -> builder.setEffect(ResourceLocation.parse(str))))
                .key("duration", val -> val.intValue().min(0).handle(builder::setDuration))
                .ifKey("amplifier", val -> val.intValue().min(0).handle(builder::setAmplifier))
                .ifKey("ambient", val -> val.bool().handle(builder::setAmbient))
                .ifKey("visible", val -> val.bool().handle(builder::setVisible))
                .ifKey("show_particles", val -> val.bool().handle(builder::setShowParticles))
                .ifKey("show_icon", val -> val.bool().handle(builder::setShowIcon));
        return builder;
    }
}
