package gigaherz.jsonthings.util.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface ArrayValue
{
    void forEach(IntObjBiConsumer<Any> visitor);

    void collect(Consumer<Stream<Any>> collector);

    void handleArray(Consumer<JsonArray> value);

    ArrayValue notEmpty();
    ArrayValue atLeast(int min);
    ArrayValue between(int min, int maxExclusive);
}
