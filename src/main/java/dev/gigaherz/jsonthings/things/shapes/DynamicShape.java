package dev.gigaherz.jsonthings.things.shapes;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gigaherz.jsonthings.util.CodecExtras;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class DynamicShape
{
    private static final Codec<IShapeProvider> SHAPE_CODEC = CodecExtras.makeChoiceCodec(
            CodecExtras.toSubclass(ConditionalShape.CODEC, ConditionalShape.class),
            CodecExtras.toSubclass(CombinedShape.CODEC, CombinedShape.class),
            CodecExtras.toSubclass(BasicShape.CODEC, BasicShape.class)
    );

    public static final Codec<DynamicShape> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            SHAPE_CODEC.fieldOf("shape").forGetter(shape -> shape.shape),
            CodecExtras.PROPERTY_CODEC.optionalFieldOf("shape_rotation").forGetter(shape -> Optional.ofNullable(shape.facing))
    ).apply(instance, (shape, facing) -> new DynamicShape(shape, (Property<Direction>) facing.orElse(null))));

    private static final DynamicShape EMPTY = new DynamicShape(new CombinedShape(IBooleanFunction.OR, Collections.emptyList()), null);

    public static DynamicShape empty()
    {
        return EMPTY;
    }

    public static Codec<IShapeProvider> shapeCodec()
    {
        return SHAPE_CODEC;
    }

    private final Map<BlockState, VoxelShape> shapeCache = new IdentityHashMap<>();
    private final IShapeProvider shape;
    @Nullable
    private final Property<Direction> facing;

    public DynamicShape(IShapeProvider shape, @Nullable Property<Direction> facing)
    {
        this.shape = shape;
        this.facing = facing;
    }

    public VoxelShape getShape(BlockState blockstate)
    {
        return shapeCache.computeIfAbsent(blockstate, state -> {
            Direction d = facing != null ? state.getValue(facing) : Direction.NORTH;
            return shape.getShape(state, d).orElseGet(VoxelShapes::block);
        });
    }

    public static DynamicShape fromJson(JsonElement data, @Nullable Property<Direction> facingProperty, Function<String, Property<?>> properties)
    {
        IShapeProvider shape = SHAPE_CODEC.decode(JsonOps.INSTANCE, data).getOrThrow(false, str -> {
        }).getFirst();
        shape = shape.bake(properties);
        return new DynamicShape(shape, facingProperty);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static VoxelShape cuboidWithRotation(Direction facing, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        switch (facing)
        {
            case NORTH:
                return VoxelShapes.box(x1, y1, z1, x2, y2, z2);
            case SOUTH:
                return VoxelShapes.box(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
            case WEST:
                return VoxelShapes.box(z1, y1, 1 - x2, z2, y2, 1 - x1);
            case EAST:
                return VoxelShapes.box(1 - z2, y1, x1, 1 - z1, y2, x2);
            case UP:
                return VoxelShapes.box(1 - y1, x1, z1, 1 - y2, x2, z2);
            case DOWN:
                return VoxelShapes.box(y1, 1 - x1, z1, y2, 1 - x2, z2);
        }
        return VoxelShapes.box(x1, y1, z1, x2, y2, z2);
    }
}
