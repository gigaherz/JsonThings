package dev.gigaherz.jsonthings.codegen.codetree.expr;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.Nullable;

public interface BooleanExpression<B> extends ValueExpression<Boolean, B>
{
    void compile(MethodVisitor mv, @Nullable Label jumpTrue, @Nullable Label jumpFalse);
}
