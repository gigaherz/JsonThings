package dev.gigaherz.jsonthings.codegen.codetree.expr;

public class ExprBase<R>
{
    protected final CodeBlock<R,?,?> cb;

    public ExprBase(CodeBlock<R,?,?> cb)
    {
        this.cb = cb;
    }

    public CodeBlock<R, ?, ?> block()
    {
        return cb;
    }
}
