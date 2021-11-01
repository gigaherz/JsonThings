package dev.gigaherz.jsonthings.util.parse.function;

@FunctionalInterface
public interface FloatFunction<T>
{
    T apply(float value);
}
