package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class ConditionalExpression<T, B> extends ValueExpression<T, B>
{
    private final BooleanExpression condition;
    private final ValueExpression<T, B> trueBranch;
    private final ValueExpression<T, B> falseBranch;

    public ConditionalExpression(CodeBlock<B,?,?> cb, BooleanExpression condition, ValueExpression<T, B> trueBranch, ValueExpression<T, B> falseBranch)
    {
        super(cb);
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public TypeToken<T> effectiveType()
    {
        return null;
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {

    }
}
