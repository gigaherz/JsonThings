package dev.gigaherz.jsonthings.things;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.things.serializers.ItemVariantProvider;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class StackContext implements ItemVariantProvider
{
    public final ResourceLocation item;
    public int count = 1;
    public DataComponentMap components = null;

    private Item theItem;

    public StackContext(@Nullable ResourceLocation item)
    {
        this.item = item;
    }

    public StackContext withComponents(DataComponentMap components)
    {
        this.components = components;
        return this;
    }

    public StackContext withCount(int count)
    {
        this.count = count;
        return this;
    }

    public ItemStack toStack(@Nullable Item self)
    {
        if (theItem == null)
        {
            if (this.item != null)
            {
                theItem = BuiltInRegistries.ITEM.getOptional(this.item).orElseThrow(() -> new RuntimeException(String.format("The item '%s' is not registered.", this.item)));
            }
            else
            {
                theItem = self;
                if (theItem == null)
                    throw new RuntimeException("No item name specified for a stack context that isn't linked to an item.");
            }
        }
        ItemStack stack = new ItemStack(theItem, count);
        if (components != null)
            stack.applyComponents(components);
        return stack;
    }

    @Override
    public void provideVariants(ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.Output output, CreativeModeTab.ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit)
    {
        output.accept(toStack(null));
}
}
