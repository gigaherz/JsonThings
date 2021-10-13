package dev.gigaherz.jsonthings.codegen.codetree.impl;

import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ExprBreak extends InstructionSource
{
    private final CodeBlockInternal<?, ?, ?> cb;

    private final ValueExpression<?, ?> value;

    public ExprBreak(CodeBlockInternal<?, ?, ?> cb, ValueExpression<?, ?> value)
    {
        this.cb = cb;
        this.value = value;
    }

    @Override
    public boolean compile(MethodVisitor mv, Label jumpEnd, boolean needsResult)
    {
        mv.visitLabel(cb.owner().makeLabel());
        value.compile(mv, needsResult);
        mv.visitJumpInsn(Opcodes.GOTO, jumpEnd);
        return true;
    }
}
