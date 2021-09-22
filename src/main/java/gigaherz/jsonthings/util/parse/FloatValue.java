package gigaherz.jsonthings.util.parse;

import it.unimi.dsi.fastutil.floats.FloatConsumer;

public interface FloatValue
{
    void handle(FloatConsumer value);

    FloatValue min(float min);
    FloatValue range(float min, float maxExclusive);
}
