package dev.gigaherz.jsonthings.codegen.codetree.expr;

import dev.gigaherz.jsonthings.codegen.codetree.impl.MethodImplementation;
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
    public void compile(MethodVisitor mv, @Nullable Label jumpTrue, @Nullable Label jumpFalse)
    {
        if (jumpFalse == null && jumpTrue == null)
            throw new IllegalStateException("Comparison compile called with both labels null");

        if(first instanceof BooleanExpression b1 && second instanceof BooleanExpression b2)
        {
            switch(comparisonType)
            {
                case AND -> {
                    boolean b = jumpFalse == null;
                    if (b) jumpFalse = new Label();
                    b1.compile(mv, null, jumpFalse);
                    b2.compile(mv, jumpTrue, jumpFalse);
                    if (b) mv.visitLabel(jumpFalse);
                }
                case OR -> {
                    boolean b = jumpTrue == null;
                    if (b) jumpTrue = new Label();
                    b1.compile(mv, jumpTrue, null);
                    b2.compile(mv, jumpTrue, jumpFalse);
                    if (b) mv.visitLabel(jumpTrue);
                }
                default -> throw new IllegalStateException("Cannot use GT/LT/GE/LE with non-numeric data types.");
            }
        }
        else if(MethodImplementation.isBoolean(first.effectiveType()))
        {
            switch(comparisonType)
            {
                case AND -> {
                    boolean b = jumpFalse == null;
                    if (b) jumpFalse = new Label();

                    first.compile(mv, true);
                    mv.visitJumpInsn(Opcodes.IFEQ, jumpFalse);

                    second.compile(mv, true);
                    mv.visitJumpInsn(Opcodes.IFEQ, jumpFalse);

                    if (jumpTrue != null)  mv.visitJumpInsn(Opcodes.GOTO, jumpTrue);

                    if (b) mv.visitLabel(jumpFalse);
                }
                case OR -> {
                    boolean b = jumpTrue == null;
                    if (b) jumpTrue = new Label();

                    first.compile(mv, true);
                    mv.visitJumpInsn(Opcodes.IFNE, jumpTrue);

                    second.compile(mv, true);
                    mv.visitJumpInsn(Opcodes.IFNE, jumpTrue);

                    if (jumpFalse != null) mv.visitJumpInsn(Opcodes.GOTO, jumpFalse);

                    if (b) mv.visitLabel(jumpTrue);
                }
                default -> throw new IllegalStateException("Cannot use GT/LT/GE/LE with non-numeric data types.");
            }
            mv.visitJumpInsn(Opcodes.GOTO, jumpFalse);
        }
        else
        {
            first.compile(mv, true);
            second.compile(mv, true);

            if (MethodImplementation.isInteger(first.effectiveType()))
            {
                if (jumpTrue == null)
                {
                    switch (comparisonType)
                    {
                        case GT -> mv.visitJumpInsn(Opcodes.IF_ICMPLE, jumpFalse);
                        case GE -> mv.visitJumpInsn(Opcodes.IF_ICMPLT, jumpFalse);
                        case LT -> mv.visitJumpInsn(Opcodes.IF_ICMPGE, jumpFalse);
                        case LE -> mv.visitJumpInsn(Opcodes.IF_ICMPGT, jumpFalse);
                        case EQ -> mv.visitJumpInsn(Opcodes.IF_ICMPNE, jumpFalse);
                        case NE -> mv.visitJumpInsn(Opcodes.IF_ICMPEQ, jumpFalse);
                        default -> throw new IllegalStateException("Cannot use boolean AND/OR with non-boolean data types.");
                    }
                }
                else
                {
                    switch (comparisonType)
                    {
                        case GT -> mv.visitJumpInsn(Opcodes.IF_ICMPGT, jumpTrue);
                        case GE -> mv.visitJumpInsn(Opcodes.IF_ICMPGE, jumpTrue);
                        case LT -> mv.visitJumpInsn(Opcodes.IF_ICMPLT, jumpTrue);
                        case LE -> mv.visitJumpInsn(Opcodes.IF_ICMPLE, jumpTrue);
                        case EQ -> mv.visitJumpInsn(Opcodes.IF_ICMPEQ, jumpTrue);
                        case NE -> mv.visitJumpInsn(Opcodes.IF_ICMPNE, jumpTrue);
                        default -> throw new IllegalStateException("Cannot use boolean AND/OR with non-boolean data types.");
                    }
                    if (jumpFalse != null) mv.visitJumpInsn(Opcodes.GOTO, jumpFalse);
                }
            }
            else if (MethodImplementation.isFloat(first.effectiveType()))
            {
                mv.visitInsn(Opcodes.LCMP);
                if (jumpTrue == null)
                {
                    switch (comparisonType)
                    {
                        case GT -> mv.visitJumpInsn(Opcodes.IFLE, jumpFalse);
                        case GE -> mv.visitJumpInsn(Opcodes.IFLT, jumpFalse);
                        case LT -> mv.visitJumpInsn(Opcodes.IFGE, jumpFalse);
                        case LE -> mv.visitJumpInsn(Opcodes.IFGT, jumpFalse);
                        case EQ -> mv.visitJumpInsn(Opcodes.IFNE, jumpFalse);
                        case NE -> mv.visitJumpInsn(Opcodes.IFEQ, jumpFalse);
                        default -> throw new IllegalStateException("Cannot use boolean AND/OR with non-boolean data types.");
                    }
                }
                else
                {
                    switch (comparisonType)
                    {
                        case GT -> mv.visitJumpInsn(Opcodes.IFGT, jumpTrue);
                        case GE -> mv.visitJumpInsn(Opcodes.IFGE, jumpTrue);
                        case LT -> mv.visitJumpInsn(Opcodes.IFLT, jumpTrue);
                        case LE -> mv.visitJumpInsn(Opcodes.IFLE, jumpTrue);
                        case EQ -> mv.visitJumpInsn(Opcodes.IFEQ, jumpTrue);
                        case NE -> mv.visitJumpInsn(Opcodes.IFNE, jumpTrue);
                        default -> throw new IllegalStateException("Cannot use boolean AND/OR with non-boolean data types.");
                    }
                    if (jumpFalse != null) mv.visitJumpInsn(Opcodes.GOTO, jumpFalse);
                }
            }
            else if (MethodImplementation.isFloat(first.effectiveType()))
            {
                mv.visitInsn(Opcodes.FCMPL);
                if (jumpTrue == null)
                {
                    switch (comparisonType)
                    {
                        case GT -> mv.visitJumpInsn(Opcodes.IFLE, jumpFalse);
                        case GE -> mv.visitJumpInsn(Opcodes.IFLT, jumpFalse);
                        case LT -> mv.visitJumpInsn(Opcodes.IFGE, jumpFalse);
                        case LE -> mv.visitJumpInsn(Opcodes.IFGT, jumpFalse);
                        case EQ -> mv.visitJumpInsn(Opcodes.IFNE, jumpFalse);
                        case NE -> mv.visitJumpInsn(Opcodes.IFEQ, jumpFalse);
                        default -> throw new IllegalStateException("Cannot use boolean AND/OR with non-boolean data types.");
                    }
                }
                else
                {
                    switch (comparisonType)
                    {
                        case GT -> mv.visitJumpInsn(Opcodes.IFGT, jumpTrue);
                        case GE -> mv.visitJumpInsn(Opcodes.IFGE, jumpTrue);
                        case LT -> mv.visitJumpInsn(Opcodes.IFLT, jumpTrue);
                        case LE -> mv.visitJumpInsn(Opcodes.IFLE, jumpTrue);
                        case EQ -> mv.visitJumpInsn(Opcodes.IFEQ, jumpTrue);
                        case NE -> mv.visitJumpInsn(Opcodes.IFNE, jumpTrue);
                        default -> throw new IllegalStateException("Cannot use boolean AND/OR with non-boolean data types.");
                    }
                    if (jumpFalse != null) mv.visitJumpInsn(Opcodes.GOTO, jumpFalse);
                }
            }
            else if (MethodImplementation.isDouble(first.effectiveType()))
            {
                mv.visitInsn(Opcodes.DCMPL);
                if (jumpTrue == null)
                {
                    switch (comparisonType)
                    {
                        case GT -> mv.visitJumpInsn(Opcodes.IFLE, jumpFalse);
                        case GE -> mv.visitJumpInsn(Opcodes.IFLT, jumpFalse);
                        case LT -> mv.visitJumpInsn(Opcodes.IFGE, jumpFalse);
                        case LE -> mv.visitJumpInsn(Opcodes.IFGT, jumpFalse);
                        case EQ -> mv.visitJumpInsn(Opcodes.IFNE, jumpFalse);
                        case NE -> mv.visitJumpInsn(Opcodes.IFEQ, jumpFalse);
                        default -> throw new IllegalStateException("Cannot use boolean AND/OR with non-boolean data types.");
                    }
                }
                else
                {
                    switch (comparisonType)
                    {
                        case GT -> mv.visitJumpInsn(Opcodes.IFGT, jumpTrue);
                        case GE -> mv.visitJumpInsn(Opcodes.IFGE, jumpTrue);
                        case LT -> mv.visitJumpInsn(Opcodes.IFLT, jumpTrue);
                        case LE -> mv.visitJumpInsn(Opcodes.IFLE, jumpTrue);
                        case EQ -> mv.visitJumpInsn(Opcodes.IFEQ, jumpTrue);
                        case NE -> mv.visitJumpInsn(Opcodes.IFNE, jumpTrue);
                        default -> throw new IllegalStateException("Cannot use boolean AND/OR with non-boolean data types.");
                    }
                    if (jumpFalse != null) mv.visitJumpInsn(Opcodes.GOTO, jumpFalse);
                }
            }
            else
            {
                if (jumpTrue == null)
                {
                    switch (comparisonType)
                    {
                        case EQ -> mv.visitJumpInsn(Opcodes.IF_ACMPNE, jumpFalse);
                        case NE -> mv.visitJumpInsn(Opcodes.IF_ACMPEQ, jumpFalse);
                        default -> throw new IllegalStateException("Cannot use GT/LT/GE/LE/AND/OR with non-numeric data types.");
                    }
                }
                else
                {
                    switch (comparisonType)
                    {
                        case EQ -> mv.visitJumpInsn(Opcodes.IF_ACMPEQ, jumpTrue);
                        case NE -> mv.visitJumpInsn(Opcodes.IF_ACMPNE, jumpTrue);
                        default -> throw new IllegalStateException("Cannot use GT/LT/GE/LE/AND/OR with non-numeric data types.");
                    }
                    if (jumpFalse != null) mv.visitJumpInsn(Opcodes.GOTO, jumpFalse);
                }
            }
        }
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


