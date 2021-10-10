package dev.gigaherz.jsonthings.codegen.codetree.expr;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("UnstableApiUsage")
public abstract class LRef<T, B> extends ExprBase<B>
{
    public LRef(CodeBlock<B,?,?> cb)
    {
        super(cb);
    }

    public abstract TypeToken<T> targetType();

    public abstract void compileBefore(MethodVisitor mv);

    public abstract void compileAfter(MethodVisitor mv);
}

