package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.builders.BlockBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public interface IBlockFactory<T extends Block & IFlexBlock>
{
    T construct(AbstractBlock.Properties properties, BlockBuilder builder);
}
