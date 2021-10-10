package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.codetree.ClassData;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.MethodVisitor;

import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public abstract class ValueExpression<T, B> extends ExprBase<B>
{
    public ValueExpression(CodeBlock<B,?,?> cb)
    {
        super(cb);
    }

    public abstract TypeToken<T> effectiveType();
    public TypeProxy<T> proxyType() {
        return TypeProxy.of(effectiveType());
    }

    public abstract void compile(MethodVisitor mv, boolean needsResult);

    public LRef<?,B> fieldRef(String fieldName)
    {
        return cb.fieldRef(this, ClassData.getClassInfo(effectiveType()).getField(fieldName));
    }

    public ValueExpression<?,B> field(String fieldName)
    {
        return cb.field(this, ClassData.getClassInfo(effectiveType()).getField(fieldName));
    }

    public MethodCallExpression<?,B> methodCall(String name)
    {
        return methodCall(name, ml -> ml);
    }

    public MethodCallExpression<?,B> methodCall(String name, ValueExpression<?,B>... values)
    {
        return methodCall(name, ml -> {
            for(var val : values)
            {
                ml.withParam(val.effectiveType());
            }
            return ml;
        }, values);

    }

    public MethodCallExpression<?,B> methodCall(String name, Function<MethodLookup<T>, MethodLookup<T>> lookup, ValueExpression<?,B>... values)
    {
        var ml = new MethodLookup<>(proxyType().classInfo(), name);
        ml = lookup.apply(ml);
        return cb.methodCall(this, ml.result(), values);
    }
}
