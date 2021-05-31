package gigaherz.jsonthings.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.Optional;

@FunctionalInterface
public interface IShapeProvider
{
    Optional<VoxelShape> getShape(BlockState state, Direction facing);
}
