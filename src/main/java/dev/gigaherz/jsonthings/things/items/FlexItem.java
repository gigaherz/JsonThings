package dev.gigaherz.jsonthings.things.items;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.UseFinishMode;
import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.FuelValues;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlexItem extends Item implements IEventRunner
{
    public FlexItem(Properties properties, ItemBuilder builder)
    {
        super(properties);
        this.useAction = builder.getUseAnim();
        this.useTime = builder.getUseTime();
        this.useFinishMode = builder.getUseFinishMode();
        this.attributeModifiers = builder.getAttributeModifiers();
        this.lore = builder.getLore();
        this.toolActions = builder.getToolActions();
        this.burnTime = Utils.orElse(builder.getBurnDuration(), -1);
        initializeFlex();
    }

    //region IFlexItem
    @SuppressWarnings("rawtypes")
    private final Map<FlexEventType, FlexEventHandler> eventHandlers = Maps.newHashMap();

    private ItemAttributeModifiers attributeModifiers;
    private final ItemUseAnimation useAction;
    private final Integer useTime;
    private final UseFinishMode useFinishMode;
    private final List<MutableComponent> lore;
    private final Set<ItemAbility> toolActions;
    private final int burnTime;

    private void initializeFlex()
    {
        var builder = ItemAttributeModifiers.builder();
        var defaults = super.getDefaultAttributeModifiers(new ItemStack(this));
        if (!defaults.modifiers().isEmpty())
        {
            for (var mod : defaults.modifiers())
            {
                builder.add(mod.attribute(), mod.modifier(), mod.slot());
            }
            for (var mod : attributeModifiers.modifiers())
            {
                builder.add(mod.attribute(), mod.modifier(), mod.slot());
            }
            attributeModifiers = builder.build();
        }
    }

    @Override
    public <T> void addEventHandler(FlexEventType<T> event, FlexEventHandler<T> eventHandler)
    {
        eventHandlers.put(event, eventHandler);
    }

    @Override
    public <T> FlexEventHandler<T> getEventHandler(FlexEventType<T> event)
    {
        //noinspection unchecked
        return eventHandlers.get(event);
    }
    //endregion

    //region Item

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (useTime != null && useTime > 0)
            return runEvent(FlexEventType.BEGIN_USING_ITEM, FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> {
                playerIn.startUsingItem(handIn);
                return InteractionResult.CONSUME;
            });
        else
            return runEvent(FlexEventType.USE_ITEM_ON_AIR, FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> super.use(worldIn, playerIn, handIn));
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack heldItem = context.getItemInHand();

        var result = runEvent(FlexEventType.USE_ITEM_ON_BLOCK, FlexEventContext.of(context), () -> super.useOn(context));

        // Maybe not needed, test.
        if (result instanceof InteractionResult.Success success
                && success.heldItemTransformedTo() != null
                && success.heldItemTransformedTo() != heldItem && context.getPlayer() != null)
        {
            context.getPlayer().setItemInHand(context.getHand(), success.heldItemTransformedTo());
        }

        return result;
    }



    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack)
    {
        return Utils.orElseGet(useAction, () -> super.getUseAnimation(stack));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return Utils.orElseGet(useTime, () -> super.getUseDuration(stack, entity));
    }

    @Override
    public boolean useOnRelease(ItemStack stack)
    {
        if (useFinishMode != null)
            return useFinishMode.isUseOnRelease();
        return super.useOnRelease(stack);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft)
    {
        return runEvent(FlexEventType.STOPPED_USING,
                FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft),
                () -> super.releaseUsing(stack, worldIn, entityLiving, timeLeft));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack heldItem, Level worldIn, LivingEntity entityLiving)
    {
        var result = runEvent(FlexEventType.END_USING, FlexEventContext.of(worldIn, entityLiving, heldItem),
                () -> InteractionResult.SUCCESS.heldItemTransformedTo(super.finishUsingItem(heldItem, worldIn, entityLiving)));
        if (!(result instanceof InteractionResult.Success))
            return heldItem;

        var result2 = runEvent(FlexEventType.USE_ITEM_ON_AIR, FlexEventContext.of(worldIn, entityLiving, heldItem), () -> result);
        if (result2 instanceof InteractionResult.Success success)
            return Utils.orElse(success.heldItemTransformedTo(), heldItem);

        return heldItem;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (lore != null) tooltip.addAll(lore);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        var result = runEvent(FlexEventType.UPDATE,
                FlexEventContext.of(worldIn, entityIn, stack).with(FlexEventContext.SLOT, itemSlot).with(FlexEventContext.SELECTED, isSelected),
                () -> {
                    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
                    return stack;
                });
        if (result != stack)
        {
            entityIn.getSlot(itemSlot).set(result);
        }
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
    {
        return attributeModifiers;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction)
    {
        if (toolActions != null) return toolActions.contains(toolAction);
        return super.canPerformAction(stack, toolAction);
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, FuelValues fuelValues)
    {
        return burnTime;
    }

    //endregion
}
