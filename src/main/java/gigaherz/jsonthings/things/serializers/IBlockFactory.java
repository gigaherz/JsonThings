package gigaherz.jsonthings.things.serializers;

import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.builders.BlockBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public interface IBlockFactory
{
    IFlexBlock construct(AbstractBlock.Properties properties, BlockBuilder builder);
}
