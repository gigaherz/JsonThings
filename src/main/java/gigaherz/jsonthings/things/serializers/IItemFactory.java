package gigaherz.jsonthings.things.serializers;

import gigaherz.jsonthings.things.IFlexItem;
import gigaherz.jsonthings.things.builders.ItemBuilder;
import net.minecraft.world.item.Item;

public interface IItemFactory<T extends Item & IFlexItem>
{
    T construct(Item.Properties properties, ItemBuilder builder);
}
