package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@SuppressWarnings("UnstableApiUsage")
public class AssignExpression<R, V> implements ValueExpression<R>
{
    LRef<R> target;
    ValueExpression<V> value;

    public AssignExpression(LRef<R> target, ValueExpression<V> value)
    {
        this.target = target;
        this.value = value;
    }

    @Override
    public TypeToken<R> effectiveType()
    {
        return target.targetType();
    }

    // TODO: special compile for inner assignments that need the value duplicated
    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        target.compileBefore(mv);

        value.compile(mv, true);

        if (needsResult) mv.visitInsn(Opcodes.DUP);

        target.compileAfter(mv);
    }
}
