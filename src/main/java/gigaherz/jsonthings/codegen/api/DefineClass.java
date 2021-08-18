package gigaherz.jsonthings.codegen.api;

import com.google.common.reflect.TypeToken;
import gigaherz.jsonthings.codegen.codetree.ClassInfo;

@SuppressWarnings("UnstableApiUsage")
public interface DefineClass<C>
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

    ClassInfo<? extends C> make();
}
