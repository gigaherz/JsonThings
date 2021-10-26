package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.FieldToken;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.MethodVisitor;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public interface ValueExpression<T, B> extends Expr<B>
{
    TypeToken<T> effectiveType();

    TypeProxy<T> proxyType();

    LRef<?> fieldRef(String fieldName);

    <T> ValueExpression<T, B> field(String fieldName);
    <T> ValueExpression<T, B> field(FieldToken<T> fieldToken);

    default <R> ValueExpression<R, B> methodCall(String methodName)
    {
        return methodCall(methodName, List.of());
    }

    default <R> ValueExpression<R, B> methodCall(String methodName, ValueExpression<?, B> val0)
    {
        return methodCall(methodName, List.of(val0));
    }

    default <R> ValueExpression<R, B> methodCall(String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return methodCall(methodName, List.of(val0, val1));
    }

    default <R> ValueExpression<R, B> methodCall(String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return methodCall(methodName, List.of(val0, val1, val2));
    }

    default <R> ValueExpression<R, B> methodCall(String methodName, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return methodCall(methodName, List.of(val0, val1, val2, val3));
    }

    <R> ValueExpression<R, B> methodCall(String methodName, List<ValueExpression<?, B>> values);

    default <R> ValueExpression<R, B> methodCall(String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup)
    {
        return methodCall(methodName, methodLookup, List.of());
    }

    default <R> ValueExpression<R, B> methodCall(String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0)
    {
        return methodCall(methodName, methodLookup, List.of(val0));
    }

    default <R> ValueExpression<R, B> methodCall(String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1)
    {
        return methodCall(methodName, methodLookup, List.of(val0, val1));
    }

    default <R> ValueExpression<R, B> methodCall(String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2)
    {
        return methodCall(methodName, methodLookup, List.of(val0, val1, val2));
    }

    default <R> ValueExpression<R, B> methodCall(String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, ValueExpression<?, B> val0, ValueExpression<?, B> val1, ValueExpression<?, B> val2, ValueExpression<?, B> val3)
    {
        return methodCall(methodName, methodLookup, List.of(val0, val1, val2, val3));
    }

    <R> ValueExpression<R, B> methodCall(String methodName, Function<MethodLookup<T>, MethodLookup<T>> methodLookup, List<ValueExpression<?, B>> values);

    void compile(MethodVisitor mv, boolean needsResult);
}
