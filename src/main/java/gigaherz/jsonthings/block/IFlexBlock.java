package gigaherz.jsonthings.block;

import gigaherz.jsonthings.block.builder.DynamicShape;
import net.minecraft.block.Block;

public interface IFlexBlock
{
    default Block self()
    {
        return (Block) this;
    }

    void setGeneralShape(DynamicShape shape);
    void setCollisionShape(DynamicShape shape);
    void setRaytraceShape(DynamicShape shape);
    void setRenderShape(DynamicShape shape);
}
