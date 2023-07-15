package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;

public interface IItemSerializer<T extends Item>
{
    IItemFactory<T> createFactory(JsonObject data);
}
