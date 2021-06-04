package gigaherz.jsonthings.things.events;

import net.minecraft.util.ActionResultType;

public interface BlockEventHandler
{
    ActionResultType apply(String eventName, FlexEventContext context);
}


