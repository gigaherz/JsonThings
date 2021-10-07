package dev.gigaherz.jsonthings.codegen.codetree;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;

@SuppressWarnings("UnstableApiUsage")
public class ParamData<T> implements ParamInfo<T>
{
    public TypeProxy<?> paramType;
    public String name;

    @SuppressWarnings("unchecked")
    @Override
    public TypeProxy<T> paramType()
    {
        return (TypeProxy<T>) this.paramType;
    }

    @Override
    public String name()
    {
        return this.name;
    }
}
