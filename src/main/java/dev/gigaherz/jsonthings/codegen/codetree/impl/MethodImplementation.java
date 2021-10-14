package dev.gigaherz.jsonthings.codegen.codetree.impl;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;
import dev.gigaherz.jsonthings.codegen.codetree.ClassData;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;
import dev.gigaherz.jsonthings.codegen.codetree.expr.CodeBlockInternal;
import dev.gigaherz.jsonthings.codegen.codetree.expr.ValueExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.CodeBlockImpl;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.MethodCallExpression;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.NoopConversion;
import dev.gigaherz.jsonthings.codegen.codetree.expr.impl.UnaryConversion;
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

    private final Stack<Integer> currentStack = new Stack<>();
    public int maxStack = 0;

    private final MethodInfo<R> methodInfo;
    private final CodeBlockInternal<R, Void, R> rootBlock;

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

        rootBlock = new CodeBlockImpl<>(this, null, methodInfo.returnType());
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
            if (rs == boolean.class && rt == Boolean.class)
            {
                return targetType;
            }

            if (rs == byte.class && rt == Byte.class)
            {
                return targetType;
            }

            if (rs == int.class && rt == Integer.class)
            {
                return targetType;
            }

            if (rs == short.class && rt == Short.class)
            {
                return targetType;
            }

            if (rs == long.class && rt == Long.class)
            {
                return targetType;
            }

            if (rs == float.class && rt == Float.class)
            {
                return targetType;
            }

            if (rs == double.class && rt == Double.class)
            {
                return targetType;
            }

            if (rs == char.class && rt == Character.class)
            {
                return targetType;
            }
        }

        // unboxing
        if (rt.isPrimitive() && !rs.isPrimitive())
        {
            if (rt == boolean.class && rs == Boolean.class)
            {
                return targetType;
            }

            if (rt == byte.class && rs == Byte.class)
            {
                return targetType;
            }

            if (rt == int.class && rs == Integer.class)
            {
                return targetType;
            }

            if (rt == short.class && rs == Short.class)
            {
                return targetType;
            }

            if (rt == long.class && rs == Long.class)
            {
                return targetType;
            }

            if (rt == float.class && rs == Float.class)
            {
                return targetType;
            }

            if (rt == double.class && rs == Double.class)
            {
                return targetType;
            }

            if (rt == char.class && rs == Character.class)
            {
                return targetType;
            }
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

    public <S, B> ValueExpression<?, B> applyAutomaticCasting(TypeToken<?> targetType, ValueExpression<S, B> value)
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
            if (rs == boolean.class && rt == Boolean.class)
            {
                return makeBoxingConversion(Boolean.class, boolean.class, value);
            }

            if (rs == byte.class && rt == Byte.class)
            {
                return makeBoxingConversion(Byte.class, byte.class, value);
            }

            if (rs == int.class && rt == Integer.class)
            {
                return makeBoxingConversion(Integer.class, int.class, value);
            }

            if (rs == short.class && rt == Short.class)
            {
                return makeBoxingConversion(Short.class, short.class, value);
            }

            if (rs == long.class && rt == Long.class)
            {
                return makeBoxingConversion(Long.class, long.class, value);
            }

            if (rs == float.class && rt == Float.class)
            {
                return makeBoxingConversion(Float.class, float.class, value);
            }

            if (rs == double.class && rt == Double.class)
            {
                return makeBoxingConversion(Double.class, double.class, value);
            }

            if (rs == char.class && rt == Character.class)
            {
                return makeBoxingConversion(Character.class, char.class, value);
            }
        }

        // unboxing
        if (rt.isPrimitive() && !rs.isPrimitive())
        {
            if (rt == boolean.class && rs == Boolean.class)
            {
                return makeUnboxingConversion(Boolean.class, value, "booleanValue");
            }

            if (rt == byte.class && rs == Byte.class)
            {
                return makeUnboxingConversion(Byte.class, value, "byteValue");
            }

            if (rt == int.class && rs == Integer.class)
            {
                return makeUnboxingConversion(Integer.class, value, "intValue");
            }

            if (rt == short.class && rs == Short.class)
            {
                return makeUnboxingConversion(Short.class, value, "shortValue");
            }

            if (rt == long.class && rs == Long.class)
            {
                return makeUnboxingConversion(Long.class, value, "longValue");
            }

            if (rt == float.class && rs == Float.class)
            {
                return makeUnboxingConversion(Float.class, value, "floatValue");
            }

            if (rt == double.class && rs == Double.class)
            {
                return makeUnboxingConversion(Double.class, value, "doubleValue");
            }

            if (rt == char.class && rs == Character.class)
            {
                return makeUnboxingConversion(Character.class, value, "charValue");
            }
        }

        // no conversion found, return original.
        return value;
    }

    private <S, B> MethodCallExpression<?, B> makeUnboxingConversion(Class<S> source, ValueExpression<?, B> value, String name)
    {
        return new MethodCallExpression<>(value.block(), value, new MethodLookup<>(ClassData.getClassInfo(source), name).withParam(source).result(), List.of(value));
    }

    private <S, T, B> MethodCallExpression<T, B> makeBoxingConversion(Class<T> target, Class<S> source, ValueExpression<?, B> value)
    {
        return new MethodCallExpression<>(value.block(), null, new MethodLookup<>(ClassData.getClassInfo(target), "valueOf").withParam(source).result(), List.of(value));
    }

    public CodeBlockInternal<R, Void, R> rootBlock()
    {
        return rootBlock;
    }
}
