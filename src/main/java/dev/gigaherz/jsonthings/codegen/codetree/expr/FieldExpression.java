package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.codetree.impl.FieldLoad;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class FieldExpression<T,B> extends ValueExpression<T,B>
{
    private final ValueExpression<?,B> objRef;
    private final FieldInfo<?> field;

    public FieldExpression(CodeBlock<B,?,?> cb, ValueExpression<?,B> objRef, FieldInfo<?> field)
    {
        super(cb);
        this.objRef = objRef;
        this.field = field;
    }

    @Override
    public TypeToken<T> effectiveType()
    {
        return (TypeToken)field.type();
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        if (needsResult) {
            objRef.compile(mv, true);
            FieldLoad.compile(field, mv);
        }
    }
}
