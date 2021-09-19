package gigaherz.jsonthings.things.events;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface IEventRunner<TEventResult>
{
    void addEventHandler(String eventName, FlexEventHandler<TEventResult> eventHandler);

    @Nullable
    FlexEventHandler<TEventResult> getEventHandler(String eventName);

    default TEventResult runEvent(String eventName, FlexEventContext context, Supplier<TEventResult> defaultValue)
    {
        FlexEventHandler<TEventResult> handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.get();
    }

    default TEventResult runEventThrowing(String eventName, FlexEventContext context, Callable<TEventResult> defaultValue) throws Exception
    {
        FlexEventHandler<TEventResult> handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.call();
    }
}
