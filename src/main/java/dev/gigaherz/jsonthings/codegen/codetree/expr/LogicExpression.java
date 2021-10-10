package dev.gigaherz.jsonthings.codegen.codetree.expr;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;

public class LogicExpression<B> extends BooleanExpression<B>
{
    private final ComparisonType comparisonType;
    private final ValueExpression<?,B> first;
    private final ValueExpression<?,B> second;

    public LogicExpression(CodeBlock<B,?,?> cb, ComparisonType comparisonType, ValueExpression<?,B> first, ValueExpression<?,B> second)
    {
        super(cb);
        this.comparisonType = comparisonType;
        this.first = first;
        this.second = second;
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        if (needsResult)
        {
            cb.emitComparison(mv, comparisonType, first, second, () -> {
                mv.visitInsn(Opcodes.ICONST_1);
            }, () -> {
                mv.visitInsn(Opcodes.ICONST_0);
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
        OR
    }
}


