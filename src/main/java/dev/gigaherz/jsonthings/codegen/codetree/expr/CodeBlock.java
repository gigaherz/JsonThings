package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;
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
public class CodeBlock<B,P,M>
{
    @Nullable
    private final CodeBlock<P, ?, M> parentBlock;
    private TypeToken<?> returnType;
    private final MethodImplementation<M> owner;
    private final List<InstructionSource> instructions = Lists.newArrayList();

    public CodeBlock(MethodImplementation<M> owner, CodeBlock<P,?,M> parentBlock)
    {
        this.owner = owner;
        this.parentBlock = parentBlock;
    }

    public CodeBlock(MethodImplementation<M> owner, CodeBlock<P,?,M> parentBlock, TypeToken<B> returnType)
    {
        this(owner, parentBlock);
        this.returnType = returnType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public TypeToken<B> returnType()
    {
        return (TypeToken)returnType;
    }

    void setReturnType(TypeToken<B> rt)
    {
        this.returnType = rt;
    }

    public boolean compile(MethodVisitor mv, @Nullable Label jumpEnd)
    {
        for(var insn : instructions)
        {
            if (insn.compile(mv, jumpEnd))
                return false;
        }
        return true;
    }

    public boolean isEmpty()
    {
        return instructions.size() == 0;
    }

    public List<InstructionSource> instructions()
    {
        return instructions;
    }

    public CodeBlock<B,P,M> getThis()
    {
        instructions.add(new LocalLoad(owner, 0));
        return this;
    }

    public CodeBlock<B,P,M> getLocal(String localName)
    {
        instructions.add(new LocalLoad(owner, localName));
        return this;
    }

    public CodeBlock<B,P,M> setLocal(String localName)
    {
        instructions.add(new LocalStore(owner, localName));
        return this;
    }

    public CodeBlock<B,P,M> getField(String fieldName)
    {
        instructions.add(new FieldLoad(owner, null, fieldName));
        return this;
    }

    public CodeBlock<B,P,M> setField(String fieldName)
    {
        instructions.add(new FieldStore(owner, null, fieldName));
        return this;
    }

    public CodeBlock<B,P,M> returnVoid()
    {
        instructions.add(new Return(this, TypeToken.of(void.class)));
        return this;
    }

    public CodeBlock<B,P,M> returnInt()
    {
        instructions.add(new Return(this, TypeToken.of(int.class)));
        return this;
    }

    public CodeBlock<B,P,M> returnType(TypeToken<?> type)
    {
        instructions.add(new Return(this, type));
        return this;
    }

    public CodeBlock<B,P,M> assign(LRef<?,B> target, ValueExpression<?, B> value)
    {
        value = owner.applyAutomaticCasting(target.targetType(), value);
        if (target.targetType().isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new Assignment(owner, new AssignExpression<>(this, target, value)));
            return this;
        }
        throw new IllegalStateException("Cannot assign field of type " + target.targetType() + " from expression of type " + value.effectiveType());
    }

    public LRef<?,B> fieldRef(String fieldName)
    {
        return new FieldRef<>(this, thisVar(), owner.methodInfo().owner().getField(fieldName));
    }

    public LRef<?,B> fieldRef(ValueExpression<?, B> objRef, String fieldName)
    {
        return new FieldRef<>(this, objRef, owner.methodInfo().owner().getField(fieldName));
    }

    public LRef<?,B> fieldRef(ValueExpression<?, B> objRef, FieldInfo<?> fieldInfo)
    {
        return new FieldRef<>(this, objRef, fieldInfo);
    }

    public ValueExpression<?, B> field(String fieldName)
    {
        return field(thisVar(), owner.methodInfo().owner().getField(fieldName));
    }

    public <T> ValueExpression<T, B> field(ValueExpression<?, B> objRef, FieldInfo<?> field)
    {
        return new FieldExpression<>(this, objRef, field);
    }

    public ValueExpression<?, B> fieldOf(ValueExpression<?, B> objRef, String fieldName)
    {
        return new FieldExpression<>(this, objRef, owner.methodInfo().owner().getField(fieldName));
    }

    public ValueExpression<?, B> thisVar()
    {
        return new VarExpression<>(this, owner.getLocalVariable(0));
    }

