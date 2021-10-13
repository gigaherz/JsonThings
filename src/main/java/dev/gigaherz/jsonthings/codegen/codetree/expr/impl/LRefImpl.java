package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.LRef;

@SuppressWarnings("UnstableApiUsage")
public abstract class LRefImpl<T, B> extends ExprBase<B> implements LRef<T>
{
    public LRefImpl(CodeBlockInternal<B, ?, ?> cb)
    {
        super(cb);
    }
}

