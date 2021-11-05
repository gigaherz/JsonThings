package dev.gigaherz.jsonthings.things.serializers;

import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class ItemFactory<T extends Item & IFlexItem> implements IItemFactory<T>
{
    private final IItemFactory<T> factory;
    @Nullable
    private final Pair<ToolType, Integer> defaultTool;

    public ItemFactory(IItemFactory<T> factory)
    {
        this(factory, null);
    }

    public ItemFactory(IItemFactory<T> factory, @Nullable Pair<ToolType, Integer> defaultTool)
    {
        this.factory = factory;
        this.defaultTool = defaultTool;
    }

    @Override
    public T construct(Item.Properties properties, ItemBuilder builder)
    {
        return factory.construct(properties, builder);
    }

    @Nullable
    public Pair<ToolType, Integer> getDefaultTool()
    {
        return defaultTool;
    }
}
