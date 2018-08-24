package gigaherz.jsonthings.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ItemEventHandlerBlock extends ItemEventHandler
{
    @Override
    default ActionResult<ItemStack> apply(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack)
    {
        return apply(eventName, player, hand, stack, null, null, null);
    }

    ActionResult<ItemStack> apply(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack, @Nullable World world, @Nullable BlockPos pos, @Nullable EnumFacing side);
}

