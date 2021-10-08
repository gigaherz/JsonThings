package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

public class SingleOpConversion<R, T> implements ValueExpression<R>
{
    private final TypeToken<R> targetType;
    private final int opcode;
    private final ValueExpression<T> value;

    public SingleOpConversion(TypeToken<R> targetType, int opcode, ValueExpression<T> value)
    {
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
