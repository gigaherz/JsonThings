package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("UnstableApiUsage")
public interface BasicClass extends DefineClass<Object>, Annotatable<BasicClass>
{
    <T> DefineClass<? extends T> extending(TypeToken<T> baseClass);

    default <T> DefineClass<? extends T> extending(Class<T> baseClass)
    {
        return extending(TypeToken.of(baseClass));
    }

    // default: package-private
    BasicClass setPublic();

    BasicClass setPrivate();

    BasicClass setProtected();

    // default: non-final
    BasicClass setFinal();

    // nested default: inner; top-level: N/A
    BasicClass setStatic();

    // default: not abstract
    BasicClass setAbstract();
}
