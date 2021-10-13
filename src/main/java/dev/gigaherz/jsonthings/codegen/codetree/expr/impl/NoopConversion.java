package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.CodeBlockImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.ValueExpressionImpl;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class NoopConversion<R,B> extends ValueExpressionImpl<R,B>
{
    private final TypeToken<R> targetType;
    private final ValueExpression<?,B> value;

    public NoopConversion(CodeBlockInternal<B,?,?> cb, TypeToken<R> targetType, ValueExpression<?,B> value)
    {
        super(cb);
        this.targetType = targetType;
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
        value.compile(mv, needsResult);
    }
}

