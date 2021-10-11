package dev.gigaherz.jsonthings.codegen.codetree.impl;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;
import dev.gigaherz.jsonthings.codegen.codetree.expr.*;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Stack;

/**
 * @param <R> The return type of the code block
 */
@SuppressWarnings("UnstableApiUsage")
public class MethodImplementation<R>
{
    public final List<LocalVariable<?>> locals = Lists.newArrayList();
    public final List<StackEntry> stack = Lists.newArrayList();

    private final Stack<Integer>  currentStack = new Stack<>();
    public int maxStack = 0;

    private final MethodInfo<R> methodInfo;
    private final CodeBlock<R,Void,R> rootBlock;

    public int stackSize = 0;
    public int localsSize = 0;
    private Label firstLabel;

    public void pushStack(TypeToken<?> type)
    {
        pushStack(MethodImplementation.slotCount(type));
    }

    public void pushStack(int slots)
    {
        currentStack.push(slots);
        maxStack = Math.max(maxStack, currentStack.stream().mapToInt(i -> i).sum());
    }

    public void popStack()
    {
        currentStack.pop();
    }

    public Label makeLabel()
    {
        var l = firstLabel != null ? firstLabel : new Label();
        firstLabel = null;
        return l;
    }

    public MethodImplementation(MethodInfo<R> methodInfo, Label startLabel)
    {
        this.firstLabel = startLabel;
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

        rootBlock = new CodeBlock<>(this, null, methodInfo.returnType());
    }

    public MethodInfo<R> methodInfo()
    {
        return methodInfo;
    }

    private int makeLocal(int cLocal, TypeProxy<?> type, @Nullable String name)
    {
        return makeLocal(cLocal, type, type, name);
    }

    private int makeLocal(int cLocal, TypeProxy<?> type, TypeProxy<?> effectiveType, @Nullable String name)
    {
        int slotCount = slotCount(effectiveType);
        LocalVariable<?> local = new LocalVariable<>(cLocal, type, slotCount);
        if (name != null)
            local.name = name;
        locals.add(local);
        return slotCount;
    }

    public static int slotCount(TypeProxy<?> effectiveType)
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
        return slotCount;
    }

    public static int slotCount(TypeToken<?> effectiveType)
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
        return slotCount;
    }

    public static <R> MethodImplementation<R> begin(MethodInfo<R> methodInfo, Label startLabel)
    {
        return new MethodImplementation<>(methodInfo, startLabel);
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

    public LocalVariable<?> getLocalVariable(String localName)
    {
        return locals.stream().filter(local -> Objects.equal(local.name, localName)).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with name " + localName));
    }

    public LocalVariable<?> getLocalVariable(int localNumber)
    {
        return locals.stream().filter(local -> local.index == localNumber).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with index " + localNumber));
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

            boolean isInteger = isInteger(rs);

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

    public static boolean isInteger(TypeToken<?> tt)
    {
        return tt.isPrimitive() && isInteger(tt.getRawType());
    }

    public static boolean isInteger(Class<?> rs)
    {
        return rs == int.class || rs == byte.class || rs == short.class || rs == char.class;
    }

    public static boolean isFloat(TypeToken<?> tt)
    {
        return tt.isPrimitive() && isFloat(tt.getRawType());
    }

    public static boolean isFloat(Class<?> rs)
    {
        return rs == float.class;
    }

    public static boolean isDouble(TypeToken<?> tt)
    {
        return tt.isPrimitive() && isDouble(tt.getRawType());
    }

    public static boolean isDouble(Class<?> rs)
    {
        return rs == double.class;
    }

    public static boolean isLong(TypeToken<?> tt)
    {
        return tt.isPrimitive() && isLong(tt.getRawType());
    }

    public static boolean isLong(Class<?> rs)
    {
        return rs == long.class;
    }

    public static boolean isBoolean(TypeToken<?> tt)
    {
        return tt.isPrimitive() && isBoolean(tt.getRawType());
    }

    public static boolean isBoolean(Class<?> rs)
    {
        return rs == boolean.class;
    }

    public static boolean isVoid(TypeToken<?> tt)
    {
        return tt.getRawType() == void.class;
    }

    public <S,B> ValueExpression<?,B> applyAutomaticCasting(TypeToken<?> targetType, ValueExpression<S,B> value)
    {
        var rt = targetType.getRawType();
        var rs = value.effectiveType().getRawType();

        // numeric casting
        if (rt.isPrimitive() && rs.isPrimitive())
        {
            if ((rt == int.class && (rs == byte.class || rs == short.class || rs == char.class))
                    || (rt == short.class && rs == byte.class))
            {
                return new NoopConversion<>(value.block(), targetType, value);
            }

            boolean isInteger = isInteger(rs);

            if (rt == long.class && isInteger)
            {
                return new UnaryConversion<>(value.block(), targetType, Opcodes.I2L, value);
            }

            if (rt == float.class && isInteger)
            {
                return new UnaryConversion<>(value.block(), targetType, Opcodes.I2F, value);
            }

            if (rt == double.class && isInteger)
            {
                return new UnaryConversion<>(value.block(), targetType, Opcodes.I2D, value);
            }

            if (rt == double.class && rs == float.class)
            {
                return new UnaryConversion<>(value.block(), targetType, Opcodes.F2D, value);
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

    public CodeBlock<R,Void,R> rootBlock()
    {
        return rootBlock;
    }

}
