package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;
import dev.gigaherz.jsonthings.codegen.codetree.impl.InstructionSource;
import dev.gigaherz.jsonthings.codegen.codetree.impl.MethodImplementation;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public interface CodeBlock<B, P, M>
{
    TypeToken<B> returnType();

    List<InstructionSource> instructions();

    void returnVoid();

    <T> void returnVal(ValueExpression<T, M> value);

    CodeBlock<B, P, M> breakLoop();

    CodeBlock<B, P, M> continueLoop();

    <T> void breakVal(ValueExpression<?, M> value);

    CodeBlock<B, P, M> assign(LRef<?> target, ValueExpression<?, B> value);

    LRef<?> fieldRef(String fieldName);

    LRef<?> fieldRef(ValueExpression<?, B> objRef, String fieldName);

    <T> ValueExpression<T, B> field(String fieldName);

    ValueExpression<?, B> fieldOf(ValueExpression<?, B> objRef, String fieldName);

    ValueExpression<?, B> thisVar();

    ValueExpression<?, B> superVar();

    ValueExpression<?, B> localVar(String varName);

    CodeBlock<B, P, M> exec(ValueExpression<?, B> value);

    CodeBlock<B, P, M> superCall();

    @SuppressWarnings("unchecked")
    CodeBlock<B, P, M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B>... values);

    @SuppressWarnings("UnusedReturnValue")
    CodeBlock<B, P, M> ifElse(BooleanExpression<?> condition, Consumer<CodeBlock<B, B, M>> trueBranch, Consumer<CodeBlock<B, B, M>> falseBranch);

    @SuppressWarnings("unchecked")
    ValueExpression<?, B> methodCall(ValueExpression<?, B> objRef, String methodName, Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B>... values);

    @SuppressWarnings("unchecked")
    ValueExpression<?, B> methodCall(ValueExpression<?, B> objRef, String methodName, ValueExpression<?, B>... values);

    MethodLookup<?> method(String name);

    <C> ValueExpression<C, B> iif(BooleanExpression<B> condition, ValueExpression<C, B> trueBranch, ValueExpression<C, B> falseBranch);

    <T> ValueExpression<T, B> iif(BooleanExpression<B> condition, Consumer<CodeBlock<T, ?, M>> trueBranch, Consumer<CodeBlock<T, ?, M>> falseBranch);

    BooleanExpression<B> gt(ValueExpression<?, B> x, ValueExpression<?, B> y);

    BooleanExpression<B> ge(ValueExpression<?, B> x, ValueExpression<?, B> y);

    BooleanExpression<B> lt(ValueExpression<?, B> x, ValueExpression<?, B> y);

    BooleanExpression<B> le(ValueExpression<?, B> x, ValueExpression<?, B> y);

    BooleanExpression<B> eq(ValueExpression<?, B> x, ValueExpression<?, B> y);

    BooleanExpression<B> ne(ValueExpression<?, B> x, ValueExpression<?, B> y);

    BooleanExpression<B> and(ValueExpression<?, B> a, ValueExpression<?, B> b);

    BooleanExpression<B> or(ValueExpression<?, B> a, ValueExpression<?, B> b);

    BooleanExpression<B> not(ValueExpression<?, B> a);

    MethodImplementation<M> owner();
}
