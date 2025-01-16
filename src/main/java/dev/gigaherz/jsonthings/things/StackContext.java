package dev.gigaherz.jsonthings.things;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.things.serializers.ItemVariantProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class StackContext implements ItemVariantProvider
{
    public final ResourceLocation item;
    public CompoundTag tag = null;
    public int count = 1;

    private Item theItem;

    public StackContext(@Nullable ResourceLocation item)
    {
        this.item = item;
    }

    public StackContext withTag(CompoundTag tag)
    {
        this.tag = tag;
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
                theItem = ForgeRegistries.ITEMS.getValue(this.item);
                if (theItem == null)
                    throw new RuntimeException(String.format("The item '%s' is not registered.", this.item));
            }
            else
            {
                theItem = self;
                if (theItem == null)
                    throw new RuntimeException("No item name specified for a stack context that isn't linked to an item.");
            }
        }
        ItemStack stack = new ItemStack(theItem, count);
        if (tag != null)
            stack.setTag(tag);
        return stack;
    }

    @Override
    public void provideVariants(ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.Output output, CreativeModeTab.ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit)
    {
        output.accept(toStack(null));
    }
}
