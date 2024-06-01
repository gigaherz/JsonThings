package dev.gigaherz.jsonthings.things.events;

@FunctionalInterface
public interface FlexEventHandler<T>
{
    T apply(FlexEventType<T> event, FlexEventContext context);
}
