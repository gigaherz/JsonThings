package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.NotImplementedException;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class CodeBlockExpression<B, P> extends ValueExpression<B, P>
{
    private final CodeBlock<B, P, ?> thisBlock;

    public CodeBlockExpression(CodeBlock<P, ?, ?> cb, CodeBlock<B, P, ?> childCb)
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
