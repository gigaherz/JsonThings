package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.codetree.CodeBlock;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class FieldExpression<T> implements ValueExpression<T>
{
    private final ValueExpression<?> objRef;
    private final FieldInfo<T> field;

    public FieldExpression(ValueExpression<?> objRef, FieldInfo<T> field)
    {
        this.objRef = objRef;
        this.field = field;
    }

    @Override
    public TypeToken<T> effectiveType()
    {
        return field.type();
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        if (needsResult) {
            objRef.compile(mv, true);
            CodeBlock.FieldLoad.compile(field, mv);
        }
    }
}
