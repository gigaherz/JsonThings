package dev.gigaherz.jsonthings.codegen.codetree;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;
import dev.gigaherz.jsonthings.codegen.codetree.expr.*;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @param <R> The return type of the code block
 */
@SuppressWarnings("UnstableApiUsage")
public class CodeBlock<R>
{
    public final List<InstructionSource> instructions = Lists.newArrayList();
    public final List<LocalVariable<?>> locals = Lists.newArrayList();
    public final List<StackEntry> stack = Lists.newArrayList();

    private final MethodInfo<R> methodInfo;

    public int stackSize = 0;
    public int localsSize = 0;
    public Label startLabel = new Label();
    public Label firstLabel = startLabel;
    public Label endLabel = startLabel;

    protected void label(MethodVisitor mv)
    {
        if (firstLabel != null)
        {
            mv.visitLabel(firstLabel);
            firstLabel = null;
        }
        mv.visitLabel(endLabel = new Label());
    }

    public CodeBlock(MethodInfo<R> methodInfo)
    {
        this.methodInfo = methodInfo;

        localsSize = 0;

        if (!methodInfo.isStatic())
        {
            localsSize += makeLocal(0, methodInfo.owner().thisType(), TypeProxy.of(methodInfo.owner().superClass()), "this");
        }

        for (ParamInfo<?> f : methodInfo.params())
        {
            localsSize += makeLocal(localsSize, f.paramType(), f.name());
        }
    }

    private int makeLocal(int cLocal, TypeProxy<?> type, @Nullable String name)
    {
        return makeLocal(cLocal, type, type, name);
    }

    private int makeLocal(int cLocal, TypeProxy<?> type, TypeProxy<?> effectiveType, @Nullable String name)
    {
        int slotCount = 1;
        if (effectiveType.isPrimitive())
        {
            Class<?> rawType = effectiveType.getRawType();
            if (rawType == long.class)
            {
                slotCount = 2;
            }
            else if (rawType == double.class)
            {
                slotCount = 2;
            }
        }
        LocalVariable local = new LocalVariable(cLocal, type, slotCount);
        if (name != null)
            local.name = name;
        locals.add(local);
        return slotCount;
    }

    public static <R> CodeBlock<R> begin(MethodInfo<R> methodInfo)
    {
        return new CodeBlock<>(methodInfo);
    }
/*
    public void compile(MethodVisitor mv)
    {
        for (InstructionSource source : instructions)
        {
            source.compile(mv);
        }
    }
*/


    public CodeBlock<R> getThis()
    {
        instructions.add(new LocalLoad(0));
        return this;
    }

    public CodeBlock<R> getLocal(String localName)
    {
        instructions.add(new LocalLoad(localName));
        return this;
    }

    public CodeBlock<R> setLocal(String localName)
    {
        instructions.add(new LocalStore(localName));
        return this;
    }

    public CodeBlock<R> getField(String fieldName)
    {
        instructions.add(new FieldLoad(null, fieldName));
        return this;
    }

    public CodeBlock<R> setField(String fieldName)
    {
        instructions.add(new FieldStore(null, fieldName));
        return this;
    }

    public CodeBlock<R> returnVoid()
    {
        instructions.add(new Return(TypeToken.of(void.class)));
        return this;
    }

    public CodeBlock<R> returnInt()
    {
        instructions.add(new Return(TypeToken.of(int.class)));
        return this;
    }

    public CodeBlock<R> returnType(TypeToken<?> type)
    {
        instructions.add(new Return(type));
        return this;
    }

    public List<InstructionSource> instructions()
    {
        return instructions;
    }

    private LocalVariable<?> getLocalVariable(String localName)
    {
        return locals.stream().filter(local -> Objects.equal(local.name, localName)).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with name " + localName));
    }

    private LocalVariable<?> getLocalVariable(int localNumber)
    {
        return locals.stream().filter(local -> local.index == localNumber).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with index " + localNumber));
    }

    public LRef<?> fieldRef(String fieldName)
    {
        return new FieldRef<>(thisVar(), methodInfo.owner().getField(fieldName));
    }

    public LRef<?> fieldRef(ValueExpression<?> objRef, String fieldName)
    {
        return new FieldRef<>(objRef, methodInfo.owner().getField(fieldName));
    }

    public static LRef<?> fieldRef(ValueExpression<?> objRef, FieldInfo<?> fieldInfo)
    {
        return new FieldRef<>(objRef, fieldInfo);
    }

    public ValueExpression<?> field(String fieldName)
    {
        return new FieldExpression<>(thisVar(), methodInfo.owner().getField(fieldName));
    }

    public ValueExpression<?> fieldOf(ValueExpression<?> objRef, String fieldName)
    {
        return new FieldExpression<>(objRef, methodInfo.owner().getField(fieldName));
    }

