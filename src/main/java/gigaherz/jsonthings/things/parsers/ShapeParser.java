package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.builders.ShapeBuilder;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ShapeParser extends ThingParser<ShapeBuilder>
{
    public ShapeParser()
    {
        super(GSON, "shape");
    }

    @Override
    public void finishLoading()
    {
        getBuilders().forEach(thing -> Registry.register(ThingRegistries.DYNAMIC_SHAPES, thing.getRegistryName(), thing.get()));
    }

    @Override
    protected ShapeBuilder processThing(ResourceLocation key, JsonObject data)
    {
        return ShapeBuilder.begin(key, DynamicShape.fromJson(data, null, name -> ThingRegistries.PROPERTIES.get(new ResourceLocation(name))));
    }
}
