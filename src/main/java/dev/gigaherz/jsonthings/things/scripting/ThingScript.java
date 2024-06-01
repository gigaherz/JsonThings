package dev.gigaherz.jsonthings.things.scripting;

import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventType;

public abstract class ThingScript implements FlexEventHandler
{
    @Override
    public abstract Object apply(FlexEventType event, FlexEventContext context);
}
