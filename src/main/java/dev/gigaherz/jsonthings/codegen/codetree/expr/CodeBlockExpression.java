package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public class CodeBlockExpression<B, P> extends ValueExpression<B, P>
{
    private final CodeBlock<B, P, ?> cb;

    public CodeBlockExpression(CodeBlock<P, ?, ?> cb, CodeBlock<B, P, ?> childCb)
    {
        super(cb);
        this.cb = childCb;
    }

    @Override
    public TypeToken<B> effectiveType()
    {
        return cb.returnType();
    }

    @Override
    public void compile(MethodVisitor mv, boolean needsResult)
    {

    }
}
