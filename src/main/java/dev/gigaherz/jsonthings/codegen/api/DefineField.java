package dev.gigaherz.jsonthings.codegen.api;

import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;

public interface DefineField<C, F> extends FinishToClass<C>, Annotatable<DefineField<C, F>>
{
    // default package-private
    DefineField<C, F> setPublic();

    DefineField<C, F> setPrivate();

    DefineField<C, F> setProtected();

    // default instance
    DefineField<C, F> setStatic();

    // default non-final
    DefineField<C, F> setFinal();

    // default no initializer
    DefineField<C, F> initializer(ValueExpression<F,?> expr);
}
