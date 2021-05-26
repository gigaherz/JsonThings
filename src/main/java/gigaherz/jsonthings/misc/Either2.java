package gigaherz.jsonthings.misc;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused,WeakerAccess")
public abstract class Either2<TMain, TOther>
{
    public static final class Main<TMain, TOther> extends Either2<TMain, TOther>
    {
        private final TMain valueMain;

        public Main(TMain value)
        {
            this.valueMain = value;
        }

        @Override
        public boolean isPresent()
        {
            return true;
        }

        @Override
        public <X extends Throwable> TMain orElseThrow(Function<TOther, X> exceptionConverter) throws X
        {
            return valueMain;
        }

        @Override
        public Optional<TMain> toOptional()
        {
            return Optional.of(valueMain);
        }

        public TOther getOther()
        {
            throw new IllegalStateException("The value is Main");
        }

        public Optional<TOther> toOptionalOther()
        {
            return Optional.empty();
        }

        public Either2<TMain, TOther> ifPresent(Consumer<TMain> consumer)
        {
            consumer.accept(valueMain);
            return this;
        }

        public Either2<TMain, TOther> ifOther(Consumer<TOther> consumer)
        {
            return this;
        }

        public Either2<TMain, TOther> consumeEither(Consumer<TMain> consumerMain, Consumer<TOther> consumerOther)
        {
            consumerMain.accept(valueMain);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <R> Either2<R, TOther> map(Function<TMain, R> map)
        {
            return ofMain(map.apply(get()));
        }

        @SuppressWarnings("unchecked")
        public <S> Either2<TMain, S> mapOther(Function<TOther, S> map)
        {
            return (Either2<TMain, S>) this;
        }

        public <R, S> Either2<R, S> mapEither(Function<TMain, R> mapMain, Function<TOther, S> mapOther)
        {
            return ofMain(mapMain.apply(valueMain));
        }

        @SuppressWarnings("unchecked")
        public <R> Either2<R, TOther> flatMap(Function<TMain, Either2<R, TOther>> map)
        {
            return map.apply(valueMain);
        }

        @SuppressWarnings("unchecked")
        public <S> Either2<TMain, S> flatMapOther(Function<TOther, Either2<TMain, S>> map)
        {
            return (Either2<TMain, S>) this;
        }

        public <R, S> Either2<R, S> flatMapEither(Function<TMain, Either2<R, S>> mapMain, Function<TOther, Either2<R, S>> mapOther)
        {
            return mapMain.apply(valueMain);
        }

        public Either2<TOther, TMain> flip()
        {
            return ofOther(valueMain);
        }

        @Override
        public String toString()
        {
            return String.format("Main[%s]", valueMain);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Main<?, ?> either = (Main<?, ?>) o;
            return Objects.equals(valueMain, either.valueMain);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(valueMain);
        }
    }

    public static final class Other<TMain, TOther> extends Either2<TMain, TOther>
    {
        private final TOther valueOther;

        public Other(TOther value)
        {
            this.valueOther = value;
        }

        @Override
        public boolean isPresent()
        {
            return false;
        }

        @Override
        public <X extends Throwable> TMain orElseThrow(Function<TOther, X> exceptionConverter) throws X
        {
            throw exceptionConverter.apply(valueOther);
        }

        @Override
        public Optional<TMain> toOptional()
        {
            return Optional.empty();
        }

        public TOther getOther()
        {
            throw new IllegalStateException("The value is Main");
        }

        public Optional<TOther> toOptionalOther()
        {
            return Optional.of(valueOther);
        }

        public Either2<TMain, TOther> ifPresent(Consumer<TMain> consumer)
        {
            return this;
        }

        public Either2<TMain, TOther> ifOther(Consumer<TOther> consumer)
        {
            consumer.accept(valueOther);
            return this;
        }

