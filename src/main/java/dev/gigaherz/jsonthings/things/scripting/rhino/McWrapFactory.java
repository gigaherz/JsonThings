package dev.gigaherz.jsonthings.things.scripting.rhino;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.WrapFactory;
import net.minecraft.Util;

import java.util.IdentityHashMap;
import java.util.function.Function;

public class McWrapFactory extends WrapFactory
{
    IdentityHashMap<Class<?>, Function<Object, Scriptable>> wrapperRegistry = Util.make(new IdentityHashMap<>(), map -> {


    });

    @Override
    public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType)
    {
        if (obj != null)
        {
            var dynamicType = obj.getClass();
            while (dynamicType != Object.class)
            {
                var factory = wrapperRegistry.get(dynamicType);
                if (factory != null)
                    return factory.apply(obj);
                dynamicType = dynamicType.getSuperclass();
            }
        }
        return super.wrap(cx, scope, obj, staticType);
    }

    @Override
    public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj)
    {
        return super.wrapNewObject(cx, scope, obj);
    }

    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType)
    {
        return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
    }

    @Override
    public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass)
    {
        return super.wrapJavaClass(cx, scope, javaClass);
    }
}
