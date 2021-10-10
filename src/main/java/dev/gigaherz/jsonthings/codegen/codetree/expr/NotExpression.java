package dev.gigaherz.jsonthings.codegen.codetree.expr;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;

public class NotExpression<B> extends BooleanExpression<B>
{
    private final ValueExpression<?,B> first;

    public NotExpression(CodeBlock<B,?,?> cb, ValueExpression<?,B> first)
    {
        super(cb);
        this.first = first;
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        if (needsResult)
        {
            first.compile(mv, true);

            cb.emitConditional(mv, first, () -> {
                mv.visitInsn(Opcodes.ICONST_0);
            }, () -> {
                mv.visitInsn(Opcodes.ICONST_1);
            });
        }
    }

    @Override
    public void compile(MethodVisitor mv, @Nullable Label jumpFalse, @Nullable Label jumpTrue)
    {
        if (jumpFalse == null && jumpTrue == null)
            throw new IllegalStateException("Comparison compile called with both labels null");
    }

    public enum ComparisonType
    {
        GT,
        GE,
        LT,
        LE,
        EQ,
        NE,
        AND,
        OR,
        NAND
    }
}
