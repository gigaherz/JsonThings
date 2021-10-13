package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.BooleanExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.BooleanExpressionImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.CodeBlockImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.ValueExpressionImpl;
import dev.gigaherz.jsonthings.codegen.codetree.impl.MethodImplementation;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@SuppressWarnings("UnstableApiUsage")
public class ConditionalExpression<T, B> extends ValueExpressionImpl<T, B>
{
    private final BooleanExpression<B> condition;
    private final ValueExpression<T, B> trueBranch;
    private final ValueExpression<T, B> falseBranch;

    public ConditionalExpression(CodeBlockInternal<B,?,?> cb, BooleanExpression<B> condition, ValueExpression<T, B> trueBranch, ValueExpression<T, B> falseBranch)
    {
        super(cb);
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public TypeToken<T> effectiveType()
    {
        return trueBranch.effectiveType();
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        var jumpEnd = new Label();
        var jumpFalse = new Label();

        condition.compile(mv, null, jumpFalse);
        trueBranch.compile(mv, !MethodImplementation.isVoid(trueBranch.effectiveType()));
        mv.visitJumpInsn(Opcodes.GOTO, jumpEnd);
        cb.popStack();
        mv.visitLabel(jumpFalse);
        falseBranch.compile(mv, !MethodImplementation.isVoid(falseBranch.effectiveType()));
        mv.visitLabel(jumpEnd);
    }
}
