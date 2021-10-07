package dev.gigaherz.jsonthings.codegen;

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
                        .assign(cb.fieldRef("x"), cb.localVar("x"))
                        .assign(cb.fieldRef("y"), cb.localVar("y"))
                        .assign(cb.fieldRef("z"), cb.localVar("z"))
                        .returnVoid())
                .method("getX", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.returnVal(cb.field("x")))
                .method("getY", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.returnVal(cb.field("y")))
                .method("getZ", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.returnVal(cb.field("z")));

        try
        {
            Files.write(Path.of("F:/" + builder.finish().getSimpleName() + ".class"), builder.makeClass());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        var ci = builder.make();
    }
}
