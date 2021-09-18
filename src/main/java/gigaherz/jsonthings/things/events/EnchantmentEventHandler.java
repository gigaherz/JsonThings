package gigaherz.jsonthings.things.events;

import net.minecraft.world.InteractionResult;

import java.util.function.BiFunction;

public interface EnchantmentEventHandler extends BiFunction<String, FlexEventContext, InteractionResult>
{
    @Override
    InteractionResult apply(String eventName, FlexEventContext context);
}
