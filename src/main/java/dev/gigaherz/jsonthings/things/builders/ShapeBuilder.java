package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class ShapeBuilder implements Supplier<DynamicShape>
{
    private final ResourceLocation registryName;
    private final DynamicShape dynamicShape;

    private ShapeBuilder(ResourceLocation registryName, DynamicShape dynamicShape)
    {
        this.registryName = registryName;
        this.dynamicShape = dynamicShape;
    }

    public static ShapeBuilder begin(ResourceLocation registryName, DynamicShape dynamicShape)
    {
        return new ShapeBuilder(registryName, dynamicShape);
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public DynamicShape get()
    {
        return dynamicShape;
    }
}
