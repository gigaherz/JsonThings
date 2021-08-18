package gigaherz.jsonthings.things.serializers;

import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.builders.BlockBuilder;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface IBlockFactory
{
    IFlexBlock construct(BlockBehaviour.Properties properties, BlockBuilder builder);
}
