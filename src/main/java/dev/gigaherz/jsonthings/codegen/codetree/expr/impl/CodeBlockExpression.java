package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class CodeBlockExpression<B, P> extends ValueExpressionImpl<B, P>
{
    private final CodeBlockInternal<B, P, ?> thisBlock;

    public CodeBlockExpression(CodeBlockInternal<P, ?, ?> cb, CodeBlockInternal<B, P, ?> childCb)
    {
        super(cb);
        this.thisBlock = childCb;
    }

    @Override
    public TypeToken<B> effectiveType()
    {
        return thisBlock.returnType();
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {
        thisBlock.compile(mv, needsResult);
    }
}
