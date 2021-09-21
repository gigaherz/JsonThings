package gigaherz.jsonthings.things.misc;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class FlexCreativeModeTab extends CreativeModeTab
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
