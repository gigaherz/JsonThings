package dev.gigaherz.jsonthings.things.items;

import com.google.common.collect.*;
import dev.gigaherz.jsonthings.things.CompletionMode;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.core.NonNullList;
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
import java.util.function.Supplier;

public class FlexItem extends Item implements IFlexItem
{
    public FlexItem(Item.Properties properties)
    {
        super(properties);
        initializeFlex();
    }

    //region IFlexItem
    private final Multimap<CreativeModeTab, StackContext> perTabStacks = ArrayListMultimap.create();
    private final List<StackContext> searchTabStacks = Lists.newArrayList();
    private final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers = Maps.newHashMap();
    private final Map<String, FlexEventHandler> eventHandlers = Maps.newHashMap();

    private UseAnim useAction;
    private int useTime;
    private CompletionMode useFinishMode;
    private InteractionResultHolder<ItemStack> containerResult;
    private List<MutableComponent> lore;

    private void initializeFlex()
    {
        for (EquipmentSlot slot1 : EquipmentSlot.values())
        {
            Multimap<Attribute, AttributeModifier> multimap = ArrayListMultimap.create();
            multimap.putAll(super.getAttributeModifiers(EquipmentSlot.CHEST, ItemStack.EMPTY));
            attributeModifiers.put(slot1, multimap);
        }
    }

    @Override
    public void setUseAction(UseAnim useAction)
    {
        this.useAction = useAction;
    }

    @Override
    public UseAnim getUseAction()
    {
        return useAction;
    }

    @Override
    public void setUseTime(int useTicks)
    {
        this.useTime = useTicks;
    }

    @Override
    public int getUseTime()
    {
        return useTime;
    }

    @Override
    public void setUseFinishMode(CompletionMode onComplete)
    {
        this.useFinishMode = onComplete;
    }

    @Override
    public CompletionMode getUseFinishMode()
    {
        return useFinishMode;
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

    @Override
    public void addCreativeStack(StackContext stack, Iterable<CreativeModeTab> tabs)
    {
        for (CreativeModeTab tab : tabs)
        {
            perTabStacks.put(tab, stack);
        }
        searchTabStacks.add(stack);
    }

    @Override
    public void addAttributeModifier(@Nullable EquipmentSlot slot, Attribute attribute, AttributeModifier modifier)
    {
        if (slot != null)
        {
            attributeModifiers.get(slot).put(attribute, modifier);
        }
        else
        {
            for (EquipmentSlot slot1 : EquipmentSlot.values())
            {attributeModifiers.get(slot1).put(attribute, modifier);}
        }
    }

    @Override
    public void setLore(List<MutableComponent> lore)
    {
        this.lore = lore;
    }

    //endregion

    //region Item
    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack heldItem = context.getItemInHand();

        FlexEventResult result = runEvent("use_on_block", FlexEventContext.of(context), () -> new FlexEventResult(super.useOn(context), heldItem));

        if (result.stack() != heldItem)
        {
            context.getPlayer().setItemInHand(context.getHand(), result.stack());
        }

        return result.result();
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
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (useTime > 0)
            return runEvent("begin_using", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> FlexEventResult.of(super.use(worldIn, playerIn, handIn))).holder();
        else
            return runEvent("use_on_air", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> FlexEventResult.of(super.use(worldIn, playerIn, handIn))).holder();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(lore);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items)
    {
        if (tab == CreativeModeTab.TAB_SEARCH)
        {
            searchTabStacks.stream().map(s -> s.toStack(this)).forEach(items::add);
        }
        else if (perTabStacks.containsKey(tab))
        {
            perTabStacks.get(tab).stream().map(s -> s.toStack(this)).forEach(items::add);
        }
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
            InteractionResult typeIn = super.hasContainerItem(stack) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            if (typeIn == InteractionResult.SUCCESS)
                return new FlexEventResult(typeIn, super.getContainerItem(stack));
            return new FlexEventResult(typeIn, stack);
        }).holder();
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        containerResult = doContainerItem(stack);
        return containerResult.getResult() == InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        try
        {
            if (containerResult != null)
                return containerResult.getObject();
            return doContainerItem(itemStack).getObject();
        }
        finally
        {
            containerResult = null;
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
    {
        return Utils.orElse(attributeModifiers.get(slot), HashMultimap::create);
    }

    //endregion
}
