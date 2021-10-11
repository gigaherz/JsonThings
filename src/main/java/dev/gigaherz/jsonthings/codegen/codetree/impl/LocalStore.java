package dev.gigaherz.jsonthings.codegen.codetree.impl;

import com.google.common.base.Objects;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LocalStore extends InstructionSource
{
    private final MethodImplementation<?> mi;
    String localName;
    int localNumber;

    public LocalStore(MethodImplementation<?> mi, String localName)
    {
        this.mi = mi;
        this.localName = localName;
    }

    public LocalStore(MethodImplementation<?> mi, int localNumber)
    {
        this.mi = mi;
        this.localNumber = localNumber;
    }

    @Override
    public boolean compile(MethodVisitor mv, Label jumpEnd, boolean needsResult)
    {
        mv.visitLabel(mi.makeLabel());

        LocalVariable<?> localVariable;

        if (localName != null)
        {
            localVariable = mi.locals.stream().filter(local -> Objects.equal(local.name, localName)).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with name " + localName));
            localNumber = localVariable.index;
        }
        else
        {
            localVariable = mi.locals.stream().filter(local -> local.index == localNumber).findFirst().orElseThrow(() -> new IllegalStateException("No local or parameter with name " + localName));
        }

        compile(localVariable, mv);

        return false;
    }

    public static void compile(LocalVariable<?> localVariable, MethodVisitor mv)
    {
        if (!localVariable.variableType.isPrimitive())
        {
            mv.visitVarInsn(Opcodes.ASTORE, localVariable.index);
        }
        else
        {
            Class<?> rawType = localVariable.variableType.getRawType();
            if (rawType == long.class)
            {
                mv.visitVarInsn(Opcodes.LSTORE, localVariable.index);
            }
            else if (rawType == float.class)
            {
                mv.visitVarInsn(Opcodes.FSTORE, localVariable.index);
            }
            else if (rawType == double.class)
            {
                mv.visitVarInsn(Opcodes.DSTORE, localVariable.index);
            }
            else //if (type.getRawType() == int.class || type.getRawType() == short.class || type.getRawType() == byte.class || type.getRawType() == boolean.class)
            {
                mv.visitVarInsn(Opcodes.ISTORE, localVariable.index);
            }
        }
    }
}
