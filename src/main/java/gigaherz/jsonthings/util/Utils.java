package gigaherz.jsonthings.util;

import net.minecraft.state.Property;

import java.util.Optional;

public class Utils
{
    public static <T extends Comparable<T>> T getPropertyValue(Property<T> prop, String value)
    {
        Optional<T> propValue = prop.parseValue(value);
        return propValue.orElseThrow(() -> new IllegalStateException("Value " + value + " for property " + prop.getName() + " not found in the allowed values."));
    }

}
