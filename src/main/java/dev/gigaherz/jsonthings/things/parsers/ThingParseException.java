package dev.gigaherz.jsonthings.things.parsers;

public class ThingParseException extends RuntimeException
{
    public ThingParseException(String message)
    {
        super(message);
    }

    public ThingParseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
