package gigaherz.jsonthings.util.parse;

@FunctionalInterface
public interface IntObjBiConsumer<T>
{
    void accept(int index, T obj);
}
