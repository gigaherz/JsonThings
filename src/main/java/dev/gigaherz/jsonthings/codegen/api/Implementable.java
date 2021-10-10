package dev.gigaherz.jsonthings.codegen.api;

import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlock;

import java.util.function.Consumer;

public interface Implementable<C, R> extends FinishToClass<C>
{
    // make abstract (and finish the method definition)
    DefineClass<C> makeAbstract();

    DefineClass<C> implementation(Consumer<CodeBlock<R,?,R>> code);
}
