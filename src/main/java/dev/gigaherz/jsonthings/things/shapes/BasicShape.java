package dev.gigaherz.jsonthings.things.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gigaherz.jsonthings.util.CodecExtras;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.DoubleStream;

public class BasicShape implements IShapeProvider
{
    public static final Codec<BasicShape> ARRAY_CODEC = CodecExtras.DOUBLE_STREAM.comapFlatMap((stream) ->
                    CodecExtras.validateDoubleStreamSize(stream, 6)
                            .map((nums) -> new BasicShape(nums[0], nums[1], nums[2], nums[3], nums[4], nums[5])),
            (pos) -> DoubleStream.of(pos.x1, pos.y1, pos.z1, pos.x2, pos.y2, pos.z2));
    public static final Codec<BasicShape> OBJECT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.DOUBLE.fieldOf("x1").forGetter(shape -> shape.x1),
            Codec.DOUBLE.fieldOf("y1").forGetter(shape -> shape.y1),
            Codec.DOUBLE.fieldOf("z1").forGetter(shape -> shape.z1),
            Codec.DOUBLE.fieldOf("x2").forGetter(shape -> shape.x2),
            Codec.DOUBLE.fieldOf("y2").forGetter(shape -> shape.y2),
            Codec.DOUBLE.fieldOf("z2").forGetter(shape -> shape.z2)
    ).apply(instance, BasicShape::new));
    public static final Codec<BasicShape> CODEC = CodecExtras.makeChoiceCodec(ARRAY_CODEC, OBJECT_CODEC);

    public final double x1;
    public final double y1;
    public final double z1;
    public final double x2;
    public final double y2;
    public final double z2;

    public BasicShape(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    @Override
    public Optional<VoxelShape> getShape(BlockState state, Direction facing)
    {
        return Optional.of(DynamicShape.cuboidWithRotation(facing, x1 / 16.0, y1 / 16.0, z1 / 16.0, x2 / 16.0, y2 / 16.0, z2 / 16.0));
    }

    @Override
    public IShapeProvider bake(Function<String, Property<?>> propertyLookup)
    {
        return this;
    }
}
