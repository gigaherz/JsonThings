package dev.gigaherz.jsonthings.codegen.codetree.impl;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SkipLoop extends InstructionSource
{
    private final boolean breakLoop;

    public SkipLoop(boolean breakLoop)
    {

        this.breakLoop = breakLoop;
    }

    @Override
    public boolean compile(MethodVisitor mv, Label jumpEnd, boolean needsResult)
    {
        mv.visitJumpInsn(Opcodes.GOTO, jumpEnd);
        return breakLoop;
    }
}
