package gigaherz.jsonthings.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.util.GsonHelper;

import java.util.function.*;
import java.util.stream.Stream;

public class ParseUtils
{
    /*
    JParse.begin(data)
        .obj()
            .key("x", v -> v.string().
     */


    public static void bool(JsonObject data, String key, BooleanConsumer action)
    {
        action.accept(GsonHelper.getAsBoolean(data, key));
    }

    public static void boolOptional(JsonObject data, String key, BooleanConsumer action)
    {
        if (!data.has(key))
            return;
        bool(data, key, action);
    }

    public static void string(JsonObject data, String key, Consumer<String> action)
    {
        action.accept(GsonHelper.getAsString(data, key));
    }

    public static void stringOptional(JsonObject data, String key, Consumer<String> action)
    {
        if (!data.has(key))
            return;
        string(data, key, action);
    }

    public static void intPositive(JsonObject data, String key, IntConsumer action)
    {
        int value = GsonHelper.getAsInt(data, key);
        if (value > 0)
        {
            action.accept(value);
        }
        else
        {
            throw new JsonSyntaxException("'"+key+"' must be a positive integer greater than zero.");
        }
    }

    public static void intPositiveOptional(JsonObject data, String key, IntConsumer action)
    {
        if (!data.has(key))
            return;
        intPositive(data, key, action);
    }

    public static void intPositiveOrZero(JsonObject data, String key, IntConsumer action)
    {
        int value = GsonHelper.getAsInt(data, key);
        if (value >= 0)
        {
            action.accept(value);
        }
        else
        {
            throw new JsonSyntaxException("'"+key+"' must be a positive integer or zero.");
        }
    }

    public static void intPositiveOrZeroOptional(JsonObject data, String key, IntConsumer action)
    {
        if (!data.has(key))
            return;
        intPositiveOrZero(data, key, action);
    }

    public static void intRange(JsonObject data, String key, int min, int max, IntConsumer action)
    {
        int value = GsonHelper.getAsInt(data, key);
        if (value >= min && value < max)
        {
            action.accept(value);
        }
        else
        {
            throw new JsonSyntaxException("'"+key+"' must be an integer between "+min+" and "+max+".");
        }
    }

    public static void intRangeOptional(JsonObject data, String key, int min, int max, IntConsumer action)
    {
        if (!data.has(key))
            return;
        intRange(data, key, min, max, action);
    }

    public static void floatPositive(JsonObject data, String key, FloatConsumer action)
    {
        var value = GsonHelper.getAsFloat(data, key);
        if (value > 0)
        {
            action.accept(value);
        }
        else
        {
            throw new JsonSyntaxException("'"+key+"' must be a positive number greater than zero.");
        }
    }

    public static void floatPositiveOptional(JsonObject data, String key, FloatConsumer action)
    {
        if (!data.has(key))
            return;
        floatPositive(data, key, action);
    }

    public static void floatPositiveOrZero(JsonObject data, String key, FloatConsumer action)
    {
        var value = GsonHelper.getAsFloat(data, key);
        if (value >= 0)
        {
            action.accept(value);
        }
        else
        {
            throw new JsonSyntaxException("'"+key+"' must be a positive number or zero.");
        }
    }

    public static void floatPositiveOrZeroOptional(JsonObject data, String key, FloatConsumer action)
    {
        if (!data.has(key))
            return;
        floatPositiveOrZero(data, key, action);
    }

    public static void floatRange(JsonObject data, String key, float min, float max, FloatConsumer action)
    {
        var value = GsonHelper.getAsFloat(data, key);
        if (value >= min && value < max)
        {
            action.accept(value);
        }
        else
        {
            throw new JsonSyntaxException("'"+key+"' must be a number between "+min+" and "+max+".");
        }
    }

    public static void floatRangeOptional(JsonObject data, String key, float min, float max, FloatConsumer action)
    {
        if (!data.has(key))
            return;
        floatRange(data, key, min, max, action);
    }
}
