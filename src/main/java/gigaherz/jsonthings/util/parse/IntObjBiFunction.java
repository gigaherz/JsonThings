package gigaherz.jsonthings.util.parse;

@FunctionalInterface
public interface IntObjBiFunction<T, R>
{
    R apply(int index, T obj);
}
