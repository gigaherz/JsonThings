package dev.gigaherz.jsonthings.codegen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public class ClassGenDemo
{
    public static class Test {
        protected final int x;
        protected final int y;
        protected final int z;
        public Test(int x, int y, int z)
        {
            this.x=x;
            this.y=y;
            this.z=z;
        }
        public int getX() { return x;}
        public int getY() { return getX();}
        public int getZ() { return z;}
    }

    public static class Test2 extends Test {
        public int x;

        public Test2(int x, int y, int z)
        {
            super(x, y, z);
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public int getSuperX() {
            return super.x;
        }

        public int maxCoord() {
            if (x > y && x > z)
            {
                return x;
            }
            else if (y > z)
            {
                return y;
            }
            else
            {
                return z;
            }
        }
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
                        .superCall()
                        .assign(cb.fieldRef("x"), cb.localVar("x"))
                        .assign(cb.fieldRef("y"), cb.localVar("y"))
                        .assign(cb.fieldRef("z"), cb.localVar("z"))
                        .returnVoid())
                .method("getX", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.returnVal(cb.field("x")))
                .method("getY", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.returnVal(cb.thisVar().methodCall("getX")))
                .method("getZ", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb.returnVal(cb.thisVar().field("z")))
                .method("maxCoord", int.class)
                .setPublic().setFinal().setInstance().implementation(cb -> cb
                        .ifElse(
                                cb.and(cb.gt(cb.field("x"), cb.field("y")), cb.gt(cb.field("x"), cb.field("z"))),
                                ct -> ct.returnVal(ct.field("x")),
                                ct -> ct.ifElse(
                                        cb.gt(cb.field("y"), cb.field("z")),
                                        cz -> cz.returnVal(cz.field("u")),
                                        cz -> cz.returnVal(cz.field("z"))
                                )
                        )
                        .returnVal(cb.field("z")));

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
