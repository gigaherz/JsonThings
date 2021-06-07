package gigaherz.jsonthings.things.shapes;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.util.CodecExtras;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CombinedShape implements IShapeProvider
{
    public static final Codec<CombinedShape> LIST_CODEC = CodecExtras.lazy(DynamicShape::shapeCodec).listOf().flatComapMap(
            list -> new CombinedShape(IBooleanFunction.OR, list),
            shape -> shape.operator == IBooleanFunction.OR
                    ? DataResult.success(shape.boxes)
                    : DataResult.error("Cannot use CombinedShape.LIST_CODEC to encode a CombinedShape whose boolean function is not OR")
    );
    public static final Codec<CombinedShape> OBJECT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            CodecExtras.registryNameCodec(ThingRegistries.BOOLEAN_FUNCTIONS).fieldOf("op").forGetter(shape -> shape.operator),
            CodecExtras.lazy(DynamicShape::shapeCodec).listOf().fieldOf("shapes").forGetter(shape -> shape.boxes)
    ).apply(instance, CombinedShape::new));
    public static final Codec<CombinedShape> CODEC = CodecExtras.makeChoiceCodec(LIST_CODEC, OBJECT_CODEC);

    public final IBooleanFunction operator;
    public final List<IShapeProvider> boxes = Lists.newArrayList();

    public CombinedShape(IBooleanFunction operator, Collection<IShapeProvider> boxes)
    {
        this.operator = operator;
        this.boxes.addAll(boxes);
    }

    @Override
    public Optional<VoxelShape> getShape(BlockState state, Direction facing)
    {
        return boxes.stream()
                .map(shape -> shape.getShape(state, facing))
                .reduce(Optional.empty(), (a, b) -> a.map(aa -> b.map(bb -> VoxelShapes.joinUnoptimized(aa, bb, operator))).orElse(b))
                .map(VoxelShape::optimize);
    }

    @Override
    public IShapeProvider bake(Function<String, Property<?>> propertyLookup)
    {
        return new CombinedShape(operator, boxes.stream().map(s -> s.bake(propertyLookup)).collect(Collectors.toList()));
    }
}
