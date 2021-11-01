package dev.gigaherz.jsonthings.things;

import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.text.IFormattableTextComponent;

import javax.annotation.Nullable;
import java.util.List;

public interface IFlexItem extends IEventRunner<ActionResult<ItemStack>>
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

    void addCreativeStack(StackContext stack, Iterable<ItemGroup> tabs);

    void addAttributeModifier(@Nullable EquipmentSlotType slot, Attribute attribute, AttributeModifier modifier);

    void setLore(List<IFormattableTextComponent> lore);
}
