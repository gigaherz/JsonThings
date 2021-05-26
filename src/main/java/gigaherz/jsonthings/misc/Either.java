package gigaherz.jsonthings.misc;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Either<TMain, TOther>
{
    private final boolean isMain;
    private final TMain valueMain;
    private final TOther valueOther;

    /**
     * Constructs an Either set to the main value
     *
     * @param value    The value to assign this Either
     * @param <TMain>  The main contained type
     * @param <TOther> The alternative contained type
     * @return A constructed Either
     */
    public static <TMain, TOther> Either<TMain, TOther> ofMain(TMain value)
    {
        return new Either<>(true, Objects.requireNonNull(value), null);
    }

    /**
     * Constructs an Either set to the alternative value
     *
     * @param value    The value to assign this Either
     * @param <TMain>  The main contained type
     * @param <TOther> The alternative contained type
     * @return A constructed Either
     */
    public static <TMain, TOther> Either<TMain, TOther> ofOther(TOther value)
    {
        return new Either<>(false, null, Objects.requireNonNull(value));
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
    public static <TMain, TOther> Either<TMain, TOther> fromOptional(Optional<TMain> value, TOther orElse)
    {
        return value.<Either<TMain, TOther>>map(Either::ofMain).orElseGet(() -> ofOther(orElse));
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
    public static <TMain, TOther> Either<TMain, TOther> fromOptionalGet(Optional<TMain> value, Supplier<TOther> orElseGet)
    {
        return value.<Either<TMain, TOther>>map(Either::ofMain).orElseGet(() -> ofOther(orElseGet.get()));
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
    public static <TMain, TOther> Either<TMain, TOther> ofNullable(@Nullable TMain value, TOther orElse)
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
    public static <TMain, TOther> Either<TMain, TOther> ofNullableGet(@Nullable TMain value, Supplier<TOther> orElseGet)
    {
        return value != null ? ofMain(value) : ofOther(orElseGet.get());
    }

    private Either(boolean isMain, TMain valueMain, TOther valueOther)
    {
        this.valueMain = valueMain;
        this.valueOther = valueOther;
        this.isMain = isMain;
    }

    /**
     * Gets the kind of contained value
     *
     * @return true if the value is Main
     */
    public boolean isPresent()
    {
        return this.isMain;
    }

    /**
     * Gets the main value or throws if the Either isn't main
     *
     * @return The main value
     */
    public TMain get()
    {
        if (!isMain)
            throw new IllegalStateException("The value is Other");
        return valueMain;
    }

    /**
     * Gets the main value or throws a custom exception if the Either isn't main
     *
     * @param exceptionConverter Converts the Other into an exception
     * @param <X>                The type of the exception
     * @return The main value, if the Either is main
     * @throws X The converted exception, if the Either is not main
     */
    public <X extends Throwable> TMain orElseThrow(Function<TOther, X> exceptionConverter) throws X
    {
        if (!isMain)
            throw exceptionConverter.apply(valueOther);
        return valueMain;
    }

    /**
     * Turns the Either into an Optional, discarding the Other
     *
     * @return An optional with the main value, if main, or Optional.empty()
     */
    public Optional<TMain> toOptional()
    {
        if (!isMain)
            return Optional.empty();
        return Optional.of(valueMain);
    }

    /**
     * Gets the alternative value or throws if the Either is main
     *
     * @return The main value
     */
    public TOther getOther()
    {
        if (!isMain)
            throw new IllegalStateException("The value is Main");
        return valueOther;
    }

    /**
     * Turns the Either into an Optional, discarding the Main
     *
     * @return An optional with the other value, if not main, or Optional.empty()
     */
    public Optional<TOther> toOptionalOther()
    {
        return isMain
                ? Optional.empty()
                : Optional.of(valueOther);
    }

    /**
     * Calls the consumer if the value is Main, does nothing otherwise
     *
     * @return Itself, for chaining purposes.
     */
    public Either<TMain, TOther> ifPresent(Consumer<TMain> consumer)
    {
        if (isMain)
            consumer.accept(valueMain);
        return this;
    }

    /**
     * Calls the consumer if the value is Main, does nothing otherwise
     *
     * @return Itself, for chaining purposes.
     */
    public Either<TMain, TOther> ifOther(Consumer<TOther> consumer)
    {
        if (!isMain)
            consumer.accept(valueOther);
        return this;
    }

    public Either<TMain, TOther> consumeEither(Consumer<TMain> consumerMain, Consumer<TOther> consumerOther)
    {
        if (isMain)
            consumerMain.accept(valueMain);
        else
            consumerOther.accept(valueOther);
        return this;
    }

    /**
     * Transforms the main value, if main, and constructs a new Either with the result
     *
     * @param map Function to transform the Main
     * @param <R> New type for the Main
     * @return New Either if main, or the same Other
     */
    @SuppressWarnings("unchecked")
    public <R> Either<R, TOther> map(Function<TMain, R> map)
    {
        return isMain
                ? ofMain(map.apply(get()))
                : (Either<R, TOther>) this;
    }

    /**
     * Transforms the alternative value, if not main, and constructs a new Either with the result
     *
     * @param map Function to transform the Other
     * @param <S> New type for the Other
     * @return New Either if not main, or the same Main
     */
    @SuppressWarnings("unchecked")
    public <S> Either<TMain, S> mapOther(Function<TOther, S> map)
    {
        return !isMain
                ? ofOther(map.apply(getOther()))
                : (Either<TMain, S>) this;
    }

    /**
     * Transforms either the main value, or the alternative value.
     *
     * @param mapMain  Function to transform the Main
     * @param mapOther Function to transform the Other
     * @param <R>      New type for the Main
     * @param <S>      New type for the Other
     * @return New Either built from either the transformed Main, or transformed Other
     */
    public <R, S> Either<R, S> mapEither(Function<TMain, R> mapMain, Function<TOther, S> mapOther)
    {
        return isMain
                ? ofMain(mapMain.apply(valueMain))
                : ofOther(mapOther.apply(valueOther));
    }

    /**
     * Similar to map, but the function already returns the constructed Either
     *
     * @param map Function to transform the Main
     * @param <R> New type for the Main
     * @return
     */
    @SuppressWarnings("unchecked")
    public <R> Either<R, TOther> flatMap(Function<TMain, Either<R, TOther>> map)
    {
        return isMain
                ? map.apply(valueMain)
                : (Either<R, TOther>) this;
    }

    /**
     * Similar to map, but the function already returns the constructed Either
     *
     * @param map Function to transform the Other
     * @param <S> New type for the Other
     * @return
     */
    @SuppressWarnings("unchecked")
    public <S> Either<TMain, S> flatMapOther(Function<TOther, Either<TMain, S>> map)
    {
        return !isMain
                ? map.apply(valueOther)
                : (Either<TMain, S>) this;
    }

    /**
     * Similar to map, but the function already returns the constructed Either
     *
     * @param mapMain  Function to transform the Main
     * @param mapOther Function to transform the Other
     * @param <R>      New type for the Main
     * @param <S>      New type for the Other
     * @return The result of applying the corresponding function
     */
    public <R, S> Either<R, S> flatMapEither(Function<TMain, Either<R, S>> mapMain, Function<TOther, Either<R, S>> mapOther)
    {
        return isMain
                ? mapMain.apply(valueMain)
                : mapOther.apply(valueOther);
    }

    /**
     * Turns a Main into an Other, or an Other into a Main
     *
     * @return A new Either with the context flipped
     */
    public Either<TOther, TMain> flip()
    {
        return new Either<>(!isMain, valueOther, valueMain);
    }

    @Override
    public String toString()
    {
        return isMain
                ? String.format("Main[%s]", valueMain)
                : String.format("Other[%s]", valueOther);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Either<?, ?> either = (Either<?, ?>) o;
        return isMain == either.isMain && isMain ?
                Objects.equals(valueMain, either.valueMain) :
                Objects.equals(valueOther, either.valueOther);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(isMain, isMain ? valueMain : valueOther);
    }
}
