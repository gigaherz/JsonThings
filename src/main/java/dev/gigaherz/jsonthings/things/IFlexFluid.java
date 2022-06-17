package dev.gigaherz.jsonthings.things;

import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public interface IFlexFluid extends IEventRunner
{
    default Fluid self()
    {
        return (Fluid) this;
    }

    default boolean registerTwin() { return true; }

    void setBucketItem(Supplier<Item> bucketItem);
}
