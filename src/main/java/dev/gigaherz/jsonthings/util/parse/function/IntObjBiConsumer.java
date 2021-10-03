package dev.gigaherz.jsonthings.util.parse.function;

@FunctionalInterface
public interface IntObjBiConsumer<T>
{
    void accept(int index, T obj);
}
