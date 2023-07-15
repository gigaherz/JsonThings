package dev.gigaherz.jsonthings.things;

import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.world.item.Item;

@Deprecated(forRemoval = true)
public interface IFlexItem extends IEventRunner
{
    default Item self()
    {
        return (Item) this;
    }
}
