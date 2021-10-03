package dev.gigaherz.jsonthings.util.parse.value;

import com.google.gson.JsonArray;
import dev.gigaherz.jsonthings.util.parse.function.ArrayValueFunction;
import dev.gigaherz.jsonthings.util.parse.function.IntObjBiConsumer;
import dev.gigaherz.jsonthings.util.parse.function.JsonArrayConsumer;
import dev.gigaherz.jsonthings.util.parse.function.JsonArrayFunction;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ArrayValue
{
    void forEach(IntObjBiConsumer<Any> visitor);

    void collect(Consumer<Stream<Any>> collector);

    <T> T flatMap(Function<Stream<Any>, T> collector);

    ArrayValue notEmpty();

    ArrayValue atLeast(int min);

    ArrayValue between(int min, int maxExclusive);

    JsonArray getAsJsonArray();

    void raw(JsonArrayConsumer value);

    default <T> MappedValue<T> map(JsonArrayFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsJsonArray()));
    }

    default <T> MappedValue<T> map(ArrayValueFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(this));
    }
}
