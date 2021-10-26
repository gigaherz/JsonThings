package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlock;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public interface FinishToClass<T> extends Finishable<ClassDef<T>>, DefineClass<T>
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

    @Override
    default byte[] makeClass()
    {
        return finish().makeClass();
    }

    @Override
    default DefineClass<T> replicateParentConstructors(Consumer<CodeBlock<Void,?,T>> cb)
    {
        return finish().replicateParentConstructors(cb);
    }

    @Override
    default DefineClass<T> replicateParentConstructors(Predicate<MethodInfo<Void>> filter, Consumer<CodeBlock<Void,?,T>> cb)
    {
        return finish().replicateParentConstructors(filter, cb);
    }
}
