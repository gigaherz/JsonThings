package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.builders.BlockBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface IBlockFactory<T extends Block & IFlexBlock>
{
    T construct(BlockBehaviour.Properties properties, BlockBuilder builder);
}
