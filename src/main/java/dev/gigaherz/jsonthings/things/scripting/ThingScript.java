package dev.gigaherz.jsonthings.things.scripting;

import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;

public abstract class ThingScript implements FlexEventHandler
{
    @Override
    public abstract FlexEventResult apply(String eventName, FlexEventContext context);
}
