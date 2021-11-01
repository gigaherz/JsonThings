package dev.gigaherz.jsonthings.util.parse.function;

import dev.gigaherz.jsonthings.util.parse.value.Any;

import java.util.function.Function;

@FunctionalInterface
public interface AnyFunction<T> extends Function<Any, T>
{
}
