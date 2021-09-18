package gigaherz.jsonthings.things.events;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public interface ItemEventHandler extends BiFunction<String, FlexEventContext, InteractionResultHolder<ItemStack>>
{
    @Override
    InteractionResultHolder<ItemStack> apply(String eventName, FlexEventContext context);
}
