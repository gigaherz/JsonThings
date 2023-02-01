package dev.gigaherz.jsonthings.things.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.UseFinishMode;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class FlexArmorItem extends ArmorItem implements IFlexItem
{
    public FlexArmorItem(ArmorMaterial material, EquipmentSlot slot, Item.Properties properties,
                         @Nullable UseAnim useAction, @Nullable Integer useTime, @Nullable UseFinishMode useFinishMode,
                         Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers,
                         List<MutableComponent> lore)
    {
        super(material, slot, properties);
        this.useAction = useAction;
        this.useTime = useTime;
        this.useFinishMode = useFinishMode;
        this.attributeModifiers = attributeModifiers;
        this.lore = lore;
        initializeFlex();
    }

    //region IFlexItem
    private final Map<String, FlexEventHandler> eventHandlers = Maps.newHashMap();

    private final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers;
    private final UseAnim useAction;
    private final Integer useTime;
    private final UseFinishMode useFinishMode;
    private final List<MutableComponent> lore;

    private InteractionResultHolder<ItemStack> containerResult;

    private void initializeFlex()
    {
        for (EquipmentSlot slot1 : EquipmentSlot.values())
        {
            attributeModifiers.computeIfAbsent(slot1, key -> ArrayListMultimap.create())
                    .putAll(super.getAttributeModifiers(EquipmentSlot.CHEST, ItemStack.EMPTY));
        }
    }

    @Override
    public void addEventHandler(String eventName, FlexEventHandler eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    @Override
    public FlexEventHandler getEventHandler(String eventName)
    {
        return eventHandlers.get(eventName);
    }
    //endregion

    //region Item

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (useTime != null && useTime > 0)
            return runEvent("begin_using", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> {
                playerIn.startUsingItem(handIn);
                return FlexEventResult.consume(heldItem);
            }).holder();
        else
            return runEvent("use_on_air", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> FlexEventResult.of(super.use(worldIn, playerIn, handIn))).holder();
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack heldItem = context.getItemInHand();

        FlexEventResult result = runEvent("use_on_block", FlexEventContext.of(context), () -> new FlexEventResult(super.useOn(context), heldItem));

        if (result.stack() != heldItem && context.getPlayer() != null)
        {
            context.getPlayer().setItemInHand(context.getHand(), result.stack());
        }

        return result.result();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return Utils.orElseGet(useAction, () -> super.getUseAnimation(stack));
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return Utils.orElseGet(useTime, () -> super.getUseDuration(stack));
    }

    @Override
    public boolean useOnRelease(ItemStack stack)
    {
        if (useFinishMode != null)
            return useFinishMode.isUseOnRelease();
        return super.useOnRelease(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft)
    {
        runEvent("stopped_using",
                FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft),
                () -> {
                    super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
                    return FlexEventResult.pass(stack);
                });
    }

    @Override
    public ItemStack finishUsingItem(ItemStack heldItem, Level worldIn, LivingEntity entityLiving)
    {
        Supplier<FlexEventResult> resultSupplier = () -> FlexEventResult.success(super.finishUsingItem(heldItem, worldIn, entityLiving));

        FlexEventResult result = runEvent("end_using", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier);
        if (result.result() != InteractionResult.SUCCESS)
            return result.stack();

        return runEvent("use", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier).stack();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(lore);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        FlexEventResult result = runEvent("update",
                FlexEventContext.of(worldIn, entityIn, stack).with(FlexEventContext.SLOT, itemSlot).with(FlexEventContext.SELECTED, isSelected),
                () -> {
                    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
                    return FlexEventResult.pass(stack);
                });
        if (result.stack() != stack)
        {
            entityIn.getSlot(itemSlot).set(result.stack());
        }
    }

    private InteractionResultHolder<ItemStack> doContainerItem(ItemStack stack)
    {
        return runEvent("get_container_item", FlexEventContext.of(stack), () -> {
            if (super.hasCraftingRemainingItem(stack))
                return new FlexEventResult(InteractionResult.SUCCESS, super.getCraftingRemainingItem(stack));
            return new FlexEventResult(InteractionResult.PASS, ItemStack.EMPTY);
        }).holder();
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        containerResult = doContainerItem(stack);
        return containerResult.getResult() == InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        try
        {
            return Objects.requireNonNullElseGet(containerResult, () -> doContainerItem(itemStack)).getObject();
        }
        finally
        {
            containerResult = null;
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
    {
        return Utils.orElseGet(attributeModifiers.get(slot), HashMultimap::create);
    }

    //endregion
}
