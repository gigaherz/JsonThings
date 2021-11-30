package dev.gigaherz.jsonthings.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class CodecExtras
{
    public static final Codec<Property<?>> PROPERTY_CODEC = registryNameCodec(ThingRegistries.PROPERTIES);

    public static final Codec<DoubleStream> DOUBLE_STREAM = Codec.DOUBLE.listOf().xmap(
            list -> list.stream().mapToDouble(d -> d),
            stream -> stream.boxed().collect(Collectors.toList())
    );

    public static DataResult<double[]> validateDoubleStreamSize(DoubleStream stream, int size)
    {
        double[] aint = stream.limit(size + 1).toArray();
        if (aint.length != size)
        {
            String s = "Input is not a list of " + size + " ints";
            return aint.length >= size ? DataResult.error(s, Arrays.copyOf(aint, size)) : DataResult.error(s);
        }
        else
        {
            return DataResult.success(aint);
        }
    }

    public static <T> Codec<List<T>> maybeList(Codec<T> codec)
    {
        return Codec.either(codec.listOf(), codec).xmap(
                either -> either.map(
                        left -> left,
                        Collections::singletonList
                ),
                list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list)
        );
    }

    public static <K, V> Codec<V> mappingCodec(Codec<K> keyCodec, Function<K, V> lookup, Function<V, K> inverseLookup)
    {
        return keyCodec.flatXmap(
                key -> {
                    V value = lookup.apply(key);
                    return value != null ? DataResult.success(value) : DataResult.error("The map does not contain any value with the given key");
                },
                value -> {
                    K key = inverseLookup.apply(value);
                    return key != null ? DataResult.success(key) : DataResult.error("Could not find a key in the map for the given value");
                }
        );
    }

    public static <T> Codec<T> registryNameCodec(Registry<T> registry)
    {
        return mappingCodec(ResourceLocation.CODEC, registry::get, registry::getKey);
    }

    public static <R, T extends R> Codec<R> toSubclass(Codec<T> codec, Class<T> subclass)
    {
        return codec.flatComapMap(
                m -> m,
                v -> subclass.isInstance(v) ? DataResult.success(subclass.cast(v)) : DataResult.error("Value " + v + "is not of type " + subclass.getName())
        );
    }

    @SafeVarargs
    public static <T> Codec<T> makeChoiceCodec(Codec<T> _choice, Codec<T>... _choices)
    {
        //noinspection UnstableApiUsage
        return new Codec<T>()
        {
            final List<Codec<T>> choices = Stream.concat(Stream.of(_choice), Arrays.stream(_choices)).collect(ImmutableList.toImmutableList());

            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input)
            {
                return processChoices(choice -> choice.decode(ops, input), "Could not decode with any of the options.");
            }

            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix)
            {
                return processChoices(choice -> choice.encode(input, ops, prefix), "Could not encode with any of the options.");
            }

            private <T1> DataResult<T1> processChoices(Function<Codec<T>, DataResult<T1>> action, String errMessage)
            {
                StringBuilder builder = null;
                for (Codec<T> choice : choices)
                {
                    DataResult<T1> result = action.apply(choice);
                    Optional<T1> success = result.result();
                    if (success.isPresent())
                        return DataResult.success(success.get());
                    final StringBuilder b = builder == null ? (builder = new StringBuilder()) : builder;
                    Optional<DataResult.PartialResult<T1>> error = result.error();
                    error.ifPresent(err -> {
                        b.append("\n");
                        b.append(err.message());
                    });
                }
                if (builder != null)
                {
                    builder.append("\nEnd choices.");
                    return DataResult.error(errMessage + " Errors: " + builder);
                }
                return DataResult.error("No codecs?!");
            }

            @Override
            public String toString()
            {
                return String.format("ChoiceCodec[%s]", choices.stream().map(Codec::toString).collect(Collectors.joining(",")));
            }
        };
    }

    public static <T> Codec<T> lazy(Supplier<Codec<T>> codecSupplier)
    {
        return new Codec<T>()
        {
            final Supplier<Codec<T>> supplier = codecSupplier;
            Codec<T> resolved = null;

            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input)
            {
                return (resolved == null ? (resolved = supplier.get()) : resolved).decode(ops, input);
            }

            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix)
            {
                return (resolved == null ? (resolved = supplier.get()) : resolved).encode(input, ops, prefix);
            }

            @Override
            public String toString()
            {
                if (resolved != null)
                    return String.format("LazyCodec[%s]", resolved);
                return "LazyCodec[not resolved]";
            }
        };
    }
}
