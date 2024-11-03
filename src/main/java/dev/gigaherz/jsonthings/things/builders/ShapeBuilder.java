package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.resources.ResourceLocation;

public class ShapeBuilder extends BaseBuilder<DynamicShape, ShapeBuilder>
{
    public static ShapeBuilder begin(ThingParser<DynamicShape, ShapeBuilder> ownerParser, ResourceLocation registryName, DynamicShape dynamicShape)
    {
        return new ShapeBuilder(ownerParser, registryName, dynamicShape);
    }

    private final DynamicShape dynamicShape;

    private ShapeBuilder(ThingParser<DynamicShape, ShapeBuilder> ownerParser, ResourceLocation registryName, DynamicShape dynamicShape)
    {
        super(ownerParser, registryName);
        this.dynamicShape = dynamicShape;
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Voxel Shape";
    }

    @Override
    protected DynamicShape buildInternal()
    {
        return dynamicShape;
    }
}
