package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import dev.gigaherz.jsonthings.codegen.api.codetree.info.MethodInfo;
import dev.gigaherz.jsonthings.codegen.codetree.ClassData;
import dev.gigaherz.jsonthings.codegen.codetree.CodeBlock;
import dev.gigaherz.jsonthings.codegen.codetree.MethodLookup;
import dev.gigaherz.jsonthings.codegen.type.TypeProxy;
import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public interface ValueExpression<R>
{
    TypeToken<R> effectiveType();
    default TypeProxy<R> proxyType() {
        return TypeProxy.of(effectiveType());
    }

    void compile(MethodVisitor mv, boolean needsResult);

    default LRef<?> fieldRef(CodeBlock<?> cb, String fieldName)
    {
        return CodeBlock.fieldRef(this, ClassData.getClassInfo(effectiveType()).getField(fieldName));
    }

    default MethodCallExpression<?> methodCall(String name)
    {
        return methodCall(name, ml -> ml);
    }

    default MethodCallExpression<?> methodCall(String name, ValueExpression<?>... values)
    {
        return methodCall(name, ml -> {
            for(var val : values)
            {
                ml.withParam(val.effectiveType());
            }
            return ml;
        }, values);

    }

    default MethodCallExpression<?> methodCall(String name, Function<MethodLookup<R>, MethodLookup<R>> lookup, ValueExpression<?>... values)
    {
        var ml = new MethodLookup<>(proxyType().classInfo(), name);
        ml = lookup.apply(ml);
        return CodeBlock.methodCall(this, ml.result(), values);
    }
}
