package dev.gigaherz.jsonthings.codegen.type;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.codetree.ClassData;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public interface TypeProxy<T>
{
    static Token<?> of(Type type)
    {
        return new Token<>(TypeToken.of(type));
    }

    static <P> Token<P> of(Class<P> type)
    {
        return new Token<>(TypeToken.of(type));
    }

    static <P> Token<P> of(TypeToken<P> type)
    {
        return new Token<>(type);
    }

    TypeToken<T> actualType();

    String getSimpleName();

    String getInternalName();

    String getCanonicalName();

    ClassInfo<T> classInfo();

    default String getDescriptor()
    {
        var rt = getRawType();
        if (isPrimitive())
        {
            if (rt == int.class)
            {
                return "I";
            }
            else if (rt == boolean.class)
            {
                return "Z";
            }
            else if (rt == byte.class)
            {
                return "B";
            }
            else if (rt == char.class)
            {
                return "C";
            }
            else if (rt == short.class)
            {
                return "S";
            }
            else if (rt == long.class)
            {
                return "J";
            }
            else if (rt == float.class)
            {
                return "F";
            }
            else if (rt == double.class)
            {
                return "D";
            }
            else if (rt == void.class)
            {
                return "V";
            }
            else
            {
                throw new Error("unrecognized primitive type: " + rt);
            }
        }
        else if (isArray())
        {
            return Objects.requireNonNull(rt).getCanonicalName().replace('.', '/');
        }
        else
        {
            return "L" + getInternalName() + ";";
        }
    }

    @Nullable
    default String getSignature()
    {
        return null;
    }

    boolean isPrimitive();

    boolean isArray();

    boolean isInterface();

    @Nullable
    Class<?> getRawType();


    @Nullable
    static String getTypeSignature(TypeToken<?> type)
    {
        return TypeProxy.of(type).getSignature();
    }

    static String getTypeDescriptor(TypeToken<?> type)
    {
        return TypeProxy.of(type).getDescriptor();
    }

    @SuppressWarnings({"UnstableApiUsage", "ClassCanBeRecord"})
    class Token<T> implements TypeProxy<T>
    {
        private final TypeToken<T> type;

        private Token(TypeToken<T> type)
        {
            this.type = type;
        }

        @Override
        public TypeToken<T> actualType()
        {
            if (type == null)
                throw new IllegalStateException("Type has not been calculated yet.");
            return this.type;
        }

        @Override
        public String getSimpleName()
        {
            return type.getRawType().getSimpleName();
        }

        @Override
        public String getInternalName()
        {
            return getCanonicalName().replace(".", "/");
        }

        @Override
        public String getCanonicalName()
        {
            return actualType().getRawType().getCanonicalName();
        }

        @Override
        public ClassInfo<T> classInfo()
        {
            return ClassData.getClassInfo(type);
        }

        @Override
        public boolean isPrimitive()
        {
            return type.isPrimitive();
        }

        @Override
        public boolean isArray()
        {
            return type.isArray();
        }

        @Override
        public boolean isInterface()
        {
            return type.getRawType().isInterface();
        }

        @Override
        public Class<?> getRawType()
        {
            return type.getRawType();
        }

        @Override
        public String toString()
        {
            return type.toString();
        }
    }
}
