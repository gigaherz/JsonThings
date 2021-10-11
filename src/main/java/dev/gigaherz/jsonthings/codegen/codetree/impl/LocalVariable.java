package dev.gigaherz.jsonthings.codegen.codetree.impl;

import dev.gigaherz.jsonthings.codegen.type.TypeProxy;

import javax.annotation.Nullable;

public class LocalVariable<T>
{
    public final int index;
    public final TypeProxy<T> variableType;
    public final int slotCount;

    @Nullable
    public String name;


    LocalVariable(int index, TypeProxy<T> variableType, int slotCount)
    {
        this.index = index;
        this.variableType = variableType;
        this.slotCount = slotCount;
    }
}
