package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

public class NoopConversion<R> implements ValueExpression<R>
{
    private final TypeToken<R> targetType;
    private final ValueExpression<?> value;

    public NoopConversion(TypeToken<R> targetType, ValueExpression<?> value)
    {
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

