package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.util.ResourceLocation;

public class ShapeBuilder extends BaseBuilder<DynamicShape>
{
    private final DynamicShape dynamicShape;

    private ShapeBuilder(ResourceLocation registryName, DynamicShape dynamicShape)
    {
        super(registryName);
        this.dynamicShape = dynamicShape;
    }

    public static ShapeBuilder begin(ResourceLocation registryName, DynamicShape dynamicShape)
    {
        return new ShapeBuilder(registryName, dynamicShape);
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
