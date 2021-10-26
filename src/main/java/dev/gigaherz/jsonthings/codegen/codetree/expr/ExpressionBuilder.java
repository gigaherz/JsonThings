package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.FieldToken;
import dev.gigaherz.jsonthings.codegen.api.VarToken;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public interface ExpressionBuilder<B, M>
{
    <T> ValueExpression<T, B> field(String fieldName);
    <T> ValueExpression<T, B> field(FieldToken<T> fieldToken);

    ValueExpression<?, B> fieldOf(ValueExpression<?, B> objRef, String fieldName);

    ValueExpression<?, B> staticField(TypeToken<?> type, String fieldName);

    default ValueExpression<?, B> staticField(Class<?> type, String fieldName)
    {
        return staticField(TypeToken.of(type), fieldName);
    }

    ValueExpression<?, B> thisVar();

    ValueExpression<?, B> superVar();

    <T> ValueExpression<T, B> localVar(String varName);
    <T> ValueExpression<T, B> localVar(VarToken<T> varToken);

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName)
    {
        return methodCall(objRef, methodName, List.of());
    }

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, ValueExpression<?, B> val0)
    {
        return methodCall(objRef, methodName, List.of(val0));
    }

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return methodCall(objRef, methodName, List.of(val0, val1));
    }

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return methodCall(objRef, methodName, List.of(val0, val1, val2));
    }

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return methodCall(objRef, methodName, List.of(val0, val1, val2, val3));
    }

    <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, List<ValueExpression<?, B>> values);

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup)
    {
        return methodCall(objRef, methodName, methodLookup, List.of());
    }

    default  <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0)
    {
        return methodCall(objRef, methodName, methodLookup, List.of(val0));
    }

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return methodCall(objRef, methodName, methodLookup, List.of(val0, val1));
    }

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return methodCall(objRef, methodName, methodLookup, List.of(val0, val1, val2));
    }

    default <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return methodCall(objRef, methodName, methodLookup, List.of(val0, val1, val2, val3));
    }

    <R, T> ValueExpression<R, B> methodCall(ValueExpression<T, B> objRef, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, List<ValueExpression<?, B>> values);

    default <R> ValueExpression<R, B> staticCall(TypeToken<?> classToken, String methodName)
    {
        return staticCall(classToken, methodName, List.of());
    }

    default <R> ValueExpression<R, B> staticCall(TypeToken<?> classToken, String methodName, ValueExpression<?, B> val0)
    {
        return staticCall(classToken, methodName, List.of(val0));
    }

    default <R> ValueExpression<R, B> staticCall(TypeToken<?> classToken, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return staticCall(classToken, methodName, List.of(val0, val1));
    }

    default <R> ValueExpression<R, B> staticCall(TypeToken<?> classToken, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return staticCall(classToken, methodName, List.of(val0, val1, val2));
    }

    default <R> ValueExpression<R, B> staticCall(TypeToken<?> classToken, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return staticCall(classToken, methodName, List.of(val0, val1, val2, val3));
    }

    <R> ValueExpression<R, B> staticCall(TypeToken<?> classToken, String methodName, List<ValueExpression<?, B>> values);

    default <T> ValueExpression<?, B> staticCall(TypeToken<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup)
    {
        return staticCall(classToken, methodName, methodLookup, List.of());
    }

    default <R, T> ValueExpression<R, B> staticCall(TypeToken<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0)
    {
        return staticCall(classToken, methodName, methodLookup, List.of(val0));
    }

    default <R, T> ValueExpression<R, B> staticCall(TypeToken<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return staticCall(classToken, methodName, methodLookup, List.of(val0, val1));
    }

    default <R, T> ValueExpression<R, B> staticCall(TypeToken<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return staticCall(classToken, methodName, methodLookup, List.of(val0, val1, val2));
    }

    default <R, T> ValueExpression<R, B> staticCall(TypeToken<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return staticCall(classToken, methodName, methodLookup, List.of(val0, val1, val2, val3));
    }

    <R, T> ValueExpression<R, B> staticCall(TypeToken<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, List<ValueExpression<?, B>> values);

    default <R> ValueExpression<R, B> staticCall(Class<?> classToken, String methodName)
    {
        return staticCall(classToken, methodName, List.of());
    }

    default <R> ValueExpression<R, B> staticCall(Class<?> classToken, String methodName, ValueExpression<?, B> val0)
    {
        return staticCall(classToken, methodName, List.of(val0));
    }

    default <R> ValueExpression<R, B> staticCall(Class<?> classToken, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return staticCall(classToken, methodName, List.of(val0, val1));
    }

    default <R> ValueExpression<R, B> staticCall(Class<?> classToken, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return staticCall(classToken, methodName, List.of(val0, val1, val2));
    }

    default <R> ValueExpression<R, B> staticCall(Class<?> classToken, String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return staticCall(classToken, methodName, List.of(val0, val1, val2, val3));
    }

    default <R> ValueExpression<R, B> staticCall(Class<?> classToken, String methodName, List<ValueExpression<?, B>> values)
    {
        return this.staticCall(TypeToken.of(classToken), methodName, values);
    }

    default <R, T> ValueExpression<R, B> staticCall(Class<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup)
    {
        return staticCall(classToken, methodName, methodLookup, List.of());
    }

    default <R, T> ValueExpression<R, B> staticCall(Class<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0)
    {
        return staticCall(classToken, methodName, methodLookup, List.of(val0));
    }

    default <R, T> ValueExpression<R, B> staticCall(Class<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return staticCall(classToken, methodName, methodLookup, List.of(val0, val1));
    }

    default <R, T> ValueExpression<R, B> staticCall(Class<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return staticCall(classToken, methodName, methodLookup, List.of(val0, val1, val2));
    }

    default <R, T> ValueExpression<R, B> staticCall(Class<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return staticCall(classToken, methodName, methodLookup, List.of(val0, val1, val2, val3));
    }

    default <R, T> ValueExpression<R, B> staticCall(Class<T> classToken, String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, List<ValueExpression<?, B>> values)
    {
        return this.staticCall(TypeToken.of(classToken), methodName, methodLookup, values);
    }

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
}
