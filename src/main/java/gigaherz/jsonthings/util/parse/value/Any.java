package gigaherz.jsonthings.util.parse.value;

import com.google.gson.JsonElement;
import gigaherz.jsonthings.util.parse.function.AnyFunction;
import gigaherz.jsonthings.util.parse.function.JsonElementConsumer;
import gigaherz.jsonthings.util.parse.function.JsonElementFunction;

import java.util.function.Consumer;

public interface Any
{
    ObjValue obj();

    ArrayValue array();

    StringValue string();

    IntValue intValue();

    IntValue longValue();

    FloatValue floatValue();

    DoubleValue doubleValue();

    BooleanValue bool();

    Any ifObj(Consumer<ObjValue> visitor);

    Any ifArray(Consumer<ArrayValue> visitor);

    Any ifString(Consumer<StringValue> visitor);

    Any ifInteger(Consumer<IntValue> visitor);

    Any ifLong(Consumer<LongValue> visitor);

    Any ifFloat(Consumer<FloatValue> visitor);

    Any ifDouble(Consumer<DoubleValue> visitor);

    Any ifBool(Consumer<BooleanValue> visitor);

    void typeError();

    void raw(JsonElementConsumer visitor);

    JsonElement get();

    default <T> MappedValue<T> map(JsonElementFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(get()));
    }

    default <T> MappedValue<T> map(AnyFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(this));
    }
}
