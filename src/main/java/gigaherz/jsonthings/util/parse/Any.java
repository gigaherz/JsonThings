package gigaherz.jsonthings.util.parse;

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
}
