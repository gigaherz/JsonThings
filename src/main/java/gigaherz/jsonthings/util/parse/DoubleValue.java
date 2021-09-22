package gigaherz.jsonthings.util.parse;

import java.util.function.DoubleConsumer;

public interface DoubleValue
{
    void handle(DoubleConsumer value);

    DoubleValue min(double min);
    DoubleValue range(double min, double maxExclusive);
}
