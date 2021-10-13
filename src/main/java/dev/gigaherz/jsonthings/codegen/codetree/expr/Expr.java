package dev.gigaherz.jsonthings.codegen.codetree.expr;

public interface Expr<B>
{
    CodeBlockInternal<B, ?, ?> block();
}
