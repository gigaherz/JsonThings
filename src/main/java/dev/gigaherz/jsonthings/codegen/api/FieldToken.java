package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("UnstableApiUsage")
public interface FieldToken<T>
{
    String name();
    TypeToken<T> type();
}
