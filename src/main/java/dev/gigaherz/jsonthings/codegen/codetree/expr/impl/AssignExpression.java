package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.LRef;
import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;
import dev.gigaherz.jsonthings.codegen.codetree.impl.MethodImplementation;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@SuppressWarnings("UnstableApiUsage")
public class AssignExpression<T, S, B> extends ValueExpressionImpl<T, B>
{
    LRef<T> target;
    ValueExpression<S, B> value;

    public AssignExpression(CodeBlockInternal<B,?,?> cb, LRef<T> target, ValueExpression<S, B> value)
    {
        super(cb);
        this.target = target;
        this.value = value;
    }

    @Override
    public TypeToken<T> effectiveType()
    {
        return target.targetType();
    }

    // TODO: special compile for inner assignments that need the value duplicated
    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        target.compileBefore(mv);

        int valueSize = MethodImplementation.slotCount(value.effectiveType());

        cb.pushStack(valueSize);

        value.compile(mv, true);

        if (needsResult) {
            cb.pushStack(valueSize);
            mv.visitInsn(valueSize == 2 ? Opcodes.DUP2 : Opcodes.DUP);
        }

        target.compileAfter(mv);

        cb.popStack();
    }
}
