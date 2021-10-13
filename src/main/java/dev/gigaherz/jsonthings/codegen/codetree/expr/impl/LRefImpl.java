package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.LRef;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.CodeBlockImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.ExprBase;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public abstract class LRefImpl<T, B> extends ExprBase<B> implements LRef<T>
{
    public LRefImpl(CodeBlockInternal<B,?,?> cb)
    {
        super(cb);
    }

}

