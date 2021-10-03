package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.MobEffectInstanceBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.ObjValue;
import dev.gigaherz.jsonthings.things.builders.FoodBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableFloat;

public class FoodParser extends ThingParser<FoodBuilder>
{
    public FoodParser()
    {
        super(GSON, "food");
    }

    @Override
    public void finishLoading()
    {
        getBuilders().forEach(thing -> Registry.register(ThingRegistries.FOODS, thing.getRegistryName(), thing.get()));
    }

    @Override
    public FoodBuilder processThing(ResourceLocation key, JsonObject data)
    {
        final FoodBuilder builder = FoodBuilder.begin(key);

        JParse.begin(data)
                .obj()
                .key("nutrition", val -> val.intValue().min(1).handle(builder::setNutrition))
                .key("saturation", val -> val.intValue().min(0).handle(builder::setSaturation))
                .ifKey("meat", val -> val.bool().handle(builder::setIsMeat))
                .ifKey("fast", val -> val.bool().handle(builder::setFast))
                .ifKey("always_eat", val -> val.bool().handle(builder::setAlwaysEat))
                .ifKey("effects", val -> val.array().forEach((i, entry) -> {
                    var probability = new MutableFloat(1.0f);
                    var ei = parseEffectInstance(entry.obj()
                            .ifKey("probability", v3 -> v3.floatValue().range(0, 1).handle(probability::setValue)));
                    builder.effect(ei, probability.getValue());
                }));

        return builder;
    }

    private MobEffectInstanceBuilder parseEffectInstance(ObjValue obj)
    {
        var builder = new MobEffectInstanceBuilder();
        obj
                .key("effect", val -> val.string().handle(str -> builder.setEffect(new ResourceLocation(str))))
                .key("duration", val -> val.intValue().min(0).handle(builder::setDuration))
                .key("amplifier", val -> val.intValue().min(0).handle(builder::setAmplifier))
                .key("ambient", val -> val.bool().handle(builder::setAmbient))
                .key("visible", val -> val.bool().handle(builder::setVisible))
                .key("show_particles", val -> val.bool().handle(builder::setShowParticles))
                .key("show_icon", val -> val.bool().handle(builder::setShowIcon));
        return builder;
    }
}
