package dev.gigaherz.jsonthings.codegen.api;

import dev.gigaherz.jsonthings.codegen.codetree.CodeBlock;

import java.util.function.Function;

public interface Implementable<C, M> extends FinishToClass<C>
{
    // make abstract (and finish the method definition)
    DefineClass<C> makeAbstract();

    DefineClass<C> implementation(Function<M, CodeBlock> code);
}
