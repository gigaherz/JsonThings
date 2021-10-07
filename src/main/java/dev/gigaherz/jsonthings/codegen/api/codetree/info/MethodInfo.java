package dev.gigaherz.jsonthings.codegen.api.codetree.info;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public interface MethodInfo<R>
{
    List<? extends ParamInfo<?>> params();
    TypeToken<R> returnType();
    ClassInfo<?> owner();
    String name();
    int modifiers();

    default boolean isStatic()
    {
        return Modifier.isStatic(modifiers());
    }
}
