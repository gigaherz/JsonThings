package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.CodeBlock;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class VarRef<T> implements LRef<T>
{
    private final CodeBlock.LocalVariable<T> localVariable;

    public VarRef(CodeBlock.LocalVariable<T> localVariable)
    {

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
        CodeBlock.LocalStore.compile(localVariable, mv);
    }
}
