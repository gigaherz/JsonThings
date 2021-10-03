package dev.gigaherz.jsonthings.codegen.codetree;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ClassInfo<T>
{
    public final TypeToken<? super T> superClass;
    public final TypeToken<?> thisType;

    public final List<MethodInfo> constructors = Lists.newArrayList();
    public final List<MethodInfo> methods = Lists.newArrayList();
    public final List<FieldInfo> fields = Lists.newArrayList();

    private ClassInfo<? super T> superClassInfo;


    private ClassInfo(TypeToken<? super T> superClass, TypeToken<?> thisType)
    {
        this.superClass = superClass;
        this.thisType = thisType;
    }

    public FieldInfo getField(String fieldName)
    {
        return findField(fieldName).orElseThrow(() -> new IllegalStateException("No field found with name " + fieldName));
    }

    public Optional<FieldInfo> findField(String fieldName)
    {
        Optional<FieldInfo> first = fields.stream().filter(f -> Objects.equal(f.name, fieldName)).findFirst();
        if (first.isPresent())
            return first;
        if (superClassInfo == null)
        {
            if (superClass.getRawType() != Object.class)
            {
                superClassInfo = getSuperClassInfo(superClass);
            }
            else
            {
                return Optional.empty();
            }
        }
        return superClassInfo.findField(fieldName);
    }


    private static final Map<Class<?>, ClassInfo<?>> classInfoCache = new IdentityHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <C> ClassInfo<? super C> getSuperClassInfo(TypeToken<C> cls)
    {
        Class rawType = cls.getRawType();
        TypeToken<? super C> of = TypeToken.of(rawType);
        return (ClassInfo<? super C>) getSuperClassInfo(rawType, of);
    }

    public static <C> ClassInfo<? super C> getSuperClassInfo(Class<C> cls)
    {
        return getClassInfo(cls, TypeToken.of(cls));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <C> ClassInfo<? super C> getSuperClassInfo(Class<C> cls, TypeToken<C> clsToken)
    {
        Class superClass = cls.getSuperclass();
        TypeToken<? super C> superToken = clsToken.getSupertype(superClass);
        return getClassInfo(superClass, superToken);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <C> ClassInfo<C> getClassInfo(TypeToken<C> cls)
    {
        Class rawType = cls.getRawType();
        TypeToken<? super C> of = TypeToken.of(rawType);
        return getClassInfo(rawType, of);
    }

    public static <C> ClassInfo<C> getClassInfo(Class<C> cls)
    {
        return getClassInfo(cls, TypeToken.of(cls));
    }

    public static <C> ClassInfo<C> getClassInfo(Class<C> cls, TypeToken<C> clsToken)
    {
        Class<? super C> superClass = cls.getSuperclass();
        TypeToken<? super C> superToken = clsToken.getSupertype(superClass);
        ClassInfo<C> ci = new ClassInfo<>(superToken, clsToken);
        for (Constructor<?> cnt : superClass.getDeclaredConstructors())
        {
            MethodInfo mi = new MethodInfo();
            mi.name = cnt.getName();
            mi.modifiers = cnt.getModifiers();
            for (Parameter p : cnt.getParameters())
            {
                ParamInfo pi = new ParamInfo();
                pi.name = p.getName();
                pi.paramType = TypeToken.of(p.getParameterizedType());
                mi.params.add(pi);
            }
            ci.constructors.add(mi);
        }
        for (Method m : superClass.getDeclaredMethods())
        {
            MethodInfo mi = new MethodInfo();
            mi.name = m.getName();
            mi.modifiers = m.getModifiers();
            for (Parameter p : m.getParameters())
            {
                ParamInfo pi = new ParamInfo();
                pi.name = p.getName();
                pi.paramType = TypeToken.of(p.getParameterizedType());
                mi.params.add(pi);
            }
            ci.methods.add(mi);
        }
        for (Field f : superClass.getDeclaredFields())
        {
            FieldInfo fi = new FieldInfo();
            fi.name = f.getName();
            fi.modifiers = f.getModifiers();
            ci.fields.add(fi);
        }
        return ci;
    }
}
