package dev.gigaherz.jsonthings.util.parse.value;

import java.util.function.Consumer;
import java.util.function.Function;

public interface MappedValue<T>
{
    static <T> MappedValue<T> of(T value)
    {
        return new Impl<>(value);
    }

    T value();

    default void handle(Consumer<T> visitor)
    {
        visitor.accept(value());
    }

    default <R> MappedValue<R> map(Function<T, R> mapping)
    {
        return of(mapping.apply(value()));
    }

    class Impl<T> implements MappedValue<T>
    {
        private final T value;

        public Impl(T value)
        {
            this.value = value;
        }

        @Override
        public T value()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "[MappedValue " + value + "]";
        }
    }
}
