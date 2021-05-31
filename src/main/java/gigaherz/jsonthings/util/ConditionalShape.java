package gigaherz.jsonthings.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConditionalShape implements IShapeProvider
{
    public static final Codec<List<Pair<String, Set<String>>>> CONDITION_CODEC = Codec.unboundedMap(Codec.STRING, CodecExtras.maybeList(Codec.STRING)).xmap(
            fromMap -> fromMap.entrySet().stream()
                    .map(entry -> Pair.of(entry.getKey(), (Set<String>)Sets.newHashSet(entry.getValue())))
                    .collect(Collectors.toList()),
            toMap -> toMap.stream().collect(Collectors.toMap(Pair::getFirst, entry -> Lists.newArrayList(entry.getSecond()), (a, b) -> b))
    );

    public static final Codec<ConditionalShape> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            CodecExtras.maybeList(CONDITION_CODEC).fieldOf("when").forGetter(shape -> shape.conditions),
            CodecExtras.lazy(DynamicShape::shapeCodec).fieldOf("shape").forGetter(shape -> shape.shape)
    ).apply(instance, ConditionalShape::new));

    public final List<List<Pair<String, Set<String>>>> conditions = Lists.newArrayList();
    public final IShapeProvider shape;

    public ConditionalShape(List<List<Pair<String, Set<String>>>> conditions, IShapeProvider shape)
    {
        this.conditions.addAll(conditions);
        this.shape = shape;
    }

    @Override
    public Optional<VoxelShape> getShape(BlockState state, Direction facing)
    {
        for (List<Pair<String, Set<String>>> condition : conditions)
        {
            boolean allMatch = true;
            for (Pair<String, Set<String>> p : condition)
            {
                Optional<Property<?>> prop = state.getProperties().stream().filter(pr -> pr.getName().equals(p.getFirst())).findFirst();
                if (!prop.isPresent())
                    throw new IllegalStateException("Property not found " + p.getFirst());
                Property property = prop.get();
                if (!p.getSecond().contains(property.getName(state.get(property))))
                {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch)
            {
                return shape.getShape(state, facing);
            }
        }
        return Optional.empty();
    }
}
