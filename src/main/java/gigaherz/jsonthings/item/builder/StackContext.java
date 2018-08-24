package gigaherz.jsonthings.item.builder;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class StackContext
{
    public final ResourceLocation item;
    public int meta = 0;
    public NBTTagCompound tag = null;
    public int count = 1;

    private Item theItem;

    public StackContext(@Nullable ResourceLocation item)
    {
        this.item = item;
    }

    public StackContext withMetadata(int meta)
    {
        this.meta = meta;
        return this;
    }

    public StackContext withTag(NBTTagCompound tag)
    {
        this.tag = tag;
        return this;
    }

    public StackContext withCount(int count)
    {
        this.count = count;
        return this;
    }

    public ItemStack toStack(Item self)
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
            }
        }
        ItemStack stack = new ItemStack(theItem, count, meta);
        if (tag != null)
            stack.setTagCompound(tag);
        return stack;
    }
}
