package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("UnstableApiUsage")
public interface VarToken<T>
{
    String name();
    TypeToken<T> type();
}
