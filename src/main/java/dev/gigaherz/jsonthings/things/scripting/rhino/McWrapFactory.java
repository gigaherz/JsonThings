package dev.gigaherz.jsonthings.things.scripting.rhino;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.WrapFactory;
import net.minecraft.Util;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.function.Function;

public class McWrapFactory extends WrapFactory
{
    IdentityHashMap<Class<?>, Function<Object, Scriptable>> objectWrappers = Util.make(new IdentityHashMap<>(), map -> {


    });

    IdentityHashMap<Class<?>, Function<Class<?>, Scriptable>> classWrappers = Util.make(new IdentityHashMap<>(), map -> {


    });

    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, @Nullable Object javaObject, @Nullable Class<?> staticType)
    {
        if (javaObject != null)
        {
            if (staticType != null)
            {
                var factory = objectWrappers.get(staticType);
                if (factory != null)
                    return factory.apply(javaObject);
            }
            else
            {
                var dynamicType = javaObject.getClass();
                while (dynamicType != Object.class)
                {
                    var factory = objectWrappers.get(dynamicType);
                    if (factory != null)
                        return factory.apply(javaObject);
                    dynamicType = dynamicType.getSuperclass();
                }
            }
        }
        return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
    }

    @Override
    public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass)
    {
        if (javaClass != null)
        {
            while (javaClass != Object.class)
            {
                var factory = classWrappers.get(javaClass);
                if (factory != null)
                    return factory.apply(javaClass);
                javaClass = javaClass.getSuperclass();
            }
        }
        return super.wrapJavaClass(cx, scope, javaClass);
    }
}
