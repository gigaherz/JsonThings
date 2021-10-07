package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public interface LRef<T>
{
    TypeToken<T> targetType();

    void compileBefore(MethodVisitor mv);

    void compileAfter(MethodVisitor mv);
}

