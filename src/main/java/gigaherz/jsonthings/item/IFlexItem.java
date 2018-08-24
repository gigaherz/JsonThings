package gigaherz.jsonthings.item;

import gigaherz.jsonthings.item.builder.DelayedUse;
import gigaherz.jsonthings.item.builder.StackContext;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface IFlexItem
{
    void setUseAction(EnumAction useAction);

    EnumAction getUseAction();

    void setUseTime(int useTicks);

    int getUseTime();

    void setUseFinishMode(DelayedUse.CompletionMode onComplete);

    DelayedUse.CompletionMode getUseFinishMode();

    void addEventHandler(String eventName, ItemEventHandler eventHandler);

    @Nullable
    ItemEventHandler getEventHandler(String eventName);

    void addCreativeStack(StackContext stack, Iterable<CreativeTabs> tabs);

    void addAttributemodifier(AttributeModifier modifier);


    default ActionResult<ItemStack> runEvent(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack, Supplier<ActionResult<ItemStack>> defaultValue)
    {
        ItemEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, player, hand, stack);
        return defaultValue.get();
    }

    default ActionResult<ItemStack> runEvent(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack, World world, BlockPos pos, EnumFacing side, Supplier<ActionResult<ItemStack>> defaultValue)
    {
        ItemEventHandler handler = getEventHandler(eventName);
        if (handler != null)
        {
            if (handler instanceof ItemEventHandlerBlock)
                return ((ItemEventHandlerBlock) handler).apply(eventName, player, hand, stack, world, pos, side);
            return handler.apply(eventName, player, hand, stack);
        }
        return defaultValue.get();
    }

    default ActionResult<ItemStack> runEvent(String eventName, EntityLivingBase player, @Nullable EnumHand hand, ItemStack stack, Entity entity, Supplier<ActionResult<ItemStack>> defaultValue)
    {
        ItemEventHandler handler = getEventHandler(eventName);
        if (handler != null)
        {
            if (handler instanceof ItemEventHandlerEntity)
                return ((ItemEventHandlerEntity) handler).apply(eventName, player, hand, stack, entity);
            return handler.apply(eventName, player, hand, stack);
        }
        return defaultValue.get();
    }
}
