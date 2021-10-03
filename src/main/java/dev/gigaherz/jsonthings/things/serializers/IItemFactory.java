package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import net.minecraft.world.item.Item;

public interface IItemFactory<T extends Item & IFlexItem>
{
    T construct(Item.Properties properties, ItemBuilder builder);
}
