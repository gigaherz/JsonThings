package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public interface CodeBlockInternal<B, P, M> extends CodeBlock<B, P, M>
{
    LRef<?> fieldRef(ValueExpression<?, B> objRef, FieldInfo<?> fieldInfo);

    <T> ValueExpression<T, B> field(ValueExpression<?, B> objRef, FieldInfo<?> field);

    CodeBlock<B, P, M> superCall(MethodInfo<?> method, List<ValueExpression<?, B>> values);

    <R> ValueExpression<R, B> methodCall(ValueExpression<?, B> objRef, MethodInfo<R> method, List<ValueExpression<?, B>> values);

    void emitComparison(MethodVisitor mv, ComparisonType comparisonType, ValueExpression<?, B> first, ValueExpression<?, B> second, Runnable emitTrueBranch, Runnable emitFalseBranch);

    void emitConditional(MethodVisitor mv, ValueExpression<?, B> first, Runnable trueBranch, Runnable falseBranch);

    void popStack();

    void pushStack(int count);

    void compile(MethodVisitor mv, boolean needsResult);

    boolean compile(MethodVisitor mv, @Nullable Label jumpEnd);

    void pushStack(TypeToken<?> returnType);

    <T> CodeBlockInternal<T, B, M> childBlock();

    boolean isEmpty();
}
