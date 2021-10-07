package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.ClassData;

@SuppressWarnings("UnstableApiUsage")
public interface DefineClass<C> extends Finishable<ClassDef<C>>
{
    DefineClass<C> implementing(TypeToken<?> interfaceClass);

    default DefineClass<C> implementing(Class<?> baseClass)
    {
        return implementing(TypeToken.of(baseClass));
    }

    <F> DefineField<C, F> field(String name, TypeToken<F> fieldType);

    default <F> DefineField<C, F> field(String name, Class<F> fieldType)
    {
        return field(name, TypeToken.of(fieldType));
    }

    <R> DefineMethod<C, R> method(String name, TypeToken<R> returnType);

    default <F> DefineMethod<C, F> method(String name, Class<F> fieldType)
    {
        return method(name, TypeToken.of(fieldType));
    }

    DefineMethod<C, Void> constructor();

    byte[] makeClass();

    ClassData<? extends C> make();
}
