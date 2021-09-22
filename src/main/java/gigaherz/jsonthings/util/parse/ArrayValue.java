package gigaherz.jsonthings.util.parse;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface ArrayValue
{
    void forEach(IntObjBiConsumer<Any> visitor);

    void collect(Consumer<Stream<Any>> collector);

    ArrayValue notEmpty();
    ArrayValue atLeast(int min);
    ArrayValue between(int min, int maxExclusive);
}