        public Either2<TMain, TOther> consumeEither(Consumer<TMain> consumerMain, Consumer<TOther> consumerOther)
        {
            consumerOther.accept(valueOther);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <R> Either2<R, TOther> map(Function<TMain, R> map)
        {
            return (Either2<R, TOther>) this;
        }

        @SuppressWarnings("unchecked")
        public <S> Either2<TMain, S> mapOther(Function<TOther, S> map)
        {
            return ofOther(map.apply(getOther()));
        }

        public <R, S> Either2<R, S> mapEither(Function<TMain, R> mapMain, Function<TOther, S> mapOther)
        {
            return ofOther(mapOther.apply(valueOther));
        }

        @SuppressWarnings("unchecked")
        public <R> Either2<R, TOther> flatMap(Function<TMain, Either2<R, TOther>> map)
        {
            return (Either2<R, TOther>) this;
        }

        @SuppressWarnings("unchecked")
        public <S> Either2<TMain, S> flatMapOther(Function<TOther, Either2<TMain, S>> map)
        {
            return map.apply(valueOther);
        }

        public <R, S> Either2<R, S> flatMapEither(Function<TMain, Either2<R, S>> mapMain, Function<TOther, Either2<R, S>> mapOther)
        {
            return mapOther.apply(valueOther);
        }

        public Either2<TOther, TMain> flip()
        {
            return ofMain(valueOther);
        }

        @Override
        public String toString()
        {
            return String.format("Other[%s]", valueOther);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Other<?, ?> either = (Other<?, ?>) o;
            return Objects.equals(valueOther, either.valueOther);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(valueOther);
        }
    }

    /**
     * Constructs an Either set to the main value
     *
     * @param value    The value to assign this Either
     * @param <TMain>  The main contained type
     * @param <TOther> The alternative contained type
     * @return A constructed Either
     */
    public static <TMain, TOther> Either2<TMain, TOther> ofMain(TMain value)
    {
        return new Main<>(Objects.requireNonNull(value));
    }

    /**
     * Constructs an Either set to the alternative value
     *
     * @param value    The value to assign this Either
     * @param <TMain>  The main contained type
     * @param <TOther> The alternative contained type
     * @return A constructed Either
     */
    public static <TMain, TOther> Either2<TMain, TOther> ofOther(TOther value)
    {
        return new Other<>(Objects.requireNonNull(value));
    }

    /**
     * Constructs an Either from an Optional and an alternative value
     *
     * @param value    The source of the value, if present
     * @param orElse   The alternative value
     * @param <TMain>  The main contained type
     * @param <TOther> The alternative contained type
     * @return A constructed Either
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <TMain, TOther> Either2<TMain, TOther> fromOptional(Optional<TMain> value, TOther orElse)
    {
        return value.<Either2<TMain, TOther>>map(Either2::ofMain).orElseGet(() -> ofOther(orElse));
    }

    /**
     * Constructs an Either from an Optional and an alternative value
     *
     * @param value     The source of the value, if present
     * @param orElseGet The supplier of the alternative
     * @param <TMain>   The main contained type
     * @param <TOther>  The alternative contained type
     * @return A constructed Either
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <TMain, TOther> Either2<TMain, TOther> fromOptionalGet(Optional<TMain> value, Supplier<TOther> orElseGet)
    {
        return value.<Either2<TMain, TOther>>map(Either2::ofMain).orElseGet(() -> ofOther(orElseGet.get()));
    }

    /**
     * Constructs an Either from a nullable value and an alternative value
     *
     * @param value    The source of the value, if present
     * @param orElse   The alternative value
     * @param <TMain>  The main contained type
     * @param <TOther> The alternative contained type
     * @return A constructed Either
     */
    public static <TMain, TOther> Either2<TMain, TOther> ofNullable(@Nullable TMain value, TOther orElse)
    {
        return value != null ? ofMain(value) : ofOther(orElse);
    }

    /**
     * Constructs an Either from a nullable value and an alternative value
     *
     * @param value     The source of the value, if present
     * @param orElseGet The supplier of the alternative
     * @param <TMain>   The main contained type
     * @param <TOther>  The alternative contained type
     * @return A constructed Either
     */
    public static <TMain, TOther> Either2<TMain, TOther> ofNullableGet(@Nullable TMain value, Supplier<TOther> orElseGet)
    {
        return value != null ? ofMain(value) : ofOther(orElseGet.get());
    }

    /**
     * Gets the kind of contained value
     *
     * @return true if the value is Main
     */
    public abstract boolean isPresent();

