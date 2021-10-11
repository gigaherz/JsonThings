package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@SuppressWarnings("UnstableApiUsage")
public class UnaryOperator<R, B> extends ValueExpression<R,B>
{
    private final int opcode;
    private final ValueExpression<R,B> value;

    public UnaryOperator(CodeBlock<B,?,?> cb, int opcode, ValueExpression<R,B> value)
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
