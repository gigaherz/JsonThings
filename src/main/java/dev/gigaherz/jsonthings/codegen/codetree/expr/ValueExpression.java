package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public interface ValueExpression<R>
{
    TypeToken<R> effectiveType();

    void compile(MethodVisitor mv, boolean needsResult);
}
