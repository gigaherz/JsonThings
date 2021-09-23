package gigaherz.jsonthings.util.parse.value;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

public interface DoubleValue
{
    void handle(DoubleConsumer value);

    DoubleValue min(double min);

    DoubleValue range(double min, double maxExclusive);

    double getAsDouble();

    default <T> MappedValue<T> map(DoubleFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsDouble()));
    }
}
