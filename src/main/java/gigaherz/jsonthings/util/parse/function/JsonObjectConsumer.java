package gigaherz.jsonthings.util.parse.function;

import com.google.gson.JsonObject;

import java.util.function.Consumer;

@FunctionalInterface
public interface JsonObjectConsumer extends Consumer<JsonObject>
{
}

