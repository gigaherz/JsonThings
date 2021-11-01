package dev.gigaherz.jsonthings.things.shapes;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gigaherz.jsonthings.util.CodecExtras;
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
    public static final BiMap<String, IBooleanFunction> BOOLEAN_OPERATORS = ImmutableBiMap.<String, IBooleanFunction>builder()
            .put("false", IBooleanFunction.FALSE)
            .put("not_or", IBooleanFunction.NOT_OR)
            .put("only_second", IBooleanFunction.ONLY_SECOND)
            .put("not_first", IBooleanFunction.NOT_FIRST)
            .put("only_first", IBooleanFunction.ONLY_FIRST)
            .put("not_second", IBooleanFunction.NOT_SECOND)
            .put("not_same", IBooleanFunction.NOT_SAME)
            .put("not_and", IBooleanFunction.NOT_AND)
            .put("and", IBooleanFunction.AND)
            .put("same", IBooleanFunction.SAME)
            .put("second", IBooleanFunction.SECOND)
            .put("causes", IBooleanFunction.CAUSES)
            .put("first", IBooleanFunction.FIRST)
            .put("caused_by", IBooleanFunction.CAUSED_BY)
            .put("or", IBooleanFunction.OR)
            .put("true", IBooleanFunction.TRUE)
            .build();
    public static final Codec<IBooleanFunction> BOOLEAN_OP_CODEC = CodecExtras.mappingCodec(Codec.STRING, BOOLEAN_OPERATORS::get, BOOLEAN_OPERATORS.inverse()::get);
    public static final Codec<CombinedShape> LIST_CODEC = CodecExtras.lazy(DynamicShape::shapeCodec).listOf().flatComapMap(
            list -> new CombinedShape(IBooleanFunction.OR, list),
            shape -> shape.operator == IBooleanFunction.OR
                    ? DataResult.success(shape.boxes)
                    : DataResult.error("Cannot use CombinedShape.LIST_CODEC to encode a CombinedShape whose boolean function is not OR")
    );
    public static final Codec<CombinedShape> OBJECT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BOOLEAN_OP_CODEC.fieldOf("op").forGetter(shape -> shape.operator),
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
