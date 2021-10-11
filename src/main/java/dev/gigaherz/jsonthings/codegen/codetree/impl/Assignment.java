package dev.gigaherz.jsonthings.codegen.codetree.impl;

import dev.gigaherz.jsonthings.codegen.codetree.expr.AssignExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class Assignment extends InstructionSource
{
    private final MethodImplementation<?> mi;
    private final AssignExpression<?, ?, ?> assignExpression;

    public Assignment(MethodImplementation<?> mi, AssignExpression<?, ?, ?> assignExpression)
    {
        this.mi = mi;
        this.assignExpression = assignExpression;
    }

    @Override
    public boolean compile(MethodVisitor mv, Label jumpEnd, boolean needsResult)
    {
        mv.visitLabel(mi.makeLabel());
        assignExpression.compile(mv, false);

        return false;
    }
}