    /**
     * Gets the main value or throws if the Either isn't main
     *
     * @return The main value
     */
    public TMain get()
    {
        return orElseThrow((x) -> new IllegalStateException("Either is not Main"));
    }

    /**
     * Gets the main value or throws a custom exception if the Either isn't main
     *
     * @param exceptionConverter Converts the Other into an exception
     * @param <X>                The type of the exception
     * @return The main value, if the Either is main
     * @throws X The converted exception, if the Either is not main
     */
    public abstract <X extends Throwable> TMain orElseThrow(Function<TOther, X> exceptionConverter) throws X;

    /**
     * Turns the Either into an Optional, discarding the Other
     *
     * @return An optional with the main value, if main, or Optional.empty()
     */
    public abstract Optional<TMain> toOptional();

    /**
     * Gets the alternative value or throws if the Either is main
     *
     * @return The main value
     */
    public abstract TOther getOther();

    /**
     * Turns the Either into an Optional, discarding the Main
     *
     * @return An optional with the other value, if not main, or Optional.empty()
     */
    public abstract Optional<TOther> toOptionalOther();

    /**
     * Calls the consumer if the value is Main, does nothing otherwise
     *
     * @return Itself, for chaining purposes.
     */
    public abstract Either2<TMain, TOther> ifPresent(Consumer<TMain> consumer);

    /**
     * Calls the consumer if the value is Main, does nothing otherwise
     *
     * @return Itself, for chaining purposes.
     */
    public abstract Either2<TMain, TOther> ifOther(Consumer<TOther> consumer);

    public abstract Either2<TMain, TOther> consumeEither(Consumer<TMain> consumerMain, Consumer<TOther> consumerOther);

    /**
     * Transforms the main value, if main, and constructs a new Either with the result
     *
     * @param map Function to transform the Main
     * @param <R> New type for the Main
     * @return New Either if main, or the same Other
     */
    @SuppressWarnings("unchecked")
    public abstract <R> Either2<R, TOther> map(Function<TMain, R> map);

    /**
     * Transforms the alternative value, if not main, and constructs a new Either with the result
     *
     * @param map Function to transform the Other
     * @param <S> New type for the Other
     * @return New Either if not main, or the same Main
     */
    @SuppressWarnings("unchecked")
    public abstract <S> Either2<TMain, S> mapOther(Function<TOther, S> map);

    /**
     * Transforms either the main value, or the alternative value.
     *
     * @param mapMain  Function to transform the Main
     * @param mapOther Function to transform the Other
     * @param <R>      New type for the Main
     * @param <S>      New type for the Other
     * @return New Either built from either the transformed Main, or transformed Other
     */
    public abstract <R, S> Either2<R, S> mapEither(Function<TMain, R> mapMain, Function<TOther, S> mapOther);

    /**
     * Similar to map, but the function already returns the constructed Either
     *
     * @param map Function to transform the Main
     * @param <R> New type for the Main
     * @return New Either returned by the function if Main, or the same otherwise
     */
    @SuppressWarnings("unchecked")
    public abstract <R> Either2<R, TOther> flatMap(Function<TMain, Either2<R, TOther>> map);

    /**
     * Similar to map, but the function already returns the constructed Either
     *
     * @param map Function to transform the Other
     * @param <S> New type for the Other
     * @return New Either returned by the function if Other, or the same otherwise
     */
    @SuppressWarnings("unchecked")
    public abstract <S> Either2<TMain, S> flatMapOther(Function<TOther, Either2<TMain, S>> map);

    /**
     * Similar to map, but the function already returns the constructed Either
     *
     * @param mapMain  Function to transform the Main
     * @param mapOther Function to transform the Other
     * @param <R>      New type for the Main
     * @param <S>      New type for the Other
     * @return The result of applying the corresponding function
     */
    public abstract <R, S> Either2<R, S> flatMapEither(Function<TMain, Either2<R, S>> mapMain, Function<TOther, Either2<R, S>> mapOther);

    /**
     * Turns a Main into an Other, or an Other into a Main
     *
     * @return A new Either with the context flipped
     */
    public abstract Either2<TOther, TMain> flip();

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
