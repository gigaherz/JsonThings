package dev.gigaherz.jsonthings.util.parse.value;

import dev.gigaherz.jsonthings.util.parse.function.IntObjBiConsumer;
import dev.gigaherz.jsonthings.util.parse.function.MappedArrayValueFunction;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public interface MappedArrayValue<V>
{
    void forEach(IntObjBiConsumer<V> visitor);

    void collect(Consumer<Stream<V>> collector);

    <T> MappedArrayValue<T> map(Function<V, T> mapping);

    <T> T flatMap(Function<Stream<V>, T> collector);

    MappedArrayValue<V> notEmpty();

    MappedArrayValue<V> atLeast(int min);

    MappedArrayValue<V> between(int min, int maxExclusive);

    <T> MappedValue<T[]> flatten(Function<V, T> mapping, IntFunction<T[]> factory);

    default MappedValue<V[]> flatten(IntFunction<V[]> factory)
    {
        return flatten(Function.identity(), factory);
    }

    default <T> MappedValue<T> mapWhole(MappedArrayValueFunction<V, T> mapping)
    {
        return MappedValue.of(mapping.apply(this));
    }
}