    public ValueExpression<?> thisVar()
    {
        return new VarExpression<>(getLocalVariable(0));
    }

    public ValueExpression<?> superVar()
    {
        return new NoopConversion<>(methodInfo.owner().superClass(), new VarExpression<>(getLocalVariable(0)));
    }

    public VarExpression<?> localVar(String varName)
    {
        return new VarExpression<>(getLocalVariable(varName));
    }

    public CodeBlock<R> assign(LRef<?> target, ValueExpression<?> value)
    {
        value = applyAutomaticCasting(target.targetType(), value);
        if (target.targetType().isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new Assignment(new AssignExpression<>(target, value)));
            return this;
        }
        throw new IllegalStateException("Cannot assign field of type " + target.targetType() + " from expression of type " + value.effectiveType());
    }

    public void returnVal(ValueExpression<?> value)
    {
        value = applyAutomaticCasting(methodInfo.returnType(), value);
        if (methodInfo.returnType().isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new ExprReturn(methodInfo.returnType(), value));
        }
    }

    public static TypeToken<?> applyAutomaticCasting(TypeToken<?> targetType, TypeToken<?> valueType)
    {
        var rt = targetType.getRawType();
        var rs = valueType.getRawType();

        // numeric casting
        if (rt.isPrimitive() && rs.isPrimitive())
        {
            if ((rt == int.class && (rs == byte.class || rs == short.class || rs == char.class))
                    || (rt == short.class && rs == byte.class))
            {
                return targetType;
            }

            boolean isInteger = rs == int.class || rs == byte.class || rs == short.class || rs == char.class;

            if (rt == long.class && isInteger)
            {
                return targetType;
            }

            if (rt == float.class && isInteger)
            {
                return targetType;
            }

            if (rt == double.class && isInteger)
            {
                return targetType;
            }

            if (rt == double.class && rs == float.class)
            {
                return targetType;
            }
        }

        // boxing
        if (rs.isPrimitive() && !rt.isPrimitive())
        {
            // TODO: requires method calls
        }

        // unboxing
        if (rt.isPrimitive() && !rs.isPrimitive())
        {
            // TODO: requires method calls
        }

        // no conversion found, return original.
        return valueType;
    }

    public static ValueExpression<?> applyAutomaticCasting(TypeToken<?> targetType, ValueExpression<?> value)
    {
        var rt = targetType.getRawType();
        var rs = value.effectiveType().getRawType();

        // numeric casting
        if (rt.isPrimitive() && rs.isPrimitive())
        {
            if ((rt == int.class && (rs == byte.class || rs == short.class || rs == char.class))
                    || (rt == short.class && rs == byte.class))
            {
                return new NoopConversion<>(targetType, value);
            }

            boolean isInteger = rs == int.class || rs == byte.class || rs == short.class || rs == char.class;

            if (rt == long.class && isInteger)
            {
                return new SingleOpConversion<>(targetType, Opcodes.I2L, value);
            }

            if (rt == float.class && isInteger)
            {
                return new SingleOpConversion<>(targetType, Opcodes.I2F, value);
            }

            if (rt == double.class && isInteger)
            {
                return new SingleOpConversion<>(targetType, Opcodes.I2D, value);
            }

            if (rt == double.class && rs == float.class)
            {
                return new SingleOpConversion<>(targetType, Opcodes.F2D, value);
            }
        }

        // boxing
        if (rs.isPrimitive() && !rt.isPrimitive())
        {
            // TODO: requires method calls
        }

        // unboxing
        if (rt.isPrimitive() && !rs.isPrimitive())
        {
            // TODO: requires method calls
        }

        // no conversion found, return original.
        return value;
    }

    public CodeBlock<R> exec(ValueExpression<?> value)
    {
        instructions.add(new ExecuteExpression(value));
        return this;
    }

    public CodeBlock<R> superCall()
    {
        superCall(ml -> ml);
        return this;
    }

    public CodeBlock<R> superCall(Function<MethodLookup<?>, MethodLookup<?>> methodLookup, ValueExpression<?>... values)
    {
        var ml = new MethodLookup<>(methodInfo.owner().superClass(), "<init>");
        ml = methodLookup.apply(ml);
        superCall(ml.result(), values);
        return this;
    }

    public CodeBlock<R> superCall(MethodInfo<?> method, ValueExpression<?>... values)
    {
        if (!method.owner().thisType().actualType().equals(methodInfo.owner().superClass()))
            throw new IllegalStateException("Super call must be a method or constructor of the immediate super class of this class.");
        instructions.add(new SuperCall(methodCall(superVar(), method, values)));
        return this;
    }

    public static <R> MethodCallExpression<R> methodCall(ValueExpression<?> objRef, MethodInfo<R> method, ValueExpression<?>... values)
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
            var lVal = applyAutomaticCasting(param.paramType().actualType(), val);
            if (!param.paramType().actualType().isSupertypeOf(lVal.effectiveType()))
                throw new IllegalStateException("Param " + i + " cannot be converted from " + lVal.effectiveType() + " to " + param.paramType().actualType());
            if (lVal != val)
                lValues.set(i, lVal);
        }
        return new MethodCallExpression<>(objRef, method, lValues);
    }

    public MethodLookup<?> method(String name)
    {
        return new MethodLookup<>(methodInfo.owner(), name);
    }

    public abstract static class InstructionSource
    {
        public abstract void compile(MethodVisitor mv);
    }

    public class Assignment extends InstructionSource
    {
        private final AssignExpression<?, ?> assignExpression;

        public Assignment(AssignExpression<?, ?> assignExpression)
        {
            this.assignExpression = assignExpression;
        }

        @Override
        public void compile(MethodVisitor mv)
        {
            label(mv);
            assignExpression.compile(mv, false);
        }
    }

    public static class SuperCall extends ExecuteExpression
    {
        public SuperCall(MethodCallExpression<?> methodCall)
        {
            super(methodCall);
        }
    }

    public static class ExecuteExpression extends InstructionSource
    {
        private final ValueExpression<?> methodCall;

        public ExecuteExpression(ValueExpression<?> methodCall)
        {
            this.methodCall = methodCall;
        }

        @Override
        public void compile(MethodVisitor mv)
        {
            methodCall.compile(mv, false);
        }
    }

    public class ExprReturn extends InstructionSource
    {
        private final TypeToken<?> returnType;

        private final ValueExpression<?> value;

        public ExprReturn(TypeToken<?> returnType, ValueExpression<?> value)
        {
            this.returnType = returnType;
            this.value = value;
        }

        @Override
        public void compile(MethodVisitor mv)
        {
            label(mv);
            value.compile(mv, true);
            Return.compileReturn(returnType, mv);
        }
    }

    public class Return extends InstructionSource
    {
        private final TypeToken<?> returnType;

        public Return(TypeToken<?> returnType)
        {
            this.returnType = returnType;
        }

        @Override
        public void compile(MethodVisitor mv)
        {
            label(mv);
            compileReturn(returnType, mv);
        }

        public static void compileReturn(TypeToken<?> returnType, MethodVisitor mv)
        {
            Class<?> rawType = returnType.getRawType();
            if (rawType == void.class)
            {
                mv.visitInsn(Opcodes.RETURN);
            }
            else if (!returnType.isPrimitive())
            {
                mv.visitInsn(Opcodes.ARETURN);
            }
            else if (rawType == long.class)
            {
                mv.visitInsn(Opcodes.LRETURN);
            }
            else if (rawType == float.class)
            {
                mv.visitInsn(Opcodes.FRETURN);
            }
            else if (rawType == double.class)
            {
                mv.visitInsn(Opcodes.DRETURN);
            }
            else //if (type.getRawType() == int.class || type.getRawType() == short.class || type.getRawType() == byte.class || type.getRawType() == boolean.class)
            {
                mv.visitInsn(Opcodes.IRETURN);
            }
        }
    }

    public class LocalLoad extends InstructionSource
    {
        String localName;
        int localNumber;

        public LocalLoad(String localName)
        {
            this.localName = localName;
        }

        public LocalLoad(int localNumber)
        {
            this.localNumber = localNumber;
        }

        @Override
        public void compile(MethodVisitor mv)
        {
            label(mv);

            LocalVariable<?> localVariable;

            if (localName != null)
            {
                localVariable = getLocalVariable(localName);
                localNumber = localVariable.index;
            }
            else
            {
                int localNumber = this.localNumber;
                localVariable = getLocalVariable(localNumber);
            }
        }
        public static void compile(LocalVariable<?> localVariable, MethodVisitor mv)
        {
            if (!localVariable.variableType.isPrimitive())
            {
                mv.visitVarInsn(Opcodes.ALOAD, localVariable.index);
            }
            else
            {
                Class<?> rawType = localVariable.variableType.getRawType();
                if (rawType == long.class)
                {
                    mv.visitVarInsn(Opcodes.LLOAD, localVariable.index);
                }
                else if (rawType == float.class)
                {
                    mv.visitVarInsn(Opcodes.FLOAD, localVariable.index);
                }
                else if (rawType == double.class)
                {
                    mv.visitVarInsn(Opcodes.DLOAD, localVariable.index);
                }
                else //if (type.getRawType() == int.class || type.getRawType() == short.class || type.getRawType() == byte.class || type.getRawType() == boolean.class)
                {
                    mv.visitVarInsn(Opcodes.ILOAD, localVariable.index);
                }
            }
        }
    }

    public class LocalStore extends InstructionSource
    {
        String localName;
        int localNumber;

        public LocalStore(String localName)
        {
            this.localName = localName;
        }

        public LocalStore(int localNumber)
        {
            this.localNumber = localNumber;
        }

        @Override
        public void compile(MethodVisitor mv)
        {
            label(mv);

            LocalVariable<?> localVariable;

            if (localName != null)
            {
                localVariable = locals.stream().filter(local -> Objects.equal(local.name, localName)).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with name " + localName));
                localNumber = localVariable.index;
            }
            else
            {
                localVariable = locals.stream().filter(local -> local.index == localNumber).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with name " + localName));
            }

            compile(localVariable, mv);
        }

        public static void compile(LocalVariable<?> localVariable, MethodVisitor mv)
        {
            if (!localVariable.variableType.isPrimitive())
            {
                mv.visitVarInsn(Opcodes.ASTORE, localVariable.index);
            }
            else
            {
                Class<?> rawType = localVariable.variableType.getRawType();
                if (rawType == long.class)
                {
                    mv.visitVarInsn(Opcodes.LSTORE, localVariable.index);
                }
                else if (rawType == float.class)
                {
                    mv.visitVarInsn(Opcodes.FSTORE, localVariable.index);
                }
                else if (rawType == double.class)
                {
                    mv.visitVarInsn(Opcodes.DSTORE, localVariable.index);
                }
                else //if (type.getRawType() == int.class || type.getRawType() == short.class || type.getRawType() == byte.class || type.getRawType() == boolean.class)
                {
                    mv.visitVarInsn(Opcodes.ISTORE, localVariable.index);
                }
            }
        }
    }

    public class FieldLoad extends InstructionSource
    {
        private final String fieldName;
        private FieldInfo<?> fieldInfo;
        private final ClassInfo<?> owner;

        public FieldLoad(@Nullable ClassData<?> owner, String fieldName)
        {
            this.fieldName = fieldName;
            this.owner = owner != null ? owner : methodInfo.owner();
        }

        @Override
        public void compile(MethodVisitor mv)
        {
            label(mv);

            if (fieldInfo == null)
            {
                fieldInfo = owner.getField(fieldName);
            }

            compile(fieldInfo, mv);
        }

        public static void compile(FieldInfo<?> fieldInfo, MethodVisitor mv)
        {
            if ((fieldInfo.modifiers() & Opcodes.ACC_STATIC) == 0)
                mv.visitFieldInsn(Opcodes.GETFIELD, fieldInfo.owner().thisType().getInternalName(), fieldInfo.name(), TypeProxy.getTypeDescriptor(fieldInfo.type()));
            else
                mv.visitFieldInsn(Opcodes.GETSTATIC, fieldInfo.owner().thisType().getInternalName(), fieldInfo.name(), TypeProxy.getTypeDescriptor(fieldInfo.type()));
        }
    }

    public class FieldStore extends InstructionSource
    {
        private final String fieldName;
        private FieldInfo<?> fieldInfo;
        private final ClassInfo<?> owner;

        public FieldStore(@Nullable ClassData<?> owner, String fieldName)
        {
            this.fieldName = fieldName;
            this.owner = owner != null ? owner : methodInfo.owner();
        }

        @Override
        public void compile(MethodVisitor mv)
        {
            label(mv);

            if (fieldInfo == null)
            {
                fieldInfo = owner.getField(fieldName);
            }

            compile(fieldInfo, mv);
        }

        public static void compile(FieldInfo<?> fieldInfo, MethodVisitor mv)
        {
            if ((fieldInfo.modifiers() & Opcodes.ACC_STATIC) == 0)
                mv.visitFieldInsn(Opcodes.PUTFIELD, fieldInfo.owner().thisType().getInternalName(), fieldInfo.name(), TypeProxy.getTypeDescriptor(fieldInfo.type()));
            else
                mv.visitFieldInsn(Opcodes.PUTSTATIC, fieldInfo.owner().thisType().getInternalName(), fieldInfo.name(), TypeProxy.getTypeDescriptor(fieldInfo.type()));
        }
    }

    public static class LocalVariable<T>
    {
        public final int index;
        public final TypeProxy<T> variableType;
        public final int slotCount;

        @Nullable
        public String name;


        private LocalVariable(int index, TypeProxy<T> variableType, int slotCount)
        {
            this.index = index;
            this.variableType = variableType;
            this.slotCount = slotCount;
        }
    }

    public static class StackEntry
    {
    }

}
