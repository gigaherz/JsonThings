package dev.gigaherz.jsonthings.things.events;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public class ContextValue<T>
{
    private final String name;
    private final Class<? extends T> type;

    private ContextValue(String name, Class<? extends T> type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public Class<? extends T> getType()
    {
        return type;
    }

    public static <T> ContextValue<T> create(String name, Class<? extends T> type)
    {
        if (registeredValue.containsKey(name))
            throw new RuntimeException("Duplicate key " + name + " for ContextValue");
        var val = new ContextValue<T>(name, type);
        registeredValue.put(name, val);
        return val;
    }

    public static ContextValue<?> get(String name)
    {
        if (!registeredValue.containsKey(name))
            throw new RuntimeException("No ContextValue known with name " + name);
        return registeredValue.get(name);
    }

    private static final Map<String, ContextValue<?>> registeredValue = new HashMap<>();
}
