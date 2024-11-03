package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.FoodPropertiesBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

import java.util.function.Consumer;

public class FoodPropertiesParser extends ThingParser<FoodProperties, FoodPropertiesBuilder>
{
    public FoodPropertiesParser()
    {
        super(GSON, "food");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(ThingRegistries.FOOD, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }

    @Override
    public FoodPropertiesBuilder processThing(ResourceLocation key, JsonObject data, Consumer<FoodPropertiesBuilder> builderModification)
    {
        final FoodPropertiesBuilder builder = FoodPropertiesBuilder.begin(this, key);

        JParse.begin(data)
                .key("nutrition", val -> val.intValue().min(1).handle(builder::setNutrition))
                .key("saturation", val -> val.floatValue().min(0).handle(builder::setSaturation))
                .ifKey("always_eat", val -> val.bool().handle(builder::setAlwaysEat))
        ;

        builderModification.accept(builder);

        return builder;
    }
}
