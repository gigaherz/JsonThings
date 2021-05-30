package gigaherz.jsonthings.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import gigaherz.jsonthings.microregistries.ThingsByName;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class CodecExtras
{
    public static final Codec<Property<?>> PROPERTY_CODEC = registryNameCodec(ThingsByName.PROPERTIES);

    public static final Codec<DoubleStream> DOUBLE_STREAM = Codec.DOUBLE.listOf().xmap(
            list -> list.stream().mapToDouble(d -> d),
            stream -> stream.boxed().collect(Collectors.toList())
    );

    public static DataResult<double[]> validateDoubleStreamSize(DoubleStream stream, int size) {
        double[] aint = stream.limit(size + 1).toArray();
        if (aint.length != size) {
            String s = "Input is not a list of " + size + " ints";
            return aint.length >= size ? DataResult.error(s, Arrays.copyOf(aint, size)) : DataResult.error(s);
        } else {
            return DataResult.success(aint);
        }
    }

    public static <K,V> Codec<V> mappingCodec(Codec<K> keyCodec, Function<K,V> lookup, Function<V,K> inverseLookup)
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

    public static <T> Codec<T> registryNameCodec(SimpleRegistry<T> registry)
    {
        return mappingCodec(ResourceLocation.CODEC, registry::getOrDefault, registry::getKey);
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
                StringBuilder builder = null;
                for(Codec<T> choice : choices)
                {
                    DataResult<Pair<T, T1>> result = choice.decode(ops, input);
                    Optional<Pair<T, T1>> result1 = result.result();
                    if (result1.isPresent())
                        return DataResult.success(result1.map(p -> Pair.of(p.getFirst(), p.getSecond())).get());
                    final StringBuilder b = builder == null ? (builder = new StringBuilder()) : builder;
                    Optional<DataResult.PartialResult<Pair<T, T1>>> error = result.error();
                    error.ifPresent(err -> { b.append(err.message()); b.append(System.lineSeparator()); });
                }
                if (builder != null)
                {
                    return DataResult.error("Could not decode with any of the options. Errors: " + builder.toString());
                }
                return DataResult.error("No codecs?!");
            }

            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix)
            {
                StringBuilder builder = null;
                for(Codec<T> choice : choices)
                {
                    DataResult<T1> result = choice.encode(input, ops, prefix);
                    Optional<T1> result1 = result.result();
                    if (result1.isPresent())
                        return DataResult.success(result1.get());
                    final StringBuilder b = builder == null ? (builder = new StringBuilder()) : builder;
                    Optional<DataResult.PartialResult<T1>> error = result.error();
                    error.ifPresent(err -> { b.append(err.message()); b.append(System.lineSeparator()); });
                }
                if (builder != null)
                {
                    return DataResult.error("Could not encode with any of the options. Errors: " + builder.toString());
                }
                return DataResult.error("No codecs?!");
            }
        };
    }
}
