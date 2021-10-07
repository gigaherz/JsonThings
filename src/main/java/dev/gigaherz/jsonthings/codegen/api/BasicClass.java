package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("UnstableApiUsage")
public interface BasicClass extends ClassDef<Object>, Annotatable<BasicClass>
{
    <T> ClassDef<? extends T> extending(TypeToken<T> baseClass);

    default <T> ClassDef<? extends T> extending(Class<T> baseClass)
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
