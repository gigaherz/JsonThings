package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.VarToken;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;
import dev.gigaherz.jsonthings.codegen.codetree.impl.InstructionSource;
import dev.gigaherz.jsonthings.codegen.codetree.impl.MethodImplementation;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public interface CodeBlock<B, P, M> extends ExpressionBuilder<B, M>
{
    TypeToken<B> returnType();

    List<InstructionSource> instructions();

    CodeBlock<B, P, M> local(String name, TypeToken<?> varType);

    default <T> CodeBlock<B, P, M> local(VarToken<T> varToken)
    {
        return local(varToken.name(), varToken.type());
    }

    default CodeBlock<B, P, M> local(String name, Class<?> varType)
    {
        return local(name, TypeToken.of(varType));
    }

    void returnVoid();

    <T> void returnVal(ValueExpression<T, M> value);

    CodeBlock<B, P, M> breakLoop();

    CodeBlock<B, P, M> continueLoop();

    <T> void breakVal(ValueExpression<?, M> value);

    CodeBlock<B, P, M> assign(LRef<?> target, ValueExpression<?, B> value);

    LRef<?> fieldRef(String fieldName);

    LRef<?> fieldRef(ValueExpression<?, B> objRef, String fieldName);

    LRef<?> localRef(String localName);

    CodeBlock<B, P, M> exec(ValueExpression<?, B> value);

    CodeBlock<B, P, M> autoSuperCall();

    default CodeBlock<B, P, M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup)
    {
        return superCall(methodLookup, List.of());
    }

    default CodeBlock<B, P, M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B> val0)
    {
        return superCall(methodLookup, List.of(val0));
    }

    default CodeBlock<B, P, M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return superCall(methodLookup, List.of(val0, val1));
    }

    default CodeBlock<B, P, M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return superCall(methodLookup, List.of(val0, val1, val2));
    }

    default CodeBlock<B, P, M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return superCall(methodLookup, List.of(val0, val1, val2, val3));
    }

    @SuppressWarnings("unchecked")
    CodeBlock<B, P, M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, List<ValueExpression<?, B>> values);

    default CodeBlock<B, P, M> superCall()
    {
        return superCall(List.of());
    }

    default CodeBlock<B, P, M> superCall(ValueExpression<?, B> val0)
    {
        return superCall(List.of(val0));
    }

    default CodeBlock<B, P, M> superCall(ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return superCall(List.of(val0, val1));
    }

    default CodeBlock<B, P, M> superCall(ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return superCall(List.of(val0, val1, val2));
    }

    default CodeBlock<B, P, M> superCall(ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return superCall(List.of(val0, val1, val2, val3));
    }

    @SuppressWarnings("unchecked")
    CodeBlock<B, P, M> superCall(List<ValueExpression<?, B>> values);

    @SuppressWarnings("UnusedReturnValue")
    CodeBlock<B, P, M> ifElse(BooleanExpression<?> condition, Consumer<CodeBlock<B, B, M>> trueBranch, Consumer<CodeBlock<B, B, M>> falseBranch);

    CodeBlock<B, P, M> forLoop(String localName, TypeToken<?> varType, BooleanExpression<?> condition, ValueExpression<?, B> step, Consumer<CodeBlock<B, B, M>> body);

    default CodeBlock<B, P, M> forLoop(String localName, Class<?> varType, BooleanExpression<?> condition, ValueExpression<?, B> step, Consumer<CodeBlock<B, B, M>> body)
    {
        return forLoop(localName, TypeToken.of(varType), condition, step, body);
    }

    default CodeBlock<B, P, M> forLoop(VarToken<?> varType, BooleanExpression<?> condition, ValueExpression<?, B> step, Consumer<CodeBlock<B, B, M>> body)
    {
        return forLoop(varType.name(), varType.type(), condition, step, body);
    }

    <V, S extends V> CodeBlock<B, P, M> forEach(String localName, TypeToken<V> varType, ValueExpression<S, B> collection, Consumer<CodeBlock<B, B, M>> body);

    default <V, S extends V> CodeBlock<B, P, M> forEach(String localName, Class<V> varType, ValueExpression<S, B> collection, Consumer<CodeBlock<B, B, M>> body)
    {
        return forEach(localName, TypeToken.of(varType), collection, body);
    }

    default <V, S extends V> CodeBlock<B, P, M> forEach(VarToken<V> varToken, ValueExpression<S, B> collection, Consumer<CodeBlock<B, B, M>> body)
    {
        return forEach(varToken.name(), varToken.type(), collection, body);
    }

    <V, S extends V> CodeBlock<B, P, M> whileLoop(BooleanExpression<?> condition, Consumer<CodeBlock<B, B, M>> body);

    <V, S extends V> CodeBlock<B, P, M> doWhile(Consumer<CodeBlock<B, B, M>> body, BooleanExpression<?> condition);


    MethodLookup<?> method(String name);

    MethodImplementation<M> owner();
}
