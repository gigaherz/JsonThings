package dev.gigaherz.jsonthings.things.properties;

import dev.gigaherz.jsonthings.things.ThingRegistries;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.registry.Registry;

public class PropertyTypes
{
    public static final PropertyType BOOLEAN_PROPERTY = register("boolean", new PropertyType.BoolType());
    public static final PropertyType INTEGER_PROPERTY = register("int", new PropertyType.RangeType<>(IntegerProperty.class, IntegerProperty::create, js -> js.getAsJsonPrimitive().getAsInt()));
    public static final PropertyType STRING_PROPERTY = register("string", new PropertyType.StringType());
    public static final PropertyType DIRECTION_PROPERTY = register("direction", new PropertyType.DirectionType());
    public static final PropertyType ENUM_PROPERTY = register("enum", new PropertyType.EnumType());

    public static PropertyType register(String name, PropertyType propertyType)
    {
        return Registry.register(ThingRegistries.PROPERTY_TYPES, name, propertyType);
    }

    public static void init()
    {
    }
}
