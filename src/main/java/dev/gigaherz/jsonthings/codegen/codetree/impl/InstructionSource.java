package dev.gigaherz.jsonthings.codegen.codetree.impl;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public abstract class InstructionSource
{
    public abstract boolean compile(MethodVisitor mv, Label jumpEnd, boolean needsResult);
}
