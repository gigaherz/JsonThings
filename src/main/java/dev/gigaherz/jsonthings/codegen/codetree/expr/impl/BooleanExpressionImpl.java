package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.BooleanExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;

@SuppressWarnings("UnstableApiUsage")
public abstract class BooleanExpressionImpl<B> extends ValueExpressionImpl<Boolean, B> implements BooleanExpression<B>
{
    public static final TypeToken<Boolean> BOOLEAN_TYPE_TOKEN = TypeToken.of(boolean.class);

    public BooleanExpressionImpl(CodeBlockInternal<B, ?, ?> cb)
    {
        super(cb);
    }

    @Override
    public TypeToken<Boolean> effectiveType()
    {
        return BOOLEAN_TYPE_TOKEN;
    }
}
