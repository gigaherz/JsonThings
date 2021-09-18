package gigaherz.jsonthings.things.events;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IEventRunner<TEventResult, THandlerType extends BiFunction<String, FlexEventContext, TEventResult>>
{
    void addEventHandler(String eventName, THandlerType eventHandler);

    @Nullable
    THandlerType getEventHandler(String eventName);

    default TEventResult runEvent(String eventName, FlexEventContext context, Supplier<TEventResult> defaultValue)
    {
        THandlerType handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.get();
    }

    default TEventResult runEventThrowing(String eventName, FlexEventContext context, Callable<TEventResult> defaultValue) throws Exception
    {
        THandlerType handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.call();
    }
}
