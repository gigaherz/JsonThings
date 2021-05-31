package gigaherz.jsonthings.things.shapes;

import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.Optional;
import java.util.function.Function;

public interface IShapeProvider
{
    Optional<VoxelShape> getShape(BlockState state, Direction facing);

    IShapeProvider bake(Function<String, Property<?>> propertyLookup);
}