    public ValueExpression<?, B> superVar()
    {
        return new NoopConversion<>(this, owner.methodInfo().owner().superClass(), new VarExpression<>(this, owner.getLocalVariable(0)));
    }

    public VarExpression<?, B> localVar(String varName)
    {
        return new VarExpression<>(this, owner.getLocalVariable(varName));
    }

    public <T> void returnVal(ValueExpression<?, M> value)
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
        if (owner.methodInfo().returnType().isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new ExprReturn(this, owner.methodInfo().returnType(), nValue));
        }
    }

    public CodeBlock<B,P,M> exec(ValueExpression<?, B> value)
    {
        instructions.add(new ExecuteExpression(owner, value));
        return this;
    }

    public CodeBlock<B,P,M> superCall()
    {
        return superCall(ml -> ml);
    }

    @SafeVarargs
    public final CodeBlock<B,P,M> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?, B>... values)
    {
        var ml = new MethodLookup<>(owner.methodInfo().owner().superClass(), "<init>");
        ml = methodLookup.apply(ml);
        return superCall(ml.result(), values);
    }

    @SafeVarargs
    public final CodeBlock<B,P,M> superCall(MethodInfo<?> method, ValueExpression<?, B>... values)
    {
        if (!method.owner().thisType().actualType().equals(owner.methodInfo().owner().superClass()))
            throw new IllegalStateException("Super call must be a method or constructor of the immediate super class of this class.");
        instructions.add(new SuperCall(owner, methodCall(superVar(), method, values)));
        return this;
    }

    @SafeVarargs
    public final <R> MethodCallExpression<R,B> methodCall(ValueExpression<?, B> objRef, MethodInfo<R> method, ValueExpression<?, B>... values)
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

    public MethodLookup<?> method(String name)
    {
        return new MethodLookup<>(owner.methodInfo().owner(), name);
    }

    public void emitComparison(MethodVisitor mv, LogicExpression.ComparisonType comparisonType, ValueExpression<?, B> first, ValueExpression<?, B> second, Runnable emitTrueBranch, Runnable emitFalseBranch)
    {

    }

    public void emitConditional(MethodVisitor mv, ValueExpression<?, B> first, Runnable trueBranch, Runnable falseBranch)
    {

    }

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

    public BooleanExpression<B> and(ValueExpression<?, B> a, ValueExpression<?, B> b)
    {
        if (!BooleanExpression.BOOLEAN_TYPE_TOKEN.equals(a.effectiveType())
                || !BooleanExpression.BOOLEAN_TYPE_TOKEN.equals(b.effectiveType()))
            throw new IllegalStateException("Operator and requires two boolean parameters, found " + a.effectiveType() + " to " + b.effectiveType());

        return new LogicExpression<>(this, LogicExpression.ComparisonType.AND, a, b);
    }

    public <C> ValueExpression<C, B> iif(BooleanExpression<B> condition, ValueExpression<C, B> trueBranch, ValueExpression<C, B> falseBranch)
    {
        return new ConditionalExpression<>(this, condition, trueBranch, falseBranch);
    }

    public <T> ValueExpression<T,B> iif(BooleanExpression<B> condition, Consumer<CodeBlock<T,?,M>> trueBranch, Consumer<CodeBlock<T,?,M>> falseBranch)
    {
        var tb = this.<T>childBlock();
        var fb = this.<T>childBlock();

        trueBranch.accept(tb);
        falseBranch.accept(fb);

        return new ConditionalExpression<>(this, condition, new CodeBlockExpression<>(this, tb), new CodeBlockExpression<>(this, fb));
    }

    public <X> CodeBlock<X, B, M> childBlock()
    {
        return new CodeBlock<>(owner, this);
    }

    public CodeBlock<B,P,M> ifElse(BooleanExpression<?> condition, Consumer<CodeBlock<B,B,M>> trueBranch, Consumer<CodeBlock<B,B,M>> falseBranch)
    {
        instructions.add(new IfBlock<>(this, condition, trueBranch, falseBranch));
        return this;
    }

    public MethodImplementation<M> owner()
    {
        return owner;
    }
}

