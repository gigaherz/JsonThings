package gigaherz.jsonthings.item;

import gigaherz.jsonthings.item.context.FlexEventContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface ItemEventHandler
{
    ActionResult<ItemStack> apply(String eventName, FlexEventContext context);
}


