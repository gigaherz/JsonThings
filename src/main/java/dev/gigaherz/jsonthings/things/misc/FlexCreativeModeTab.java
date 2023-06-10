package dev.gigaherz.jsonthings.things.misc;

import dev.gigaherz.jsonthings.things.StackContext;

public record FlexCreativeModeTab(String name, StackContext icon)
{
    public static FlexCreativeModeTab create(String name, StackContext icon)
    {
        return new FlexCreativeModeTab(name, icon);
    }
}
