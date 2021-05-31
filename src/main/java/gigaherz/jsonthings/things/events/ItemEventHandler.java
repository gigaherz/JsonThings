package gigaherz.jsonthings.things.events;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface ItemEventHandler
{
    ActionResult<ItemStack> apply(String eventName, FlexEventContext context);
}


