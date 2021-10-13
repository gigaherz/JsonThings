package dev.gigaherz.jsonthings.codegen.api;

public interface DefineParam<C, R, P, T extends DefineParam<C, R, P, T>> extends Implementable<C, R>, Annotatable<T>
{
    T withName(String name);
}
