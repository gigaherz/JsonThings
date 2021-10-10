package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class SingleOpConversion<R, T, B> extends ValueExpression<R,B>
{
    private final TypeToken<R> targetType;
    private final int opcode;
    private final ValueExpression<T,B> value;

    public SingleOpConversion(CodeBlock<B,?,?> cb, TypeToken<R> targetType, int opcode, ValueExpression<T,B> value)
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
