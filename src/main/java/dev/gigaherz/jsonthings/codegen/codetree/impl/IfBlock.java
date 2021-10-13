package dev.gigaherz.jsonthings.codegen.codetree.impl;

import dev.gigaherz.jsonthings.codegen.codetree.expr.BooleanExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.BooleanExpressionImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlock;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.CodeBlockImpl;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

public class IfBlock<T, P, R> extends InstructionSource
{
    private final CodeBlockInternal<P, ?, R> cb;
    private final BooleanExpression<?> condition;
    private final Consumer<CodeBlock<T, P, R>> trueBranch;
    private final Consumer<CodeBlock<T, P, R>> falseBranch;

    public IfBlock(CodeBlockInternal<P, ?, R> cb, BooleanExpression<?> condition, Consumer<CodeBlock<T, P, R>> trueBranch, Consumer<CodeBlock<T, P, R>> falseBranch)
    {
        this.cb = cb;
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public boolean compile(MethodVisitor mv, Label jumpEnd, boolean needsResult)
    {
        mv.visitLabel(cb.owner().makeLabel());

        var tb = cb.<T>childBlock();
        var fb = cb.<T>childBlock();

        trueBranch.accept(tb);
        falseBranch.accept(fb);

        var jumpFalse = cb.owner().makeLabel();

        var b = (jumpEnd == null);
        if (b) jumpEnd = cb.owner().makeLabel();

        condition.compile(mv, null, jumpFalse);
        boolean tr = tb.compile(mv, null);
        if (tr) mv.visitJumpInsn(Opcodes.GOTO, jumpEnd);
        mv.visitLabel(jumpFalse);
        boolean fr = fb.compile(mv, jumpEnd);
        if (b) mv.visitLabel(jumpEnd);

        return (!tr && !fr) || (tb.isEmpty() && !fr ) || (fb.isEmpty() && !tr );
    }
}
