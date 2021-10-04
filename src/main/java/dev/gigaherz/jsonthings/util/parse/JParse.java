package dev.gigaherz.jsonthings.util.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.util.parse.function.IntObjBiConsumer;
import dev.gigaherz.jsonthings.util.parse.value.*;
import dev.gigaherz.jsonthings.util.parse.function.JsonArrayConsumer;
import dev.gigaherz.jsonthings.util.parse.function.JsonElementConsumer;
import dev.gigaherz.jsonthings.util.parse.function.JsonObjectConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JParse
        implements Any, ObjValue, ArrayValue, StringValue, FloatValue, DoubleValue, IntValue, LongValue, BooleanValue
{
    private final String path;
    private final JsonElement data;
    private final List<String> altTypes = new ArrayList<>();
    private boolean handledType = false;

    public JParse(String path, JsonElement data)
    {
        this.path = path;
        this.data = data;
    }

    public static Any begin(JsonElement data)
    {
        return new JParse("$", data);
    }

    private String formatAltTypes(String and)
    {
        if (altTypes.size() > 0)
        {
            return String.join(", ", altTypes) + ", or " + and;
        }

        return and;
    }

    private String formatAltTypes()
    {
        if (altTypes.size() > 0)
        {
            return String.join(", ", altTypes);
        }

        throw new RuntimeException("IMPLEMENTATION ERROR: typeError() called without having used any ifType() methods!");
    }

    @Override
    public ObjValue obj()
    {
        if (!data.isJsonObject())
        {
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes("a Json Object"));
        }
        return this;
    }

    @Override
    public ArrayValue array()
    {
        if (!data.isJsonArray())
        {
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes("a Json Array"));
        }
        return this;
    }

    @Override
    public StringValue string()
    {
        if (!GsonHelper.isStringValue(data))
        {
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes("a String"));
        }
        return this;
    }

    @Override
    public IntValue intValue()
    {
        if (!GsonHelper.isNumberValue(data))
        {
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes("an Integer"));
        }
        return this;
    }

    @Override
    public IntValue longValue()
    {
        if (!GsonHelper.isNumberValue(data))
        {
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes("a Long Integer"));
        }
        return this;
    }

    @Override
    public FloatValue floatValue()
    {
        if (!GsonHelper.isNumberValue(data))
        {
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes("a Float"));
        }
        return this;
    }

    @Override
    public DoubleValue doubleValue()
    {
        if (!GsonHelper.isNumberValue(data))
        {
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes("a Double"));
        }
        return this;
    }

    @Override
    public BooleanValue bool()
    {
        if (!GsonHelper.isBooleanValue(data))
        {
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes("a Boolean"));
        }
        return this;
    }

    @Override
    public void typeError()
    {
        if (!handledType)
            throw new JParseException("Value at '" + path + "' must be " + formatAltTypes());
    }

    @Override
    public Any ifObj(Consumer<ObjValue> visitor)
    {
        altTypes.add("a Json Object");
        if (data.isJsonObject())
        {
            handledType = true;
            try
            {
                visitor.accept(this);
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + path, e);
            }
        }
        return this;
    }

    @Override
    public Any ifArray(Consumer<ArrayValue> visitor)
    {
        altTypes.add("a Json Array");
        if (data.isJsonArray())
        {
            handledType = true;
            try
            {
                visitor.accept(this);
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + path, e);
            }
        }
        return this;
    }

    @Override
    public Any ifString(Consumer<StringValue> visitor)
    {
        altTypes.add("aString");
        if (GsonHelper.isStringValue(data))
        {
            handledType = true;
            try
            {
                visitor.accept(this);
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + path, e);
            }
        }
        return this;
    }

    @Override
    public Any ifInteger(Consumer<IntValue> visitor)
    {
        altTypes.add("an Integer");
        if (GsonHelper.isNumberValue(data))
        {
            handledType = true;
            try
            {
                visitor.accept(this);
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + path, e);
            }
        }
        return this;
    }

    @Override
    public Any ifLong(Consumer<LongValue> visitor)
    {
        altTypes.add("a Long Integer");
        if (GsonHelper.isNumberValue(data))
        {
            handledType = true;
            try
            {
                visitor.accept(this);
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + path, e);
            }
        }
        return this;
    }

    @Override
    public Any ifFloat(Consumer<FloatValue> visitor)
    {
        altTypes.add("a Float");
        if (GsonHelper.isNumberValue(data))
        {
            handledType = true;
            try
            {
                visitor.accept(this);
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + path, e);
            }
        }
        return this;
    }

    @Override
    public Any ifDouble(Consumer<DoubleValue> visitor)
    {
        altTypes.add("a Double");
        if (GsonHelper.isNumberValue(data))
        {
            handledType = true;
            try
            {
                visitor.accept(this);
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + path, e);
            }
        }
        return this;
    }

    @Override
    public Any ifBool(Consumer<BooleanValue> visitor)
    {
        altTypes.add("a Boolean");
        if (GsonHelper.isBooleanValue(data))
        {
            handledType = true;
            try
            {
                visitor.accept(this);
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + path, e);
            }
        }
        return this;
    }

    @Override
    public void raw(JsonElementConsumer visitor)
    {
        visitor.accept(data);
    }

    @Override
    public JsonElement get()
    {
        return data;
    }

    @Override
    public ObjValue key(String keyName, Consumer<Any> visitor)
    {
        var obj = getAsJsonObject();
        if (!obj.has(keyName))
        {
            throw new JParseException("Json Object at '" + path + "' must contain a key with name '" + keyName + "'.");
        }
        var keyPath = path + wrapName(keyName);
        try
        {
            visitor.accept(new JParse(keyPath, obj.get(keyName)));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error running visitor for " + keyPath);
        }
        return this;
    }

    @Override
    public ObjValue ifKey(String keyName, Consumer<Any> visitor)
    {
        var obj = getAsJsonObject();
        if (obj.has(keyName))
        {
            var keyPath = path + wrapName(keyName);
            try
            {
                visitor.accept(new JParse(keyPath, obj.get(keyName)));
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + keyPath, e);
            }
        }
        return this;
    }

    @Override
    public void forEach(StringAnyConsumer visitor)
    {
        var obj = getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : obj.entrySet())
        {
            var keyName = entry.getKey();
            var keyPath = path + wrapName(keyName);
            visitor.accept(keyName, new JParse(keyPath, entry.getValue()));
        }
    }

    @Override
    public boolean hasKey(String keyName)
    {
        return getAsJsonObject().has(keyName);
    }

    @Override
    public JsonObject getAsJsonObject()
    {
        return data.getAsJsonObject();
    }

    private static final Pattern SIMPLE_IDENT = Pattern.compile("^[a-zA-Z0-9_]+$");

    private String wrapName(String keyName)
    {
        if (SIMPLE_IDENT.matcher(keyName).matches())
            return "." + keyName;
        return "[\"" + keyName.replace("\"", "\\\"") + "\"]";
    }

    @Override
    public void raw(JsonObjectConsumer value)
    {
        try
        {
            value.accept(getAsJsonObject());
        }
        catch (Exception e)
        {
            if (e instanceof JParseException)
                throw e;
            throw new JParseException("Error running handler for " + path, e);
        }
    }

    @Override
    public void forEach(IntObjBiConsumer<Any> visitor)
    {
        var arr = getAsJsonArray();
        for (int i = 0; i < arr.size(); i++)
        {
            var entryPath = path + "[" + i + "]";
            try
            {
                visitor.accept(i, new JParse(entryPath, arr.get(i)));
            }
            catch (Exception e)
            {
                if (e instanceof JParseException)
                    throw e;
                throw new JParseException("Error running visitor for " + entryPath, e);
            }
        }
    }

    @Override
    public void collect(Consumer<Stream<Any>> collector)
    {
        var arr = getAsJsonArray();
        collector.accept(IntStream.range(0, arr.size())
                .mapToObj(i -> {
                    var entryPath = path + "[" + i + "]";
                    try
                    {
                        return new JParse(entryPath, arr.get(i));
                    }
                    catch (Exception e)
                    {
                        if (e instanceof JParseException)
                            throw e;
                        throw new JParseException("Error running visitor for " + entryPath, e);
                    }
                }));
    }

    @Override
    public <T> T flatMap(Function<Stream<Any>, T> collector)
    {
        var arr = getAsJsonArray();
        return collector.apply(IntStream.range(0, arr.size())
                .mapToObj(i -> {
                    var entryPath = path + "[" + i + "]";
                    try
                    {
                        return new JParse(entryPath, arr.get(i));
                    }
                    catch (Exception e)
                    {
                        if (e instanceof JParseException)
                            throw e;
                        throw new JParseException("Error running visitor for " + entryPath, e);
                    }
                }));
    }

    @Override
    public void raw(JsonArrayConsumer value)
    {
        try
        {
            value.accept(getAsJsonArray());
        }
        catch (Exception e)
        {
            if (e instanceof JParseException)
                throw e;
            throw new JParseException("Error running handler for " + path, e);
        }
    }

    @Override
    public ArrayValue notEmpty()
    {
        var arr = getAsJsonArray();
        if (arr.size() == 0)
        {
            throw new JParseException("Json Array at '" + path + "' must not be empty.");
        }
        return this;
    }

    @Override
    public ArrayValue atLeast(int min)
    {
        var arr = getAsJsonArray();
        if (arr.size() < min)
        {
            throw new JParseException("Json Array at '" + path + "' must contain at least " + min + ".");
        }
        return this;
    }

    @Override
    public ArrayValue between(int min, int maxExclusive)
    {
        var arr = getAsJsonArray();
        if (arr.size() < min)
        {
            throw new JParseException("Json Array at '" + path + "' must contain at least " + min + ".");
        }
        return this;
    }

    @Override
    public JsonArray getAsJsonArray()
    {
        return data.getAsJsonArray();
    }

    @Override
    public void handle(Consumer<String> value)
    {
        try
        {
            value.accept(getAsString());
        }
        catch (Exception e)
        {
            if (e instanceof JParseException)
                throw e;
            throw new JParseException("Error running handler for " + path, e);
        }
    }

    @Override
    public String getAsString()
    {
        return data.getAsJsonPrimitive().getAsString();
    }

    @Override
    public void handle(FloatConsumer value)
    {
        value.accept(getAsFloat());
    }

    @Override
    public FloatValue min(float min)
    {
        var val = getAsFloat();
        if (val < min)
        {
            throw new JParseException("Value at '" + path + "' must be " + min + " or bigger.");
        }
        return this;
    }

    @Override
    public FloatValue range(float min, float maxExclusive)
    {
        var val = getAsFloat();
        if (val < min || val >= maxExclusive)
        {
            throw new JParseException("Value at '" + path + "' must be betwee " + min + " and " + maxExclusive + " (exclusive).");
        }
        return this;
    }

    @Override
    public float getAsFloat()
    {
        return data.getAsJsonPrimitive().getAsFloat();
    }

    @Override
    public void handle(DoubleConsumer value)
    {
        try
        {
            value.accept(getAsDouble());
        }
        catch (Exception e)
        {
            if (e instanceof JParseException)
                throw e;
            throw new JParseException("Error running handler for " + path, e);
        }
    }

    @Override
    public DoubleValue min(double min)
    {
        var val = getAsDouble();
        if (val < min)
        {
            throw new JParseException("Value at '" + path + "' must be " + min + " or bigger.");
        }
        return this;
    }

    @Override
    public DoubleValue range(double min, double maxExclusive)
    {
        var val = getAsDouble();
        if (val < min || val >= maxExclusive)
        {
            throw new JParseException("Value at '" + path + "' must be betwee " + min + " and " + maxExclusive + " (exclusive).");
        }
        return this;
    }

    @Override
    public double getAsDouble()
    {
        return data.getAsJsonPrimitive().getAsDouble();
    }

    @Override
    public void handle(IntConsumer value)
    {
        try
        {
            value.accept(getAsInt());
        }
        catch (Exception e)
        {
            if (e instanceof JParseException)
                throw e;
            throw new JParseException("Error running handler for " + path, e);
        }
    }

    @Override
    public IntValue min(int min)
    {
        var val = getAsInt();
        if (val < min)
        {
            throw new JParseException("Value at '" + path + "' must be " + min + " or bigger.");
        }
        return this;
    }

    @Override
    public IntValue range(int min, int maxExclusive)
    {
        var val = data.getAsJsonPrimitive().getAsInt();
        if (val < min || val >= maxExclusive)
        {
            throw new JParseException("Value at '" + path + "' must be betwee " + min + " and " + maxExclusive + " (exclusive).");
        }
        return this;
    }

    @Override
    public int getAsInt()
    {
        return data.getAsJsonPrimitive().getAsInt();
    }

    @Override
    public void handle(BooleanConsumer value)
    {
        try
        {
            value.accept(getAsBoolean());
        }
        catch (Exception e)
        {
            if (e instanceof JParseException)
                throw e;
            throw new JParseException("Error running handler for " + path, e);
        }
    }

    @Override
    public boolean getAsBoolean()
    {
        return data.getAsJsonPrimitive().getAsBoolean();
    }

    @Override
    public void handle(LongConsumer value)
    {
        try
        {
            value.accept(getAsLong());
        }
        catch (Exception e)
        {
            if (e instanceof JParseException)
                throw e;
            throw new JParseException("Error running handler for " + path, e);
        }
    }

    @Override
    public LongValue min(long min)
    {
        var val = getAsLong();
        if (val < min)
        {
            throw new JParseException("Value at '" + path + "' must be " + min + " or bigger.");
        }
        return this;
    }

    @Override
    public LongValue range(long min, long maxExclusive)
    {
        var val = getAsLong();
        if (val < min || val >= maxExclusive)
        {
            throw new JParseException("Value at '" + path + "' must be betwee " + min + " and " + maxExclusive + " (exclusive).");
        }
        return this;
    }

    @Override
    public long getAsLong()
    {
        return data.getAsJsonPrimitive().getAsLong();
    }
}
