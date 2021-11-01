package dev.gigaherz.jsonthings.util.parse.function;

@FunctionalInterface
public interface IntObjBiFunction<T, R>
{
    R apply(int index, T obj);
}
