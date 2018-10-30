package gigaherz.jsonthings.block;

import net.minecraft.block.Block;

public interface IFlexBlock
{
    default Block self()
    {
        return (Block)this;
    }

}
