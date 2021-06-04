package gigaherz.jsonthings.things.items;

import com.google.common.collect.*;
import gigaherz.jsonthings.things.IFlexItem;
import gigaherz.jsonthings.things.builders.CompletionMode;
import gigaherz.jsonthings.things.builders.StackContext;
import gigaherz.jsonthings.things.events.FlexEventContext;
import gigaherz.jsonthings.things.events.ItemEventHandler;
import net.minecraft.block.Block;
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
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FlexBlockNamedItem extends BlockNamedItem implements IFlexItem
{
    public FlexBlockNamedItem(Block block, Item.Properties properties)
    {
        super(block, properties);

        initializeFlex();

        eventHandlers.put("use_on_block", (eventName, context) -> {
            BlockRayTraceResult trace = new BlockRayTraceResult(
                    context.get(FlexEventContext.HIT_VEC),
                    context.get(FlexEventContext.HIT_FACE),
                    context.get(FlexEventContext.HIT_POS),
                    context.get(FlexEventContext.HIT_INSIDE));
            return new ActionResult<>(super.onItemUse(new ItemUseContext((PlayerEntity) context.get(FlexEventContext.USER), context.get(FlexEventContext.HAND), trace)), context.getStack());
        });
    }

    //region IFlexItem
    private final Multimap<ItemGroup, StackContext> perTabStacks = ArrayListMultimap.create();
    private final List<StackContext> searchTabStacks = Lists.newArrayList();
    private final List<ITextComponent> tooltipStrings = Lists.newArrayList();
    private final Map<EquipmentSlotType, Multimap<Attribute, AttributeModifier>> attributeModifiers = Maps.newHashMap();
    private final Map<String, ItemEventHandler> eventHandlers = Maps.newHashMap();

    private UseAction useAction;
    private int useTime;
    private CompletionMode useFinishMode;

    private void initializeFlex()
    {
        for (EquipmentSlotType slot1 : EquipmentSlotType.values())
        {
            Multimap<Attribute, AttributeModifier> multimap = ArrayListMultimap.create();
            multimap.putAll(super.getAttributeModifiers(EquipmentSlotType.CHEST, ItemStack.EMPTY));
            attributeModifiers.put(slot1, multimap);
        }

        eventHandlers.put("get_container_item", (eventName, context) -> new ActionResult<>(ActionResultType.SUCCESS, super.getContainerItem(context.getStack())));
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
    public void addEventHandler(String eventName, ItemEventHandler eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    @Override
    public ItemEventHandler getEventHandler(String eventName)
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
            { attributeModifiers.get(slot1).put(attribute, modifier); }
        }
    }
    //endregion

    //region Item
    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        ItemStack heldItem = context.getItem();
        if (useTime > 0)
            return ActionResultType.PASS;

        return runEvent("use_on_block", FlexEventContext.of(context), () -> new ActionResult<>(ActionResultType.PASS, heldItem)).getType();
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft)
    {
        runEvent("stopped_using", FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft), () -> new ActionResult<>(ActionResultType.PASS, stack));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack heldItem, World worldIn, LivingEntity entityLiving)
    {
        Supplier<ActionResult<ItemStack>> resultSupplier = () -> new ActionResult<>(ActionResultType.SUCCESS, heldItem);

        ActionResult<ItemStack> result = runEvent("end_using", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier);
        if (result.getType() != ActionResultType.SUCCESS)
            return result.getResult();

        return runEvent("use", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier).getResult();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (useTime > 0)
            return runEvent("begin_using", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> new ActionResult<>(ActionResultType.SUCCESS, heldItem));
        else
            return runEvent("use_on_air", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> new ActionResult<>(ActionResultType.PASS, heldItem));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(tooltipStrings);
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items)
    {
        if (tab == ItemGroup.SEARCH)
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
                () -> new ActionResult<>(ActionResultType.PASS, stack));
        if (!ItemStack.areItemStacksEqual(result.getResult(), stack))
        {
            entityIn.replaceItemInInventory(itemSlot, result.getResult());
        }
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return runEvent("get_container_item", FlexEventContext.of(itemStack), () -> new ActionResult<>(ActionResultType.PASS, itemStack)).getResult();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack)
    {
        return IFlexItem.orElse(attributeModifiers.get(slot), () -> HashMultimap.create());
    }

    //endregion
}
