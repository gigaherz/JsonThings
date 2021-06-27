package gigaherz.jsonthings.codegen.codetree;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class CodeBlock
{
    private final List<InstructionSource> instructions = Lists.newArrayList();
    private final List<LocalVariable> locals = Lists.newArrayList();
    private final List<StackEntry> stack = Lists.newArrayList();

    private final MethodInfo methodInfo;

    public CodeBlock(MethodInfo methodInfo)
    {
        this.methodInfo = methodInfo;

        int cLocal = 0;

        if (!methodInfo.isStatic())
        {
            cLocal += makeLocal(0, methodInfo.owner.thisType, methodInfo.owner.superClass, "this");
        }

        for(ParamInfo f : methodInfo.params)
        {
            cLocal += makeLocal(cLocal, f.paramType, f.name);
        }
    }

    private int makeLocal(int cLocal, TypeToken<?> type, @Nullable String name)
    {
        return makeLocal(cLocal, type, type, name);
    }
    private int makeLocal(int cLocal, TypeToken<?> type, TypeToken<?> effectiveType, @Nullable String name)
    {
        int slotCount = 1;
        Class<?> rawType = effectiveType.getRawType();
        if (rawType == long.class)
        {
            slotCount = 2;
        }
        else if (rawType == double.class)
        {
            slotCount = 2;
        }
        LocalVariable local = new LocalVariable(cLocal, type, slotCount);
        if (name != null)
            local.name = name;
        locals.add(local);
        return slotCount;
    }

    public static CodeBlock begin(MethodInfo methodInfo)
    {
        return new CodeBlock(methodInfo);
    }

    public Stream<AbstractInsnNode> compile()
    {
        Stream<AbstractInsnNode> instructionStream = Stream.empty();
        for(InstructionSource source : instructions)
        {
            instructionStream = Stream.concat(instructionStream, source.compile());
        }
        return instructionStream;
    }

    public CodeBlock getLocal(String localName)
    {
        instructions.add(new LocalLoad(localName));
        return this;
    }

    public CodeBlock setField(String fieldName)
    {
        instructions.add(new FieldStore(fieldName));
        return this;
    }

    public CodeBlock returnVoid()
    {
        instructions.add(new Return(TypeToken.of(void.class)));
        return this;
    }

    public CodeBlock returnInt()
    {
        instructions.add(new Return(TypeToken.of(int.class)));
        return this;
    }

    public CodeBlock returnType(TypeToken<?> type)
    {
        instructions.add(new Return(type));
        return this;
    }

    public CodeBlock getField(String x)
    {
        return null;
    }

    private abstract static class InstructionSource
    {
        public abstract Stream<AbstractInsnNode> compile();
    }

    private class LocalLoad extends InstructionSource
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
        public Stream<AbstractInsnNode> compile()
        {
            LocalVariable localVariable;
            if (localName != null)
            {
                localVariable = locals.stream().filter(local -> Objects.equal(local.name, localName)).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with name " + localName));
                localNumber = localVariable.index;
            }
            else
            {
                localVariable = locals.stream().filter(local -> local.index == localNumber).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with name " + localName));
            }
            return Stream.of(
                    getLoadFromType(localVariable.variableType, localVariable.index)
            );
        }

        private AbstractInsnNode getLoadFromType(TypeToken<?> type, int localNumber)
        {
            if (!type.isPrimitive())
            {
                return new VarInsnNode(Opcodes.ALOAD, localNumber);
            }
            else
            {
                Class<?> rawType = type.getRawType();
                if (rawType == long.class)
                {
                    return new VarInsnNode(Opcodes.LLOAD, localNumber);
                }
                else if (rawType == float.class)
                {
                    return new VarInsnNode(Opcodes.FLOAD, localNumber);
                }
                else if (rawType == double.class)
                {
                    return new VarInsnNode(Opcodes.DLOAD, localNumber);
                }
                else //if (type.getRawType() == int.class || type.getRawType() == short.class || type.getRawType() == byte.class || type.getRawType() == boolean.class)
                {
                    return new VarInsnNode(Opcodes.ILOAD, localNumber);
                }
            }
        }
    }

    private class Return extends InstructionSource
    {
        private final TypeToken<?> returnType;

        public Return(TypeToken<?> returnType)
        {
            this.returnType = returnType;
        }

        @Override
        public Stream<AbstractInsnNode> compile()
        {
            return Stream.of(
                    getReturnFromType(returnType)
            );
        }

        private AbstractInsnNode getReturnFromType(TypeToken<?> type)
        {
            Class<?> rawType = type.getRawType();
            if (rawType == void.class)
            {
                return new InsnNode(Opcodes.RETURN);
            }
            else if (!type.isPrimitive())
            {
                return new InsnNode(Opcodes.ARETURN);
            }
            else if (rawType == long.class)
            {
                return new InsnNode(Opcodes.LRETURN);
            }
            else if (rawType == float.class)
            {
                return new InsnNode(Opcodes.FRETURN);
            }
            else if (rawType == double.class)
            {
                return new InsnNode(Opcodes.DRETURN);
            }
            else //if (type.getRawType() == int.class || type.getRawType() == short.class || type.getRawType() == byte.class || type.getRawType() == boolean.class)
            {
                return new InsnNode(Opcodes.IRETURN);
            }
        }
    }

    private class FieldStore extends InstructionSource
    {
        private final String fieldName;
        private FieldInfo fieldInfo;

        public FieldStore(String fieldName)
        {
            this.fieldName = fieldName;
        }

        @Override
        public Stream<AbstractInsnNode> compile()
        {
            if (fieldInfo == null)
            {
                fieldInfo = methodInfo.owner.getField(fieldName);
            }

            return Stream.of(
                    //getStoreFromType()
            );
        }
    }

    private class LocalVariable
    {
        public final int index;
        public final TypeToken<?> variableType;
        public final int slotCount;

        @Nullable
        public String name;



        private LocalVariable(int index, TypeToken<?> variableType, int slotCount)
        {
            this.index = index;
            this.variableType = variableType;
            this.slotCount = slotCount;
        }
    }

    private class StackEntry
    {
    }
}
