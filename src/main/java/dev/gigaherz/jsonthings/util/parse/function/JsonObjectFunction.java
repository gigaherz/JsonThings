package dev.gigaherz.jsonthings.util.parse.function;

import com.google.gson.JsonObject;

import java.util.function.Function;

@FunctionalInterface
public interface JsonObjectFunction<T> extends Function<JsonObject, T>
{
}
