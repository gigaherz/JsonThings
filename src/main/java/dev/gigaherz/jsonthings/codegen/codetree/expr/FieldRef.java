package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.codetree.CodeBlock;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class FieldRef<T> implements LRef<T>
{
    private final ValueExpression<?> objRef;
    private final FieldInfo<T> field;

    public FieldRef(ValueExpression<?> objRef, FieldInfo<T> field)
    {
        this.objRef = objRef;
        this.field = field;
    }

    @Override
    public TypeToken<T> targetType()
    {
        return field.type();
    }

    public FieldInfo<T> getField()
    {
        return field;
    }

    @Override
    public void compileBefore(MethodVisitor mv)
    {
        objRef.compile(mv, true);
    }

    @Override
    public void compileAfter(MethodVisitor mv)
    {
        CodeBlock.FieldStore.compile(field, mv);
    }
}
