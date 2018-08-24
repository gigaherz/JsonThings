package gigaherz.jsonthings.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

public interface ItemEventHandler
{
    ActionResult<ItemStack> apply(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack);
}


