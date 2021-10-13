package dev.gigaherz.jsonthings.codegen.api.codetree.info;

import dev.gigaherz.jsonthings.codegen.type.TypeProxy;

@SuppressWarnings("UnstableApiUsage")
public interface ParamInfo<T>
{
    TypeProxy<T> paramType();

    String name();
}
