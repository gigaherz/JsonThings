package gigaherz.jsonthings.codegen;

import gigaherz.jsonthings.codegen.codetree.CodeBlock;

@SuppressWarnings("UnstableApiUsage")
public class Demo
{
    public void test()
    {
        new ClassMaker().begin()
                .setPublic().setFinal()
                .field("x", int.class).setPrivate().setFinal()
                .field("y", int.class).setPrivate().setFinal()
                .field("z", int.class).setPrivate().setFinal()
                .constructor()
                    .setPublic().setInstance()
                    .param(int.class).withName("x")
                    .param(int.class).withName("y")
                    .param(int.class).withName("z")
                    .implementation(m -> CodeBlock.begin(m)
                            .getLocal("x").setField("x")
                            .getLocal("x").setField("y")
                            .getLocal("x").setField("z")
                            .returnVoid())
                .method("getX", int.class)
                    .setPublic().setFinal().setInstance().implementation(m -> CodeBlock.begin(m).getField("x").returnInt())
                .method("getY", int.class)
                    .setPublic().setFinal().setInstance().implementation(m -> CodeBlock.begin(m).getField("y").returnInt())
                .method("getZ", int.class)
                    .setPublic().setFinal().setInstance().implementation(m -> CodeBlock.begin(m).getField("z").returnInt());

    }
}
