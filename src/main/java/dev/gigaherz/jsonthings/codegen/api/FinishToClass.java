package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.ClassInfo;

@SuppressWarnings("UnstableApiUsage")
public interface FinishToClass<T> extends Finishable<DefineClass<T>>, DefineClass<T>
{
    @Override
    default DefineClass<T> implementing(TypeToken<?> interfaceClass)
    {
        return finish().implementing(interfaceClass);
    }

    @Override
    default <F> DefineField<T, F> field(String name, TypeToken<F> fieldType)
    {
        return finish().field(name, fieldType);
    }

    @Override
    default <F> DefineMethod<T, F> method(String name, TypeToken<F> returnType)
    {
        return finish().method(name, returnType);
    }

    @Override
    default DefineMethod<T, Void> constructor()
    {
        return finish().constructor();
    }

    @Override
    default ClassInfo<? extends T> make()
    {
        return finish().make();
    }
}
