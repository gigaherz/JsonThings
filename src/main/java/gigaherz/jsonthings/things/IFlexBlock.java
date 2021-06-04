package gigaherz.jsonthings.things;

import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.block.Block;

import javax.annotation.Nullable;

public interface IFlexBlock
{
    default Block self()
    {
        return (Block) this;
    }

    void setGeneralShape(@Nullable DynamicShape shape);

    void setCollisionShape(@Nullable DynamicShape shape);

    void setRaytraceShape(@Nullable DynamicShape shape);

    void setRenderShape(@Nullable DynamicShape shape);
}
