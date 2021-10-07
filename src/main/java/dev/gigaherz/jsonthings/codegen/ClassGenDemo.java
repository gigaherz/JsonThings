package dev.gigaherz.jsonthings.codegen;

import dev.gigaherz.jsonthings.codegen.api.BasicClass;
import dev.gigaherz.jsonthings.codegen.api.DefineClass;
import dev.gigaherz.jsonthings.codegen.codetree.CodeBlock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public class ClassGenDemo
{
    public final static class Test {
        private final int x;
        private final int y;
        private final int z;
        public Test(int x, int y, int z)
        {
            this.x=x;
            this.y=y;
            this.z=z;
        }
        public final int getX() { return x;}
        public final int getY() { return y;}
        public final int getZ() { return z;}
    }

    public static void main(String[] args)
    {
        var builder = new ClassMaker().begin()
                .setPublic().setFinal()
                .field("x", int.class).setPrivate().setFinal()
                .field("y", int.class).setPrivate().setFinal()
                .field("z", int.class).setPrivate().setFinal()
                .constructor().setPublic().setInstance()
                .param(int.class).withName("x")
                .param(int.class).withName("y")
                .param(int.class).withName("z")
                .implementation(cb -> cb
                        .getThis().getLocal("x").setField("x")
                        .getThis().getLocal("y").setField("y")
                        .getThis().getLocal("z").setField("z")
                        .returnVoid())
                .method("getX", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.getThis().getField("x").returnInt())
                .method("getY", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.getThis().getField("y").returnInt())
                .method("getZ", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.getThis().getField("z").returnInt());

        try
        {
            Files.write(Path.of("F:/" + builder.finish().getSimpleName() + ".class"), builder.makeClass());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        var ci = builder
                .make();
    }
}
