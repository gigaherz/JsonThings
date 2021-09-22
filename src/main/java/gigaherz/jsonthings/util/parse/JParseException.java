package gigaherz.jsonthings.util.parse;

import com.google.gson.JsonParseException;

public class JParseException extends JsonParseException
{
    public JParseException(String message)
    {
        super(message);
    }

    public JParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public JParseException(Throwable cause)
    {
        super(cause);
    }
}
