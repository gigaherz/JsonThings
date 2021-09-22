package gigaherz.jsonthings.util.parse;

import java.util.function.IntConsumer;

public interface IntValue
{
    void handle(IntConsumer value);

    IntValue min(int min);
    IntValue range(int min, int maxExclusive);
}
