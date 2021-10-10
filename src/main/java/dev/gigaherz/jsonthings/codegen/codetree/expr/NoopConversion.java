package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class NoopConversion<R,B> extends ValueExpression<R,B>
{
    private final TypeToken<R> targetType;
    private final ValueExpression<?,B> value;

    public NoopConversion(CodeBlock<B,?,?> cb, TypeToken<R> targetType, ValueExpression<?,B> value)
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

