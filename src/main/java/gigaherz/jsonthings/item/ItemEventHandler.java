package gigaherz.jsonthings.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

public interface ItemEventHandler
{
    ActionResult<ItemStack> apply(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack);
}


