package gigaherz.jsonthings.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

public interface ItemEventHandlerEntity extends ItemEventHandler
{
    @Override
    default ActionResult<ItemStack> apply(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack)
    {
        return apply(eventName, player, hand, stack, null);
    }

    ActionResult<ItemStack> apply(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack, @Nullable Entity entity);
}
