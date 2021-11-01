package dev.gigaherz.jsonthings.util.parse.value;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public interface IntValue
{
    void handle(IntConsumer value);

    IntValue min(int min);

    IntValue range(int min, int maxExclusive);

    int getAsInt();

    default <T> MappedValue<T> map(IntFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsInt()));
    }
}
