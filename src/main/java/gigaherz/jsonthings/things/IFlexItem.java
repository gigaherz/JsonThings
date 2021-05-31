package gigaherz.jsonthings.things;

import gigaherz.jsonthings.things.builders.CompletionMode;
import gigaherz.jsonthings.things.builders.StackContext;
import gigaherz.jsonthings.things.events.FlexEventContext;
import gigaherz.jsonthings.things.events.ItemEventHandler;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface IFlexItem
{
    default Item self()
    {
        return (Item) this;
    }

    void setUseAction(UseAction useAction);

    UseAction getUseAction();

    void setUseTime(int useTicks);

    int getUseTime();

    void setUseFinishMode(CompletionMode onComplete);

    CompletionMode getUseFinishMode();

    void addEventHandler(String eventName, ItemEventHandler eventHandler);

    @Nullable
    ItemEventHandler getEventHandler(String eventName);

    void addCreativeStack(StackContext stack, Iterable<ItemGroup> tabs);

    void addAttributeModifier(@Nullable EquipmentSlotType slot, Attribute attribute, AttributeModifier modifier);

    default ActionResult<ItemStack> runEvent(String eventName, FlexEventContext context, Supplier<ActionResult<ItemStack>> defaultValue)
    {
        ItemEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.get();
    }

    static <T> T orElse(@Nullable T value, Supplier<T> fallback)
    {
        return value != null ? value : fallback.get();
    }
}
