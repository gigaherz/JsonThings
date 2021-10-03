package dev.gigaherz.jsonthings.util.parse.value;

import dev.gigaherz.jsonthings.util.parse.function.BooleanFunction;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public interface BooleanValue
{
    void handle(BooleanConsumer value);

    boolean getAsBoolean();

    default <T> MappedValue<T> map(BooleanFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsBoolean()));
    }
}
