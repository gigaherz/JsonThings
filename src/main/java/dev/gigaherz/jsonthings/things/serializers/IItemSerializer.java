package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.IFlexItem;
import net.minecraft.world.item.Item;

public interface IItemSerializer<T extends Item & IFlexItem>
{
    IItemFactory<T> createFactory(JsonObject data);
}
