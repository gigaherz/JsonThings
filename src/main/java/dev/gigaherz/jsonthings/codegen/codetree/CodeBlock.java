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
import java.util.List;

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

        /*if (!methodInfo.isStatic())
        {
            localsSize += makeLocal(0, methodInfo.owner().thisType(), TypeProxy.of(methodInfo.owner().superClass()), "this");
        }*/

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

    public FieldRef<?> fieldRef(String fieldName)
    {
        return new FieldRef<>(thisVar(), methodInfo.owner().getField(fieldName));
    }

    public FieldRef<?> fieldRef(ValueExpression<?> objRef, String fieldName)
    {
        return new FieldRef<>(objRef, methodInfo.owner().getField(fieldName));
    }

    public FieldExpression<?> field(String fieldName)
    {
        return new FieldExpression<>(thisVar(), methodInfo.owner().getField(fieldName));
    }

    public FieldExpression<?> fieldOf(ValueExpression<?> objRef, String fieldName)
    {
        return new FieldExpression<>(objRef, methodInfo.owner().getField(fieldName));
    }

    public VarExpression<?> thisVar()
    {
        return new VarExpression<>(getLocalVariable(0));
    }

    public VarExpression<?> localVar(String varName)
    {
        return new VarExpression<>(getLocalVariable(varName));
    }

    public CodeBlock<R> assign(FieldRef<?> target, ValueExpression<?> value)
    {
        // TODO: value = applyAutomaticCasting(target, value);
        if (target.targetType().isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new Assignment(new AssignExpression<>(target, value)));
            return this;
        }
        throw new IllegalStateException("Cannot assign field of type " + target.targetType() + " from expression of type " + value.effectiveType());
    }

    public void returnVal(ValueExpression<?> value)
    {
        // TODO: value = applyAutomaticCasting(target, value);
        if (methodInfo.returnType().isSupertypeOf(value.effectiveType()))
        {
            instructions.add(new ExprReturn(methodInfo.returnType(), value));
        }
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

    public static class SuperCall extends MethodCall
    {

    }

    public static class MethodCall extends InstructionSource
    {

        @Override
        public void compile(MethodVisitor mv)
        {
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
            mv.visitFieldInsn(Opcodes.GETFIELD, fieldInfo.owner().thisType().getInternalName(), fieldInfo.name(), TypeProxy.getTypeDescriptor(fieldInfo.type()));
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
            mv.visitFieldInsn(Opcodes.PUTFIELD, fieldInfo.owner().thisType().getInternalName(), fieldInfo.name(), TypeProxy.getTypeDescriptor(fieldInfo.type()));
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
