package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class UnaryConversion<R, T, B> extends ValueExpressionImpl<R, B>
{
    private final TypeToken<R> targetType;
    private final int opcode;
    private final ValueExpression<T, B> value;

    public UnaryConversion(CodeBlockInternal<B, ?, ?> cb, TypeToken<R> targetType, int opcode, ValueExpression<T, B> value)
    {
        super(cb);
        this.targetType = targetType;
        this.opcode = opcode;
        this.value = value;
    }

    @Override
    public TypeToken<R> effectiveType()
    {
        return targetType;
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        if (needsResult)
        {
            value.compile(mv, true);
            mv.visitInsn(opcode);
        }
    }
}
