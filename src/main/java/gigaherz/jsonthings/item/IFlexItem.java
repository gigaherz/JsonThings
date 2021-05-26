package gigaherz.jsonthings.item;

import gigaherz.jsonthings.item.builder.CompletionMode;
import gigaherz.jsonthings.item.context.FlexEventContext;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
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

    void addAttributemodifier(@Nullable EquipmentSlotType slot, String attributeName, AttributeModifier modifier);

    default ActionResult<ItemStack> runEvent(String eventName, FlexEventContext context, Supplier<ActionResult<ItemStack>> defaultValue)
    {
        ItemEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.get();
    }
}
