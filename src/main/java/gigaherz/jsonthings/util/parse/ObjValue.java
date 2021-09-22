package gigaherz.jsonthings.util.parse;

import com.google.gson.JsonObject;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ObjValue
{
    ObjValue key(String keyName, Consumer<Any> visitor);

    ObjValue ifKey(String keyName, Consumer<Any> visitor);

    void handleObj(Consumer<JsonObject> value);
}
