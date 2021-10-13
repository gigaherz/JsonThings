package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.CodeBlockImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.ValueExpressionImpl;
import dev.gigaherz.jsonthings.codegen.codetree.impl.LocalLoad;
import dev.gigaherz.jsonthings.codegen.codetree.impl.LocalVariable;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class VarExpression<T,B> extends ValueExpressionImpl<T,B>
{
    private final LocalVariable<T> localVariable;

    public VarExpression(CodeBlockInternal<B,?,?> cb, LocalVariable<T> localVariable)
    {
        super(cb);
        this.localVariable = localVariable;
    }

    @Override
    public TypeToken<T> effectiveType()
    {
        return localVariable.variableType.actualType();
    }

    @Override
    public TypeProxy<T> proxyType()
    {
        return localVariable.variableType;
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        if (needsResult) LocalLoad.compile(localVariable, mv);
    }
}

