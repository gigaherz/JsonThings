package dev.gigaherz.jsonthings.util.parse.function;

import dev.gigaherz.jsonthings.util.parse.value.ObjValue;

import java.util.function.Function;

@FunctionalInterface
public interface ObjValueFunction<T> extends Function<ObjValue, T>
{
}
