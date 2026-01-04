package dev.gigaherz.jsonthings.things.events;

import javax.annotation.Nullable;

import static dev.gigaherz.jsonthings.things.scripting.McFunctionScript.orElse;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface IEventRunner
{
    <T> void addEventHandler(FlexEventType<T> event, FlexEventHandler<T> eventHandler);

    @Nullable
    <T> FlexEventHandler<T> getEventHandler(FlexEventType<T> event);

    default void runEvent(FlexEventType<Void> event, FlexEventContext context, Runnable defaultValue)
    {
        runEvent(event, context, () -> {
            defaultValue.run();
            return null;
        });
    }

    default <T> T runEvent(FlexEventType<T> event, FlexEventContext context, Supplier<T> defaultValue)
    {
        FlexEventHandler<T> handler = getEventHandler(event);
        if (handler != null)
            return (T) orElse(handler.apply(event, context), defaultValue.get());
        return defaultValue.get();
    }

    default <T> T runEventThrowing(FlexEventType<T> event, FlexEventContext context, Callable<T> defaultValue) throws Exception
    {
        FlexEventHandler<T> handler = getEventHandler(event);
        if (handler != null)
            return (T) orElse(handler.apply(event, context), defaultValue.call());
        return defaultValue.call();
    }
}
