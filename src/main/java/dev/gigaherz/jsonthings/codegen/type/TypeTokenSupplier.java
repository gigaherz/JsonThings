package dev.gigaherz.jsonthings.codegen.type;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("UnstableApiUsage")
public interface TypeTokenSupplier<T>
{
    TypeToken<T> actualType();
}
