package gigaherz.jsonthings.util.parse.value;

import com.google.gson.JsonObject;
import gigaherz.jsonthings.util.parse.function.JsonObjectConsumer;
import gigaherz.jsonthings.util.parse.function.JsonObjectFunction;
import gigaherz.jsonthings.util.parse.function.ObjValueFunction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ObjValue
{
    ObjValue key(String keyName, Consumer<Any> visitor);

    ObjValue ifKey(String keyName, Consumer<Any> visitor);

    void forEach(StringAnyConsumer visitor);

    boolean hasKey(String keyName);

    JsonObject getAsJsonObject();

    void raw(JsonObjectConsumer value);

    default <T> MappedValue<T> map(JsonObjectFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsJsonObject()));
    }

    default <T> MappedValue<T> map(ObjValueFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(this));
    }

    default ObjValue noKey(String keyName, Supplier<RuntimeException> exception)
    {
        if (hasKey(keyName))
            throw exception.get();
        return this;
    }

    default ObjValue mutex(List<String> keys, Supplier<RuntimeException> exception)
    {
        String found = null;
        for (String key : keys)
        {
            if (hasKey(key))
            {
                if (found != null)
                    throw exception.get();

                found = key;
            }
        }
        return this;
    }
}
