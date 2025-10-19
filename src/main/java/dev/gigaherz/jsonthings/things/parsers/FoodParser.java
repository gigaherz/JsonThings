package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.FoodBuilder;
import dev.gigaherz.jsonthings.things.builders.MobEffectInstanceBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.ObjValue;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FoodParser extends ThingParser<FoodBuilder>
{
    public FoodParser()
    {
        super(GSON, "food");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(ThingRegistries.FOODS, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }

    @Override
    public FoodBuilder processThing(ResourceLocation key, JsonObject data, Consumer<FoodBuilder> builderModification)
    {
        final FoodBuilder builder = FoodBuilder.begin(this, key);

        JParse.begin(data)
                .key("nutrition", val -> val.intValue().min(1).handle(builder::setNutrition))
                .key("saturation", val -> val.floatValue().min(0).handle(builder::setSaturation))
                .ifKey("fast", val -> val.bool().handle(builder::setFast))
                .ifKey("always_eat", val -> val.bool().handle(builder::setAlwaysEat))
                .ifKey("convert_to", val -> val.string().handle(builder::setConvertTo))
                .ifKey("effects", val -> val.array().forEach((i, entry) -> {
                    var probability = new MutableFloat(1.0f);
                    var effectBuilder = parseEffectInstance(entry.obj()
                            .ifKey("probability", v3 -> v3.floatValue().range(0, 1).handle(probability::setValue)), builder);
                    if (effectBuilder != null)
                        builder.effect(effectBuilder, probability.getValue());
                }));

        builderModification.accept(builder);

        return builder;
    }

    @Nullable
    private MobEffectInstanceBuilder parseEffectInstance(ObjValue obj, FoodBuilder parentBuilder)
    {
        try
        {
            var builder = JsonThings.mobEffectInstanceParser.parseFromElement(parentBuilder.getRegistryName(), obj.getAsJsonObject());
            if (builder != null)
                builder.setOwner(parentBuilder);
            return builder;
        }
        catch (Exception e)
        {
            throw new ThingParseException("Exception while parsing nested block in " + parentBuilder.getRegistryName(), e);
        }
    }
}
