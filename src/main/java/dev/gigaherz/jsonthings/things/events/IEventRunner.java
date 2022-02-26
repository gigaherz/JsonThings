package dev.gigaherz.jsonthings.things.events;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface IEventRunner
{
    void addEventHandler(String eventName, FlexEventHandler eventHandler);

    @Nullable
    FlexEventHandler getEventHandler(String eventName);

    default FlexEventResult runEvent(String eventName, FlexEventContext context, Supplier<FlexEventResult> defaultValue)
    {
        FlexEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.get();
    }

    default FlexEventResult runEventThrowing(String eventName, FlexEventContext context, Callable<FlexEventResult> defaultValue) throws Exception
    {
        FlexEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.call();
    }
}
