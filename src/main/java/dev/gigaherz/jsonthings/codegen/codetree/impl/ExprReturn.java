package dev.gigaherz.jsonthings.codegen.codetree.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlock;
import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.CodeBlockImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.ValueExpressionImpl;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class ExprReturn extends InstructionSource
{
    private final CodeBlock<?, ?, ?> cb;
    private final TypeToken<?> returnType;

    private final ValueExpression<?, ?> value;

    public ExprReturn(CodeBlock<?, ?, ?> cb, TypeToken<?> returnType, ValueExpression<?, ?> value)
    {
        this.cb = cb;
        this.returnType = returnType;
        this.value = value;
    }

    @Override
    public boolean compile(MethodVisitor mv, Label jumpEnd, boolean needsResult)
    {
        mv.visitLabel(cb.owner().makeLabel());
        value.compile(mv, true);
        Return.compileReturn(returnType, mv);
        return true;
    }
}
