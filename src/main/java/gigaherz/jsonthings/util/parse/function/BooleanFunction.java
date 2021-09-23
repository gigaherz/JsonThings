package gigaherz.jsonthings.util.parse.function;

@FunctionalInterface
public
interface BooleanFunction<T>
{
    T apply(boolean b);
}
