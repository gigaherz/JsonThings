package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class FlexCreativeModeTab extends ItemGroup
{
    public static FlexCreativeModeTab create(String name, Supplier<Item> icon)
    {
        return new FlexCreativeModeTab(name, icon);
    }

    private final Supplier<Item> icon;

    public FlexCreativeModeTab(String name, Supplier<Item> icon)
    {
        super(name);
        this.icon = icon;
    }

    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(icon.get());
    }
}
