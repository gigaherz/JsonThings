package dev.gigaherz.jsonthings.codegen.codetree;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ClassData<T> implements ClassInfo<T>
{
    public final TypeToken<? super T> superClass;
    public final TypeToken<?> thisType;

    public final List<MethodInfo<?>> constructors = Lists.newArrayList();
    public final List<MethodInfo<?>> methods = Lists.newArrayList();
    public final List<FieldInfo<?>> fields = Lists.newArrayList();

    private ClassData<? super T> superClassInfo;


    private ClassData(TypeToken<? super T> superClass, TypeToken<?> thisType)
    {
        this.superClass = superClass;
        this.thisType = thisType;
    }

    @Override
    public TypeToken<? super T> superClass()
    {
        return this.superClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TypeProxy<T> thisType()
    {
        return (TypeProxy<T>)TypeProxy.of(this.thisType);
    }

    @Override
    public List<dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo<?>> constructors()
    {
        return this.constructors;
    }

    @Override
    public List<dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo<?>> methods()
    {
        return this.methods;
    }

    @Override
    public List<dev.gigaherz.jsonthings.codegen.api.codetree.info.FieldInfo<?>> fields()
    {
        return this.fields;
    }

    @Override
    public ClassInfo<? super T> superClassInfo()
    {
        if (this.superClassInfo == null)
        {
            this.superClassInfo = getSuperClassInfo(this.superClass);
        }
        return this.superClassInfo;
    }

    public Optional<FieldInfo<?>> findField(String fieldName)
    {
        Optional<FieldInfo<?>> first = fields.stream().filter(f -> Objects.equal(f.name(), fieldName)).findFirst();
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


    private static final Map<Class<?>, ClassData<?>> classInfoCache = new IdentityHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <C> ClassData<? super C> getSuperClassInfo(TypeToken<C> cls)
    {
        Class rawType = cls.getRawType();
        TypeToken<? super C> of = TypeToken.of(rawType);
        return (ClassData<? super C>) getSuperClassInfo(rawType, of);
    }

    public static <C> ClassData<? super C> getSuperClassInfo(Class<C> cls)
    {
        return getClassInfo(cls, TypeToken.of(cls));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <C> ClassData<? super C> getSuperClassInfo(Class<C> cls, TypeToken<C> clsToken)
    {
        Class superClass = cls.getSuperclass();
        TypeToken<? super C> superToken = clsToken.getSupertype(superClass);
        return getClassInfo(superClass, superToken);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <C> ClassData<C> getClassInfo(TypeToken<C> cls)
    {
        Class rawType = cls.getRawType();
        TypeToken<? super C> of = TypeToken.of(rawType);
        return getClassInfo(rawType, of);
    }

    public static <C> ClassData<C> getClassInfo(Class<C> cls)
    {
        return getClassInfo(cls, TypeToken.of(cls));
    }

    public static <C> ClassData<C> getClassInfo(Class<C> cls, TypeToken<C> clsToken)
    {
        Class<? super C> superClass = cls.getSuperclass();
        TypeToken<? super C> superToken = clsToken.getSupertype(superClass);
        ClassData<C> ci = new ClassData<>(superToken, clsToken);
        for (Constructor<?> cnt : superClass.getDeclaredConstructors())
        {
            MethodData<?> mi = new MethodData<>();
            mi.name = cnt.getName();
            mi.modifiers = cnt.getModifiers();
            for (Parameter p : cnt.getParameters())
            {
                ParamData<?> pi = new ParamData<>();
                pi.name = p.getName();
                pi.paramType = TypeProxy.of(p.getParameterizedType());
                mi.params.add(pi);
            }
            ci.constructors.add(mi);
        }
        for (Method m : superClass.getDeclaredMethods())
        {
            MethodData<?> mi = new MethodData<>();
            mi.name = m.getName();
            mi.modifiers = m.getModifiers();
            for (Parameter p : m.getParameters())
            {
                ParamData<?> pi = new ParamData<>();
                pi.name = p.getName();
                pi.paramType = TypeProxy.of(p.getParameterizedType());
                mi.params.add(pi);
            }
            ci.methods.add(mi);
        }
        for (Field f : superClass.getDeclaredFields())
        {
            FieldData<?> fi = new FieldData<>();
            fi.name = f.getName();
            fi.modifiers = f.getModifiers();
            ci.fields.add(fi);
        }
        return ci;
    }
}
