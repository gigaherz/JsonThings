package gigaherz.jsonthings.things;

import gigaherz.jsonthings.things.events.FlexEventContext;
import gigaherz.jsonthings.things.events.ItemEventHandler;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface IFlexItem
{
    void setUseAction(UseAnim useAction);

    UseAnim getUseAction();

    void setUseTime(int useTicks);

    int getUseTime();

    void setUseFinishMode(CompletionMode onComplete);

    CompletionMode getUseFinishMode();

    void addEventHandler(String eventName, ItemEventHandler eventHandler);

    @Nullable
    ItemEventHandler getEventHandler(String eventName);

    void addCreativeStack(StackContext stack, Iterable<CreativeModeTab> tabs);

    void addAttributeModifier(@Nullable EquipmentSlot slot, Attribute attribute, AttributeModifier modifier);

    default InteractionResultHolder<ItemStack> runEvent(String eventName, FlexEventContext context, Supplier<InteractionResultHolder<ItemStack>> defaultValue)
    {
        ItemEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.get();
    }

    default InteractionResultHolder<ItemStack> runEventThrowing(String eventName, FlexEventContext context, Callable<InteractionResultHolder<ItemStack>> defaultValue) throws Exception
    {
        ItemEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.call();
    }

    static <T> T orElse(@Nullable T value, Supplier<T> fallback)
    {
        return value != null ? value : fallback.get();
    }
}
