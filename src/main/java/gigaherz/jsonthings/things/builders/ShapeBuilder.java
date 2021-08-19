package gigaherz.jsonthings.things.builders;

import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("ClassCanBeRecord")
public class ShapeBuilder
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

    public DynamicShape build()
    {
        return dynamicShape;
    }
}
