package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.MethodImplementation;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class VarRef<T,B> extends LRef<T,B>
{
    private final MethodImplementation.LocalVariable<T> localVariable;

    public VarRef(CodeBlock<B,?,?> cb, MethodImplementation.LocalVariable<T> localVariable)
    {
        super(cb);

        this.localVariable = localVariable;
    }

    @Override
    public TypeToken<T> targetType()
    {
        return localVariable.variableType.actualType();
    }

    @Override
    public void compileBefore(MethodVisitor mv)
    {
        // nothing needed before
    }

    @Override
    public void compileAfter(MethodVisitor mv)
    {
        MethodImplementation.LocalStore.compile(localVariable, mv);
    }
}
