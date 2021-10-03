package dev.gigaherz.jsonthings.things.events;

public class ContextValue<T>
{
    private final String name;
    private final Class<? extends T> type;

    public ContextValue(String name, Class<? extends T> type)
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
        return new ContextValue<T>(name, type);
    }
}
