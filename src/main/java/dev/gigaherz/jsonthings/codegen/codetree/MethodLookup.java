package dev.gigaherz.jsonthings.codegen.codetree;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ClassInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.ParamInfo;
import dev.gigaherz.jsonthings.codegen.codetree.impl.MethodImplementation;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MethodLookup<R>
{
    private final ClassInfo<R> owner;
    private final String name;
    private final List<TypeToken<?>> params = Lists.newArrayList();
    private boolean exact = false;

    public MethodLookup(TypeToken<R> ownerType, String name)
    {
        this.owner = ClassData.getClassInfo(ownerType);
        this.name = name;
    }

    public MethodLookup(ClassInfo<R> classData, String name)
    {
        this.owner = classData;
        this.name = name;
    }

    public MethodLookup<R> exact()
    {
        exact = true;
        return this;
    }

    public MethodLookup<R> withParam(Class<?> cls)
    {
        params.add(TypeToken.of(cls));
        return this;
    }

    public MethodLookup<R> withParam(TypeToken<?> cls)
    {
        params.add(cls);
        return this;
    }

    public MethodInfo<?> result()
    {
        int bestDistance = Integer.MAX_VALUE;
        MethodInfo<?> bestMatch = null;
        List<? extends MethodInfo<?>> list;
        if (name.equals("<init>"))
            list = owner.constructors();
        else list = owner.methods();
        outer: for(var method : list)
        {
            if (!name.equals(method.name()))
                continue;
            List<? extends ParamInfo<?>> params = method.params();
            if (params.size() != this.params.size())
                continue;
            int distance = 0;
            for(int i=0;i<params.size();i++)
            {
                var rt = params.get(i).paramType().actualType();
                var rs = MethodImplementation.applyAutomaticCasting(rt, this.params.get(i));

                if (!rt.equals(rs))
                {
                    if (exact)
                        continue outer;

                    if (rt.isSupertypeOf(rs))
                    {
                        // TODO: count real distance
                        distance++;
                    }
                    else
                    {
                        continue outer;
                    }
                }
            }
            if (distance < bestDistance)
            {
                bestDistance = distance;
                bestMatch = method;
            }
        }
        if (bestMatch == null)
        {
            if (name.equals("<init>"))
                throw new IllegalStateException("Could not find a constructor of "+owner.thisType()+" matching params " + params);
            else
                throw new IllegalStateException("Could not find a method in "+owner.thisType()+" with name " + name + " and params " + params);
        }
        return bestMatch;
    }
}
