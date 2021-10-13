package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.CodeBlockImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.ValueExpressionImpl;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class UnaryOperator<R, B> extends ValueExpressionImpl<R,B>
{
    private final int opcode;
    private final ValueExpression<R,B> value;

    public UnaryOperator(CodeBlockInternal<B,?,?> cb, int opcode, ValueExpression<R,B> value)
    {
        super(cb);
        this.opcode = opcode;
        this.value = value;
    }

    @Override
    public TypeToken<R> effectiveType()
    {
        return value.effectiveType();
    }

    @Override
    public TypeProxy<R> proxyType()
    {
        return value.proxyType();
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        value.compile(mv, needsResult);
        if (needsResult)
        {
            mv.visitInsn(opcode);
        }
    }
}
