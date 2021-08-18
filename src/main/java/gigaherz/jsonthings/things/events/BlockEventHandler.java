package gigaherz.jsonthings.things.events;

import net.minecraft.world.InteractionResult;

public interface BlockEventHandler
{
    InteractionResult apply(String eventName, FlexEventContext context);
}


