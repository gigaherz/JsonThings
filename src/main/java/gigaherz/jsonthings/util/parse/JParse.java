package gigaherz.jsonthings.util.parse;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.IntRange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JParse
        implements Any, ObjValue, ArrayValue, StringValue, FloatValue, DoubleValue, IntValue, LongValue, BooleanValue
{
    private final String path;
    private final JsonElement data;
    private final List<String> altTypes = new ArrayList<>();

    public JParse(String path, JsonElement data)
    {
        this.path = path;
        this.data = data;
    }

    public static Any begin(JsonElement data)
    {
        return new JParse("/", data);
    }

    private String formatAltTypes(String and)
    {
        if (altTypes.size() > 0)
        {
            return String.join(", ", altTypes) + ", or " + and;
        }

        return and;
    }

    @Override
    public ObjValue obj()
    {
        if (!data.isJsonObject())
        {
            throw new JsonParseException("Value at '" + path + "' must be " + formatAltTypes("a Json Object"));
        }
        return this;
    }

    @Override
    public ArrayValue array()
    {
        if (!data.isJsonArray())
        {
            throw new JsonParseException("Value at '" + path + "' must be " + formatAltTypes("a Json Array"));
        }
        return this;
    }

    @Override
    public StringValue string()
    {
        if (!GsonHelper.isStringValue(data))
        {
            throw new JsonParseException("Value at '" + path + "' must be " + formatAltTypes("a String"));
        }
        return this;
    }

    @Override
    public IntValue intValue()
    {
        if (!GsonHelper.isNumberValue(data))
        {
            throw new JsonParseException("Value at '" + path + "' must be " + formatAltTypes("an Integer"));
        }
        return this;
    }

    @Override
    public IntValue longValue()
    {
        if (!GsonHelper.isNumberValue(data))
        {
            throw new JsonParseException("Value at '" + path + "' must be " + formatAltTypes("a Long Integer"));
        }
        return this;
    }

    @Override
    public FloatValue floatValue()
    {
        if (!GsonHelper.isNumberValue(data))
        {
            throw new JsonParseException("Value at '" + path + "' must be " + formatAltTypes("a Float"));
        }
        return this;
    }

    @Override
    public DoubleValue doubleValue()
    {
        if (!GsonHelper.isNumberValue(data))
        {
            throw new JsonParseException("Value at '" + path + "' must be " + formatAltTypes("a Double"));
        }
        return this;
    }

    @Override
    public BooleanValue bool()
    {
        if (!GsonHelper.isBooleanValue(data))
        {
            throw new JsonParseException("Value at '" + path + "' must be " + formatAltTypes("a Boolean"));
        }
        return this;
    }

    @Override
    public Any ifObj(Consumer<ObjValue> visitor)
    {
        altTypes.add("a Json Object");
        if (data.isJsonObject())
        {
            visitor.accept(this);
        }
        return this;
    }

    @Override
    public Any ifArray(Consumer<ArrayValue> visitor)
    {
        altTypes.add("a Json Array");
        if (data.isJsonArray())
        {
            visitor.accept(this);
        }
        return this;
    }

    @Override
    public Any ifString(Consumer<StringValue> visitor)
    {
        altTypes.add("aString");
        if (GsonHelper.isStringValue(data))
        {
            visitor.accept(this);
        }
        return this;
    }

    @Override
    public Any ifInteger(Consumer<IntValue> visitor)
    {
        altTypes.add("an Integer");
        if (GsonHelper.isNumberValue(data))
        {
            visitor.accept(this);
        }
        return this;
    }

    @Override
    public Any ifLong(Consumer<LongValue> visitor)
    {
        altTypes.add("a Long Integer");
        if (GsonHelper.isNumberValue(data))
        {
            visitor.accept(this);
        }
        return this;
    }

    @Override
    public Any ifFloat(Consumer<FloatValue> visitor)
    {
        altTypes.add("a Float");
        if (GsonHelper.isNumberValue(data))
        {
            visitor.accept(this);
        }
        return this;
    }

    @Override
    public Any ifDouble(Consumer<DoubleValue> visitor)
    {
        altTypes.add("a Double");
        if (GsonHelper.isNumberValue(data))
        {
            visitor.accept(this);
        }
        return this;
    }

    @Override
    public Any ifBool(Consumer<BooleanValue> visitor)
    {
        altTypes.add("a Boolean");
        if (GsonHelper.isBooleanValue(data))
        {
            visitor.accept(this);
        }
        return this;
    }

    @Override
    public ObjValue key(String keyName, Consumer<Any> visitor)
    {
        var obj = data.getAsJsonObject();
        if (!obj.has(keyName))
        {
            throw new JsonParseException("Object at '" + path + "' must contain a key with name '" + keyName + "'.");
        }
        var keyPath = path + "/" + keyName;
        visitor.accept(new JParse(keyPath, obj.get(keyName)));
        return this;
    }

    @Override
    public ObjValue ifKey(String keyName, Consumer<Any> visitor)
    {
        var obj = data.getAsJsonObject();
        if (obj.has("keyName"))
        {
            var keyPath = path + "/" + keyName;
            visitor.accept(new JParse(keyPath, obj.get(keyName)));
        }
        return this;
    }

    @Override
    public void forEach(IntObjBiConsumer<Any> visitor)
    {
        var arr = data.getAsJsonArray();
        for (int i=0;i<arr.size();i++)
        {
            var entryPath = path + "[" + i + "]";
            visitor.accept(i, new JParse(entryPath, arr.get(i)));
        }
    }

    @Override
    public void collect(Consumer<Stream<Any>> collector)
    {
        var arr = data.getAsJsonArray();
        collector.accept(IntStream.range(0, arr.size())
                .mapToObj(i -> {
                    var entryPath = path + "[" + i + "]";
                    return new JParse(entryPath, arr.get(i));
                }));
    }

    @Override
    public ArrayValue notEmpty()
    {
        var arr = data.getAsJsonArray();
        if (arr.size() == 0)
        {
            throw new JsonParseException("Array at '" + path + "' must not be empty.");
        }
        return this;
    }

    @Override
    public ArrayValue atLeast(int min)
    {
        var arr = data.getAsJsonArray();
        if (arr.size() < min)
        {
            throw new JsonParseException("Array at '" + path + "' must contain at least "+min+".");
        }
        return this;
    }

    @Override
    public ArrayValue between(int min, int maxExclusive)
    {
        var arr = data.getAsJsonArray();
        if (arr.size() < min)
        {
            throw new JsonParseException("Array at '" + path + "' must contain at least "+min+".");
        }
        return this;
    }

    @Override
    public void handle(Consumer<String> value)
    {
        value.accept(data.getAsJsonPrimitive().getAsString());
    }

    @Override
    public void handle(FloatConsumer value)
    {
        value.accept(data.getAsJsonPrimitive().getAsFloat());
    }

    @Override
    public FloatValue min(float min)
    {
        var val = data.getAsJsonPrimitive().getAsFloat();
        if (val < min)
        {
            throw new JsonParseException("Value at '" + path + "' must be "+min+" or bigger.");
        }
        return this;
    }

    @Override
    public FloatValue range(float min, float maxExclusive)
    {
        var val = data.getAsJsonPrimitive().getAsFloat();
        if (val < min || val >= maxExclusive)
        {
            throw new JsonParseException("Value at '" + path + "' must be betwee "+min+" and "+maxExclusive+" (exclusive).");
        }
        return this;
    }

    @Override
    public void handle(DoubleConsumer value)
    {
        value.accept(data.getAsJsonPrimitive().getAsDouble());
    }

    @Override
    public DoubleValue min(double min)
    {
        var val = data.getAsJsonPrimitive().getAsDouble();
        if (val < min)
        {
            throw new JsonParseException("Value at '" + path + "' must be "+min+" or bigger.");
        }
        return this;
    }

    @Override
    public DoubleValue range(double min, double maxExclusive)
    {
        var val = data.getAsJsonPrimitive().getAsDouble();
        if (val < min || val >= maxExclusive)
        {
            throw new JsonParseException("Value at '" + path + "' must be betwee "+min+" and "+maxExclusive+" (exclusive).");
        }
        return this;
    }

    @Override
    public void handle(IntConsumer value)
    {
        value.accept(data.getAsJsonPrimitive().getAsInt());
    }

    @Override
    public IntValue min(int min)
    {
        var val = data.getAsJsonPrimitive().getAsInt();
        if (val < min)
        {
            throw new JsonParseException("Value at '" + path + "' must be "+min+" or bigger.");
        }
        return this;
    }

    @Override
    public IntValue range(int min, int maxExclusive)
    {
        var val = data.getAsJsonPrimitive().getAsInt();
        if (val < min || val >= maxExclusive)
        {
            throw new JsonParseException("Value at '" + path + "' must be betwee "+min+" and "+maxExclusive+" (exclusive).");
        }
        return this;
    }

    @Override
    public void handle(BooleanConsumer value)
    {
        value.accept(data.getAsJsonPrimitive().getAsBoolean());
    }

    @Override
    public void handle(LongConsumer value)
    {
        value.accept(data.getAsJsonPrimitive().getAsLong());
    }

    @Override
    public LongValue min(long min)
    {
        var val = data.getAsJsonPrimitive().getAsLong();
        if (val < min)
        {
            throw new JsonParseException("Value at '" + path + "' must be "+min+" or bigger.");
        }
        return this;
    }

    @Override
    public LongValue range(long min, long maxExclusive)
    {
        var val = data.getAsJsonPrimitive().getAsLong();
        if (val < min || val >= maxExclusive)
        {
            throw new JsonParseException("Value at '" + path + "' must be betwee "+min+" and "+maxExclusive+" (exclusive).");
        }
        return this;
    }
}
