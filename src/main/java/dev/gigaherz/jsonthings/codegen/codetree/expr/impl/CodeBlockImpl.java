package dev.gigaherz.jsonthings.codegen.codetree.expr.impl;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;
import dev.gigaherz.jsonthings.codegen.codetree.expr.*;
import dev.gigaherz.jsonthings.codegen.codetree.impl.*;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class CodeBlockImpl<B,P,M> implements CodeBlockInternal<B, P, M>
{
    @Nullable
    private final CodeBlock<P, ?, M> parentBlock;
    private TypeToken<?> returnType;
    private final MethodImplementation<M> owner;
    private final List<InstructionSource> instructions = Lists.newArrayList();

    public CodeBlockImpl(MethodImplementation<M> owner, @Nullable CodeBlock<P,?,M> parentBlock)
    {
        this.owner = owner;
        this.parentBlock = parentBlock;
    }

    public CodeBlockImpl(MethodImplementation<M> owner, @Nullable CodeBlock<P,?,M> parentBlock, TypeToken<B> returnType)
    {
        this(owner, parentBlock);
        this.returnType = returnType;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TypeToken<B> returnType()
    {
        return (TypeToken)returnType;
    }

    public boolean compile(MethodVisitor mv, @Nullable Label jumpEnd)
    {
        int last = instructions.size() - 1;
        for (int i = 0; i <= last; i++)
        {
            InstructionSource insn = instructions.get(i);
            if (insn.compile(mv, i == last ? jumpEnd : null, false))
                return false;
        }
        return true;
    }

    public void compile(MethodVisitor mv, boolean needsResult)
    {
        var jumpEnd = new Label();
        int last = instructions.size() - 1;
        for (int i = 0; i <= last; i++)
        {
            InstructionSource insn = instructions.get(i);
            if (insn.compile(mv, i == last ? jumpEnd : null, needsResult))
                break;
        }
        mv.visitLabel(jumpEnd);
    }

    public boolean isEmpty()
    {
        return instructions.size() == 0;
    }

    @Override
    public List<InstructionSource> instructions()
    {
        return instructions;
    }

    public void pushStack(TypeToken<?> type)
    {
        owner.pushStack(type);
    }

    public void pushStack(int slots)
    {
        owner.pushStack(slots);
    }
    public void popStack()
    {
        owner.popStack();
    }

    public CodeBlockInternal<B,P,M> getThis()
    {
        instructions.add(new LocalLoad(owner, 0));
        return this;
    }

    public CodeBlockInternal<B,P,M> getLocal(String localName)
    {
        instructions.add(new LocalLoad(owner, localName));
        return this;
    }

    public CodeBlockInternal<B,P,M> setLocal(String localName)
    {
        instructions.add(new LocalStore(owner, localName));
        return this;
    }

    public CodeBlockInternal<B,P,M> getField(String fieldName)
    {
        instructions.add(new FieldLoad(owner, null, fieldName));
        return this;
    }

    public CodeBlockInternal<B,P,M> setField(String fieldName)
    {
        instructions.add(new FieldStore(owner, null, fieldName));
        return this;
    }

    @Override
    public void returnVoid()
    {
        instructions.add(new Return(this, TypeToken.of(void.class)));
    }

    public void returnInt()
    {
        instructions.add(new Return(this, TypeToken.of(int.class)));
    }

    public void returnType(TypeToken<?> type)
    {
        instructions.add(new Return(this, type));
    }

    @Override
    public <T> void returnVal(ValueExpression<T, M> value)
    {
        ValueExpression<?, M> nValue = owner.applyAutomaticCasting(owner.methodInfo().returnType(), value);
        if (owner.methodInfo().returnType().isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new ExprReturn(this, owner.methodInfo().returnType(), nValue));
        }
    }

    @Override
    public CodeBlock<B,P,M> breakLoop()
    {
        instructions.add(new SkipLoop(true));
        return this;
    }

    @Override
    public CodeBlock<B,P,M> continueLoop()
    {
        instructions.add(new SkipLoop(false));
        return this;
    }

    @Override
    public <T> void breakVal(ValueExpression<?, M> value)
    {
        ValueExpression<?, M> nValue = value;
        if (returnType == null)
        {
            returnType = nValue.effectiveType();
        }
        else
        {
            nValue = owner.applyAutomaticCasting(returnType, nValue);
        }
        if (returnType.isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new ExprBreak(this, nValue));
        }
    }

    @Override
    public CodeBlock<B,P,M> assign(LRef<?> target, ValueExpression<?, B> value)
    {
        value = owner.applyAutomaticCasting(target.targetType(), value);
        if (target.targetType().isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new Assignment(owner, new AssignExpression<>(this, target, value)));
            return this;
        }
        throw new IllegalStateException("Cannot assign field of type " + target.targetType() + " from expression of type " + value.effectiveType());
    }

    @Override
    public LRef<?> fieldRef(String fieldName)
    {
        return new FieldRef<>(this, thisVar(), owner.methodInfo().owner().getField(fieldName));
    }

    @Override
    public LRef<?> fieldRef(ValueExpression<?, B> objRef, String fieldName)
    {
        return new FieldRef<>(this, objRef, owner.methodInfo().owner().getField(fieldName));
    }

    @Override
    public LRef<?> fieldRef(ValueExpression<?, B> objRef, FieldInfo<?> fieldInfo)
    {
        return new FieldRef<>(this, objRef, fieldInfo);
    }

    @Override
    public <T> ValueExpression<T, B> field(String fieldName)
    {
        return field(thisVar(), owner.methodInfo().owner().getField(fieldName));
    }

    @Override
    public <T> ValueExpression<T, B> field(ValueExpression<?, B> objRef, FieldInfo<?> field)
    {
        return new FieldExpression<>(this, objRef, field);
    }

    @Override
    public ValueExpression<?, B> fieldOf(ValueExpression<?, B> objRef, String fieldName)
    {
        return new FieldExpression<>(this, objRef, owner.methodInfo().owner().getField(fieldName));
    }

    @Override
    public ValueExpression<?, B> thisVar()
    {
        return new VarExpression<>(this, owner.getLocalVariable(0));
    }

    @Override
    public ValueExpression<?, B> superVar()
    {
        return new NoopConversion<>(this, owner.methodInfo().owner().superClass(), new VarExpression<>(this, owner.getLocalVariable(0)));
    }

    @Override
    public VarExpression<?, B> localVar(String varName)
    {
        return new VarExpression<>(this, owner.getLocalVariable(varName));
    }

    @Override
    public CodeBlock<B,P,M> exec(ValueExpression<?, B> value)
    {
        instructions.add(new ExecuteExpression(owner, value));
        return this;
    }

    @Override
    public CodeBlock<B,P,M> superCall()
    {
        return superCall(ml -> ml);
    }

    @Override
    @SafeVarargs
    public final CodeBlock<B,P,M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B>... values)
    {
        var ml = new MethodLookup<>(owner.methodInfo().owner().superClass(), "<init>");
        ml = methodLookup.apply(ml);
        return superCall(ml.result(), values);
    }

    @Override
    @SafeVarargs
    public final CodeBlock<B,P,M> superCall(MethodInfo<?> method, ValueExpression<?, B>... values)
    {
        if (!method.owner().thisType().actualType().equals(owner.methodInfo().owner().superClass()))
            throw new IllegalStateException("Super call must be a method or constructor of the immediate super class of this class.");
        instructions.add(new SuperCall(owner, methodCall(superVar(), method, values)));
        return this;
    }

    @Override
    @SafeVarargs
    public final ValueExpression<?,B> methodCall(ValueExpression<?, B> objRef, String methodName, Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B>... values)
    {
        var ml = new MethodLookup<>(objRef.effectiveType(), methodName);
        ml = methodLookup.apply(ml);
        return methodCall(objRef, ml.result(), values);
    }

    @Override
    @SafeVarargs
    public final ValueExpression<?,B> methodCall(ValueExpression<?, B> objRef, String methodName, ValueExpression<?, B>... values)
    {
        var ml = new MethodLookup<>(objRef.effectiveType(), methodName);
        for(var expr : values)
            ml = ml.withParam(expr.effectiveType());
        return methodCall(objRef, ml.result(), values);
    }

    @Override
    @SafeVarargs
    public final <R> ValueExpression<R,B> methodCall(ValueExpression<?, B> objRef, MethodInfo<R> method, ValueExpression<?, B>... values)
    {
        List<? extends ParamInfo<?>> params = method.params();
        var lValues = Arrays.stream(values).collect(Collectors.toList());
        if (params.size() != values.length)
            throw new IllegalStateException("Mismatched set of values. Expected: " + params.stream().map(ParamInfo::paramType).toList()
                    + "; Received: " +  lValues.stream().map(ValueExpression::effectiveType).toList());
        for(int i = 0; i< params.size(); i++)
        {
            var param = params.get(i);
            var val = lValues.get(i);
            var lVal = owner.applyAutomaticCasting(param.paramType().actualType(), val);
            if (!param.paramType().actualType().isSupertypeOf(lVal.effectiveType()))
                throw new IllegalStateException("Param " + i + " cannot be converted from " + lVal.effectiveType() + " to " + param.paramType().actualType());
            if (lVal != val)
                lValues.set(i, lVal);
        }
        return new MethodCallExpression<>(this, objRef, method, lValues);
    }

    @Override
    public MethodLookup<?> method(String name)
    {
        return new MethodLookup<>(owner.methodInfo().owner(), name);
    }

    @Override
    public void emitComparison(MethodVisitor mv, LogicExpression.ComparisonType comparisonType, ValueExpression<?, B> first, ValueExpression<?, B> second, Runnable emitTrueBranch, Runnable emitFalseBranch)
    {

    }

    @Override
    public void emitConditional(MethodVisitor mv, ValueExpression<?, B> first, Runnable trueBranch, Runnable falseBranch)
    {

    }

    @Override
    public BooleanExpression<B> gt(ValueExpression<?, B> x, ValueExpression<?, B> y)
    {
        if (!x.effectiveType().equals(y.effectiveType()))
        {
            y = owner.applyAutomaticCasting(x.effectiveType(), y);
            if (!x.effectiveType().equals(y.effectiveType()))
            {
                x = owner.applyAutomaticCasting(y.effectiveType(), x);
                if (!x.effectiveType().equals(y.effectiveType()))
                    throw new IllegalStateException("Cannot compare " + x.effectiveType() + " to " + y.effectiveType());
            }
        }

        return new LogicExpression<>(this, LogicExpression.ComparisonType.GT, x, y);
    }

    @Override
    public BooleanExpression<B> ge(ValueExpression<?, B> x, ValueExpression<?, B> y)
    {
        if (!x.effectiveType().equals(y.effectiveType()))
        {
            y = owner.applyAutomaticCasting(x.effectiveType(), y);
            if (!x.effectiveType().equals(y.effectiveType()))
            {
                x = owner.applyAutomaticCasting(y.effectiveType(), x);
                if (!x.effectiveType().equals(y.effectiveType()))
                    throw new IllegalStateException("Cannot compare " + x.effectiveType() + " to " + y.effectiveType());
            }
        }

        return new LogicExpression<>(this, LogicExpression.ComparisonType.GE, x, y);
    }

    @Override
    public BooleanExpression<B> lt(ValueExpression<?, B> x, ValueExpression<?, B> y)
    {
        if (!x.effectiveType().equals(y.effectiveType()))
        {
            y = owner.applyAutomaticCasting(x.effectiveType(), y);
            if (!x.effectiveType().equals(y.effectiveType()))
            {
                x = owner.applyAutomaticCasting(y.effectiveType(), x);
                if (!x.effectiveType().equals(y.effectiveType()))
                    throw new IllegalStateException("Cannot compare " + x.effectiveType() + " to " + y.effectiveType());
            }
        }

        return new LogicExpression<>(this, LogicExpression.ComparisonType.LT, x, y);
    }

    @Override
    public BooleanExpression<B> le(ValueExpression<?, B> x, ValueExpression<?, B> y)
    {
        if (!x.effectiveType().equals(y.effectiveType()))
        {
            y = owner.applyAutomaticCasting(x.effectiveType(), y);
            if (!x.effectiveType().equals(y.effectiveType()))
            {
                x = owner.applyAutomaticCasting(y.effectiveType(), x);
                if (!x.effectiveType().equals(y.effectiveType()))
                    throw new IllegalStateException("Cannot compare " + x.effectiveType() + " to " + y.effectiveType());
            }
        }

        return new LogicExpression<>(this, LogicExpression.ComparisonType.LE, x, y);
    }

    @Override
    public BooleanExpression<B> eq(ValueExpression<?, B> x, ValueExpression<?, B> y)
    {
        if (!x.effectiveType().equals(y.effectiveType()))
        {
            y = owner.applyAutomaticCasting(x.effectiveType(), y);
            if (!x.effectiveType().equals(y.effectiveType()))
            {
                x = owner.applyAutomaticCasting(y.effectiveType(), x);
                if (!x.effectiveType().equals(y.effectiveType()))
                    throw new IllegalStateException("Cannot compare " + x.effectiveType() + " to " + y.effectiveType());
            }
        }

        return new LogicExpression<>(this, LogicExpression.ComparisonType.EQ, x, y);
    }

    @Override
    public BooleanExpression<B> ne(ValueExpression<?, B> x, ValueExpression<?, B> y)
    {
        if (!x.effectiveType().equals(y.effectiveType()))
        {
            y = owner.applyAutomaticCasting(x.effectiveType(), y);
            if (!x.effectiveType().equals(y.effectiveType()))
            {
                x = owner.applyAutomaticCasting(y.effectiveType(), x);
                if (!x.effectiveType().equals(y.effectiveType()))
                    throw new IllegalStateException("Cannot compare " + x.effectiveType() + " to " + y.effectiveType());
            }
        }

        return new LogicExpression<>(this, LogicExpression.ComparisonType.NE, x, y);
    }

    @Override
    public BooleanExpression<B> and(ValueExpression<?, B> a, ValueExpression<?, B> b)
    {
        if (!BooleanExpressionImpl.BOOLEAN_TYPE_TOKEN.equals(a.effectiveType())
                || !BooleanExpressionImpl.BOOLEAN_TYPE_TOKEN.equals(b.effectiveType()))
            throw new IllegalStateException("Operator AND requires two boolean parameters, found " + a.effectiveType() + " and " + b.effectiveType());

        return new LogicExpression<>(this, LogicExpression.ComparisonType.AND, a, b);
    }

    @Override
    public BooleanExpression<B> or(ValueExpression<?, B> a, ValueExpression<?, B> b)
    {
        if (!BooleanExpressionImpl.BOOLEAN_TYPE_TOKEN.equals(a.effectiveType())
                || !BooleanExpressionImpl.BOOLEAN_TYPE_TOKEN.equals(b.effectiveType()))
            throw new IllegalStateException("Operator OR requires two boolean parameters, found " + a.effectiveType() + " and " + b.effectiveType());

        return new LogicExpression<>(this, LogicExpression.ComparisonType.OR, a, b);
    }

    @Override
    public BooleanExpression<B> not(ValueExpression<?, B> a)
    {
        if (!BooleanExpressionImpl.BOOLEAN_TYPE_TOKEN.equals(a.effectiveType()))
            throw new IllegalStateException("Operator NOT requires a boolean parameters, found " + a.effectiveType());

        return new NotExpression<>(this, a);
    }

    @Override
    public <C> ValueExpression<C, B> iif(BooleanExpression<B> condition, ValueExpression<C, B> trueBranch, ValueExpression<C, B> falseBranch)
    {
        return new ConditionalExpression<>(this, condition, trueBranch, falseBranch);
    }

    @Override
    public <T> ValueExpression<T,B> iif(BooleanExpression<B> condition, Consumer<CodeBlock<T, ?, M>> trueBranch, Consumer<CodeBlock<T, ?, M>> falseBranch)
    {
        var tb = this.<T>childBlock();
        var fb = this.<T>childBlock();

        trueBranch.accept(tb);
        falseBranch.accept(fb);

        return new ConditionalExpression<>(this, condition, new CodeBlockExpression<>(this, tb), new CodeBlockExpression<>(this, fb));
    }

    public <X> CodeBlockImpl<X, B, M> childBlock()
    {
        return new CodeBlockImpl<>(owner, this);
    }

    @Override
    public CodeBlock<B,P,M> ifElse(BooleanExpression<?> condition, Consumer<CodeBlock<B, B, M>> trueBranch, Consumer<CodeBlock<B, B, M>> falseBranch)
    {
        instructions.add(new IfBlock<>(this, condition, trueBranch, falseBranch));
        return this;
    }

    public MethodImplementation<M> owner()
    {
        return owner;
    }
}

