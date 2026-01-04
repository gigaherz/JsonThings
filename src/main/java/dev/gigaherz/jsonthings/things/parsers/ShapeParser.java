package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.ShapeBuilder;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class ShapeParser extends ThingParser<DynamicShape, ShapeBuilder>
{
    public ShapeParser()
    {
        super(GSON, "shape");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(ThingRegistries.DYNAMIC_SHAPE, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }

    @Override
    protected ShapeBuilder processThing(Identifier key, JsonObject data, Consumer<ShapeBuilder> builderModification)
    {
        ShapeBuilder builder = ShapeBuilder.begin(this, key, DynamicShape.fromJson(data, null,
                name -> ThingRegistries.PROPERTY.getOptional(Identifier.parse(name)).orElseThrow()));

        builderModification.accept(builder);

        return builder;
    }
}
