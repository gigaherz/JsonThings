package gigaherz.jsonthings.util.parse;

import java.util.function.Consumer;

public interface StringValue
{
    void handle(Consumer<String> value);
}
