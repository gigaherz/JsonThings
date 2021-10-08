package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.CodeBlock;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class VarExpression<T> implements ValueExpression<T>
{
    private final CodeBlock.LocalVariable<T> localVariable;

    public VarExpression(CodeBlock.LocalVariable<T> localVariable)
    {
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
        if (needsResult) CodeBlock.LocalLoad.compile(localVariable, mv);
    }
}

