package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class MethodCallExpression<R,B> extends ValueExpression<R,B>
{
    @Nullable
    private final ValueExpression<?,B> objRef;
    private final MethodInfo<R> method;
    private final List<ValueExpression<?,B>> lValues;

    public MethodCallExpression(CodeBlock<B,?,?> cb, @Nullable ValueExpression<?,B> objRef, MethodInfo<R> method, List<ValueExpression<?,B>> lValues)
    {
        super(cb);
        this.objRef = objRef;
        this.method = method;
        this.lValues = lValues;
    }

    @Override
    public TypeToken<R> effectiveType()
    {
        return method.returnType();
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        if(method.isStatic())
        {
            lValues.forEach(val -> val.compile(mv, true));
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, method.owner().thisType().getInternalName(), method.name(), method.getDescriptor(), method.owner().thisType().isInterface());
        }
        else if (method.name().equals("<init>"))
        {
            Objects.requireNonNull(objRef).compile(mv, true);
            lValues.forEach(val -> val.compile(mv, true));
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, method.owner().thisType().getInternalName(), method.name(), method.getDescriptor(), method.owner().thisType().isInterface());
        }
        else
        {
            Objects.requireNonNull(objRef).compile(mv, true);
            lValues.forEach(val -> val.compile(mv, true));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, method.owner().thisType().getInternalName(), method.name(), method.getDescriptor(), method.owner().thisType().isInterface());
        }
    }
}
