package dev.gigaherz.jsonthings.things.events;

import java.util.function.BiFunction;

public interface FlexEventHandler<T> extends BiFunction<String, FlexEventContext, T>
{
    @Override
    T apply(String eventName, FlexEventContext context);
}
