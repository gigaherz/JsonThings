package dev.gigaherz.jsonthings.util.parse.function;

import dev.gigaherz.jsonthings.util.parse.value.MappedArrayValue;

import java.util.function.Function;

@FunctionalInterface
public interface MappedArrayValueFunction<V, T> extends Function<MappedArrayValue<V>, T>
{
}
