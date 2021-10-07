package dev.gigaherz.jsonthings.codegen.codetree;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;

@SuppressWarnings("UnstableApiUsage")
public class FieldData<T> implements FieldInfo<T>
{
    public String name;
    public int modifiers;
    public TypeToken<?> type;
    public ClassInfo<?> owner;

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

    @SuppressWarnings("unchecked")
    @Override
    public TypeToken<T> type()
    {
        return (TypeToken<T>) this.type;
    }

    @Override
    public ClassInfo<?> owner()
    {
        return owner;
    }
}
