package gigaherz.jsonthings.things;

import gigaherz.jsonthings.things.events.BlockEventHandler;
import gigaherz.jsonthings.things.events.FlexEventContext;
import gigaherz.jsonthings.things.events.ItemEventHandler;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

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

    void addEventHandler(String eventName, BlockEventHandler eventHandler);

    @Nullable
    BlockEventHandler getEventHandler(String eventName);

    default ActionResultType runEvent(String eventName, FlexEventContext context, Supplier<ActionResultType> defaultValue)
    {
        BlockEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.get();
    }

}
