package gigaherz.jsonthings.things.serializers;

import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.builders.BlockBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface IBlockFactory<T extends Block & IFlexBlock>
{
    T construct(BlockBehaviour.Properties properties, BlockBuilder builder);
}
