package gigaherz.jsonthings.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

public class ConditionalShape implements IShapeProvider
{
    public static final Codec<List<Pair<Property<?>, Set<Comparable<?>>>>> CONDITION_CODEC = Codec.unboundedMap(CodecExtras.PROPERTY_CODEC, CodecExtras.maybeList(Codec.STRING)).flatXmap(
            fromMap -> {
                List<DataResult<Pair<Property<?>,Set<Comparable<?>>>>> ret = fromMap.entrySet().stream().<DataResult<Pair<Property<?>,Set<Comparable<?>>>>>map(entry -> {
                    Property<?> property = entry.getKey();
                    List<String> second = entry.getValue();
                    List<Optional<?>> set = second.stream().map(property::parseValue).collect(Collectors.toList());
                    if (set.stream().allMatch(Optional::isPresent))
                        return DataResult.success(
                                Pair.of(property, set.stream().map(opt -> (Comparable<?>) opt.get()).collect(Collectors.toSet()))
                        );
                    return DataResult.error("One or more values are not valid for the property " + property.getName());
                }).collect(Collectors.toList());
                List<String> errors = ret.stream().map(DataResult::error).filter(Optional::isPresent).map(Optional::get).map(DataResult.PartialResult::message).collect(Collectors.toList());
                if (errors.size() == 0)
                {
                    List<Pair<Property<?>, Set<Comparable<?>>>> collect = ret.stream().map(dr -> dr.result().get()).collect(Collectors.toList());
                    return DataResult.success(collect);
                }
                else
                {
                    return DataResult.error(String.join("; ", errors));
                }
            },
            toMap -> {
                Map<Property<?>, List<String>> map = Maps.newHashMap();
                for(Pair<Property<?>, Set<Comparable<?>>> entry : toMap)
                {
                    Property<?> first = entry.getFirst();
                    map.put(first, entry.getSecond().stream().map(val -> ((Property)first).getName(val)).collect(Collectors.toList()));
                }
                return DataResult.success(map);
            }
    );

    public static final Codec<ConditionalShape> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            CodecExtras.maybeList(CONDITION_CODEC).fieldOf("when").forGetter(shape -> shape.conditions),
            CodecExtras.lazy(DynamicShape::shapeCodec).fieldOf("shape").forGetter(shape -> shape.shape)
    ).apply(instance, ConditionalShape::new));

    public final List<List<Pair<Property<?>, Set<Comparable<?>>>>> conditions = Lists.newArrayList();
    public final IShapeProvider shape;

    public ConditionalShape(List<List<Pair<Property<?>, Set<Comparable<?>>>>> conditions, IShapeProvider shape)
    {
        this.conditions.addAll(conditions);
        this.shape = shape;
    }

    @Override
    public Optional<VoxelShape> getShape(BlockState state, Direction facing)
    {
        return conditions.stream().anyMatch(condition -> condition.stream().allMatch(p -> p.getSecond().contains(state.get(p.getFirst()))))
                ? shape.getShape(state, facing)
                : Optional.empty();
    }
}
