package dev.gigaherz.jsonthings.things;

import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public interface IFlexItem extends IEventRunner
{
    default Item self()
    {
        return (Item) this;
    }

    void addCreativeStack(StackContext stack, Iterable<CreativeModeTab> tabs);
}
