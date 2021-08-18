package gigaherz.jsonthings.things.shapes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gigaherz.jsonthings.util.CodecExtras;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConditionalShape implements IShapeProvider
{
    public static final Codec<List<Pair<String, Set<String>>>> CONDITION_CODEC = Codec.unboundedMap(Codec.STRING, CodecExtras.maybeList(Codec.STRING)).xmap(
            fromMap -> fromMap.entrySet().stream()
                    .map(entry -> Pair.of(entry.getKey(), (Set<String>) Sets.newHashSet(entry.getValue())))
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
                Property<?> property = prop.get();
                if (!p.getSecond().contains(getPropertyValueByName(state, property)))
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

    private <T extends Comparable<T>> String getPropertyValueByName(BlockState state, Property<T> property)
    {
        return property.getName(state.getValue(property));
    }

    public IShapeProvider bake(Function<String, Property<?>> propertyLookup)
    {
        List<List<Pair<Property<?>, Set<Comparable<?>>>>> baked = conditions.stream().map(
                l -> l.stream().map(p -> parsePropertyValueSet(propertyLookup, p)).collect(Collectors.toList())
        ).collect(Collectors.toList());
        return new Baked(baked, shape.bake(propertyLookup));
    }

    private Pair<Property<?>, Set<Comparable<?>>> parsePropertyValueSet(Function<String, Property<?>> propertyLookup, Pair<String, Set<String>> condition)
    {
        String pName = condition.getFirst();
        Property<?> prop = propertyLookup.apply(pName);
        if (prop == null)
            throw new IllegalStateException("Property " + pName + " not declared in the block.");
        Set<Comparable<?>> values = condition.getSecond().stream().map(
                s -> parseValueFromProperty(prop, s)
        ).collect(Collectors.toSet());
        return Pair.of(prop, values);
    }

    private <T extends Comparable<T>> T parseValueFromProperty(Property<T> prop, String s)
    {
        return prop.getValue(s).orElseThrow(() -> new IllegalStateException("Property value " + s + " not valid in property " + prop.getName()));
    }

    public class Baked implements IShapeProvider
    {

        public final List<List<Pair<Property<?>, Set<Comparable<?>>>>> conditions = Lists.newArrayList();
        public final IShapeProvider shape;

        private Baked(List<List<Pair<Property<?>, Set<Comparable<?>>>>> conditions, IShapeProvider shape)
        {
            this.conditions.addAll(conditions);
            this.shape = shape;
        }

        @Override
        public Optional<VoxelShape> getShape(BlockState state, Direction facing)
        {
            for (List<Pair<Property<?>, Set<Comparable<?>>>> condition : conditions)
            {
                boolean allMatch = true;
                for (Pair<Property<?>, Set<Comparable<?>>> p : condition)
                {
                    Property<?> property = p.getFirst();
                    Set<Comparable<?>> set = p.getSecond();
                    if (!set.contains(state.getValue(property)))
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

        @Override
        public IShapeProvider bake(Function<String, Property<?>> propertyLookup)
        {
            return ConditionalShape.this.bake(propertyLookup);
        }
    }
}
