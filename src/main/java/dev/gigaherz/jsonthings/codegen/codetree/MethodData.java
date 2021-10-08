package dev.gigaherz.jsonthings.codegen.codetree;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MethodData<R> implements MethodInfo<R>
{
    public List<ParamInfo<?>> params = Lists.newArrayList();
    public TypeToken<R> returnType;
    public ClassData<?> owner;
    public String name;
    public int modifiers;

    public MethodData(ClassData<?> owner, String name, TypeToken<R> returnType, int modifiers)
    {
        this.owner = owner;
        this.name = name;
        this.returnType = returnType;
        this.modifiers = modifiers;
    }

    @Override
    public List<ParamInfo<?>> params()
    {
        return this.params;
    }

    @Override
    public TypeToken<R> returnType()
    {
        return this.returnType;
    }

    @Override
    public ClassInfo<?> owner()
    {
        return this.owner;
    }

    @Override
    public String name()
    {
        return this.name;
    }

    @Override
    public int modifiers()
    {
        return this.modifiers;
    }
}
