package dev.gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlock;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

    default <F> DefineField<C, F> field(FieldToken<F> token)
    {
        return field(token.name(), token.type());
    }

    <R> DefineMethod<C, R> method(String name, TypeToken<R> returnType);

    default <F> DefineMethod<C, F> method(String name, Class<F> fieldType)
    {
        return method(name, TypeToken.of(fieldType));
    }

    DefineMethod<C, Void> constructor();

    DefineClass<C> replicateParentConstructors(Consumer<CodeBlock<Void,?,C>> cb);
    DefineClass<C> replicateParentConstructors(Predicate<MethodInfo<Void>> filter, Consumer<CodeBlock<Void,?,C>> cb);

    byte[] makeClass();

    ClassInfo<? extends C> make();
}
