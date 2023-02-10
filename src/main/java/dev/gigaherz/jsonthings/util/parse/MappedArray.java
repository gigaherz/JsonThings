package dev.gigaherz.jsonthings.util.parse;

import dev.gigaherz.jsonthings.util.parse.function.IntObjBiConsumer;
import dev.gigaherz.jsonthings.util.parse.value.MappedArrayValue;
import dev.gigaherz.jsonthings.util.parse.value.MappedValue;
import net.minecraft.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

class MappedArray<M> implements MappedArrayValue<M>
{
    private final String path;
    private final List<M> items;

    public MappedArray(String path, List<M> items)
    {
        this.path = path;
        this.items = items;
    }

    @Override
    public void forEach(IntObjBiConsumer<M> visitor)
    {
        for (int i = 0; i < items.size(); i++)
        {
            visitor.accept(i, items.get(i));
        }
    }

    @Override
    public void collect(Consumer<Stream<M>> collector)
    {
        collector.accept(items.stream());
    }

    @Override
    public <T> MappedArrayValue<T> map(Function<M, T> mapping)
    {
        final List<T> items = Util.make(new ArrayList<>(), list -> {
            for (var e : this.items)
            {
                list.add(mapping.apply(e));
            }
        });

        return new MappedArray<>(path, items);
    }

    @Override
    public <T> T flatMap(Function<Stream<M>, T> collector)
    {
        return collector.apply(items.stream());
    }

    @Override
    public MappedArrayValue<M> notEmpty()
    {
        if (items.size() == 0)
        {
            throw new JParseException("Json Array at '" + path + "' must not be empty.");
        }
        return this;
    }

    @Override
    public MappedArrayValue<M> atLeast(int min)
    {
        if (items.size() < min)
        {
            throw new JParseException("Json Array at '" + path + "' must contain at least " + min + ".");
        }
        return this;
    }

    @Override
    public MappedArrayValue<M> between(int min, int maxExclusive)
    {
        if (items.size() < min)
        {
            throw new JParseException("Json Array at '" + path + "' must contain at least " + min + ".");
        }
        return this;
    }

    @Override
    public <T> MappedValue<T[]> flatten(Function<M, T> mapping, IntFunction<T[]> factory)
    {
        return new MappedValue.Impl<>(items.stream().map(mapping).toArray(factory));
    }
}
