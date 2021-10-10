package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract class BooleanExpression<B> extends ValueExpression<Boolean, B>
{
    public static final TypeToken<Boolean> BOOLEAN_TYPE_TOKEN = TypeToken.of(boolean.class);

    public BooleanExpression(CodeBlock<B,?,?> cb)
    {
        super(cb);
    }

    @Override
    public TypeToken<Boolean> effectiveType()
    {
        return BOOLEAN_TYPE_TOKEN;
    }

    public abstract void compile(MethodVisitor mv, @Nullable Label jumpFalse, @Nullable Label jumpTrue);
}
