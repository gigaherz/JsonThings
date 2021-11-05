package dev.gigaherz.jsonthings.things.items;

import com.google.common.collect.*;
import dev.gigaherz.jsonthings.things.CompletionMode;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FlexTieredItem extends TieredItem implements IFlexItem
{
    public FlexTieredItem(IItemTier material, Properties properties)
    {
        super(material, properties);
        initializeFlex();
    }

    //region IFlexItem
    private final Multimap<ItemGroup, StackContext> perTabStacks = ArrayListMultimap.create();
    private final List<StackContext> searchTabStacks = Lists.newArrayList();
    private final Map<EquipmentSlotType, Multimap<Attribute, AttributeModifier>> attributeModifiers = Maps.newHashMap();
    private final Map<String, FlexEventHandler<ActionResult<ItemStack>>> eventHandlers = Maps.newHashMap();

    private UseAction useAction;
    private int useTime;
    private CompletionMode useFinishMode;
    private ActionResult<ItemStack> containerResult;
    private List<IFormattableTextComponent> lore;

    private void initializeFlex()
    {
        for (EquipmentSlotType slot1 : EquipmentSlotType.values())
        {
            Multimap<Attribute, AttributeModifier> multimap = ArrayListMultimap.create();
            multimap.putAll(super.getAttributeModifiers(EquipmentSlotType.CHEST, ItemStack.EMPTY));
            attributeModifiers.put(slot1, multimap);
        }
    }

    @Override
    public void setUseAction(UseAction useAction)
    {
        this.useAction = useAction;
    }

    @Override
    public UseAction getUseAction()
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
    public void addEventHandler(String eventName, FlexEventHandler<ActionResult<ItemStack>> eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    @Override
    public FlexEventHandler<ActionResult<ItemStack>> getEventHandler(String eventName)
    {
        return eventHandlers.get(eventName);
    }

    @Override
    public void addCreativeStack(StackContext stack, Iterable<ItemGroup> tabs)
    {
        for (ItemGroup tab : tabs)
        {
            perTabStacks.put(tab, stack);
        }
        searchTabStacks.add(stack);
    }

    @Override
    public void addAttributeModifier(@Nullable EquipmentSlotType slot, Attribute attribute, AttributeModifier modifier)
    {
        if (slot != null)
        {
            attributeModifiers.get(slot).put(attribute, modifier);
        }
        else
        {
            for (EquipmentSlotType slot1 : EquipmentSlotType.values())
            {attributeModifiers.get(slot1).put(attribute, modifier);}
        }
    }

    @Override
    public void setLore(List<IFormattableTextComponent> lore)
    {
        this.lore = lore;
    }

    //endregion

    //region Item
    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        ItemStack heldItem = context.getItemInHand();

        ActionResult<ItemStack> result = runEvent("use_on_block", FlexEventContext.of(context), () -> new ActionResult<>(super.useOn(context), heldItem));

        if (result.getObject() != heldItem)
        {
            context.getPlayer().setItemInHand(context.getHand(), result.getObject());
        }

        return result.getResult();
    }

    @Override
    public void releaseUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft)
    {
        runEvent("stopped_using",
                FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft),
                () -> {
                    super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
                    return new ActionResult<>(ActionResultType.PASS, stack);
                });
    }

    @Override
    public ItemStack finishUsingItem(ItemStack heldItem, World worldIn, LivingEntity entityLiving)
    {
        Supplier<ActionResult<ItemStack>> resultSupplier = () -> new ActionResult<>(ActionResultType.SUCCESS, super.finishUsingItem(heldItem, worldIn, entityLiving));

        ActionResult<ItemStack> result = runEvent("end_using", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier);
        if (result.getResult() != ActionResultType.SUCCESS)
            return result.getObject();

        return runEvent("use", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier).getObject();
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (useTime > 0)
            return runEvent("begin_using", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> super.use(worldIn, playerIn, handIn));
        else
            return runEvent("use_on_air", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> super.use(worldIn, playerIn, handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(lore);
    }

    @Override
    public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> items)
    {
        if (tab == ItemGroup.TAB_SEARCH)
        {
            items.addAll(searchTabStacks.stream().map(s -> s.toStack(this)).collect(Collectors.toList()));
        }
        else if (perTabStacks.containsKey(tab))
        {
            items.addAll(perTabStacks.get(tab).stream().map(s -> s.toStack(this)).collect(Collectors.toList()));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        ActionResult<ItemStack> result = runEvent("update",
                FlexEventContext.of(worldIn, entityIn, stack).with(FlexEventContext.SLOT, itemSlot).with(FlexEventContext.SELECTED, isSelected),
                () -> {
                    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
                    return new ActionResult<>(ActionResultType.PASS, stack);
                });
        if (result.getObject() != stack)
        {
            entityIn.setSlot(itemSlot, result.getObject());
        }
    }

    private ActionResult<ItemStack> doContainerItem(ItemStack stack)
    {
        return runEvent("get_container_item", FlexEventContext.of(stack), () -> {
            ActionResultType typeIn = super.hasContainerItem(stack) ? ActionResultType.SUCCESS : ActionResultType.PASS;
            if (typeIn == ActionResultType.SUCCESS)
                return new ActionResult<>(typeIn, super.getContainerItem(stack));
            return new ActionResult<>(typeIn, stack);
        });
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        containerResult = doContainerItem(stack);
        return containerResult.getResult() == ActionResultType.SUCCESS;
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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack)
    {
        return Utils.orElse(attributeModifiers.get(slot), HashMultimap::create);
    }

    //endregion
}
