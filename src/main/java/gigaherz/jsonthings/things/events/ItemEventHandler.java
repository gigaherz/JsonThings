package gigaherz.jsonthings.things.events;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public interface ItemEventHandler
{
    InteractionResultHolder<ItemStack> apply(String eventName, FlexEventContext context);
}


