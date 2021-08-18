package gigaherz.jsonthings.things.shapes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;
import java.util.function.Function;

public interface IShapeProvider
{
    Optional<VoxelShape> getShape(BlockState state, Direction facing);

    IShapeProvider bake(Function<String, Property<?>> propertyLookup);
}
