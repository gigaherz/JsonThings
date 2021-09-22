package gigaherz.jsonthings.util.parse;

import java.util.function.LongConsumer;

public interface LongValue
{
    void handle(LongConsumer value);

    LongValue min(long min);
    LongValue range(long min, long maxExclusive);
}
