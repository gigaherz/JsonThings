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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.ToolAction;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class FlexBucketItem extends BucketItem implements IEventRunner
{
    public FlexBucketItem(Supplier<Fluid> fluid, Properties properties, ItemBuilder builder)
    {
        super(fluid.get(), properties);
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
    private final UseAnim useAction;
    private final Integer useTime;
    private final UseFinishMode useFinishMode;
    private final List<MutableComponent> lore;
    private final Set<ToolAction> toolActions;
    private final int burnTime;

    private void initializeFlex()
    {
        var builder = ItemAttributeModifiers.builder();
        //noinspection deprecation
        var defaults = super.getDefaultAttributeModifiers();
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
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (useTime != null && useTime > 0)
            return runEvent(FlexEventType.BEGIN_USING_ITEM, FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> {
                playerIn.startUsingItem(handIn);
                return InteractionResultHolder.consume(heldItem);
            });
        else
            return runEvent(FlexEventType.USE_ITEM_ON_AIR, FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> super.use(worldIn, playerIn, handIn));
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack heldItem = context.getItemInHand();

        var result = runEvent(FlexEventType.USE_ITEM_ON_BLOCK, FlexEventContext.of(context), () -> new InteractionResultHolder<>(super.useOn(context), heldItem));

        if (result.getObject() != heldItem && context.getPlayer() != null)
        {
            context.getPlayer().setItemInHand(context.getHand(), result.getObject());
        }

        return result.getResult();
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
        runEvent(FlexEventType.STOPPED_USING,
                FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft),
                () -> {
                    super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
                    return null;
                });
    }

    @Override
    public ItemStack finishUsingItem(ItemStack heldItem, Level worldIn, LivingEntity entityLiving)
    {
        var result = runEvent(FlexEventType.END_USING, FlexEventContext.of(worldIn, entityLiving, heldItem), () -> InteractionResultHolder.success(super.finishUsingItem(heldItem, worldIn, entityLiving)));
        if (result.getResult() != InteractionResult.SUCCESS)
            return result.getObject();

        return runEvent(FlexEventType.USE_ITEM_ON_AIR, FlexEventContext.of(worldIn, entityLiving, heldItem), () -> result).getObject();
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

    @SuppressWarnings("deprecation")
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers()
    {
        return attributeModifiers;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction)
    {
        if (toolActions != null) return toolActions.contains(toolAction);
        return super.canPerformAction(stack, toolAction);
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @org.jetbrains.annotations.Nullable RecipeType<?> recipeType)
    {
        return burnTime;
    }

    //endregion
}
