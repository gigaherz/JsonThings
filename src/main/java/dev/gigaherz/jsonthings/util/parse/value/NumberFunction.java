package dev.gigaherz.jsonthings.util.parse.value;

import java.util.function.Function;

@FunctionalInterface
public interface NumberFunction<T> extends Function<Number, T>
{
}
