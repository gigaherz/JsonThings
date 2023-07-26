package dev.gigaherz.jsonthings.util.parse.value;

public interface NumberValue
{
    void handle(NumberConsumer value);

    NumberValue min(Number min);

    NumberValue range(Number min, Number maxExclusive);

    Number getNumber();
    int getAsInt();
    long getAsLong();
    float getAsFloat();
    double getAsDouble();

    default <T> MappedValue<T> map(NumberFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getNumber()));
    }
}
