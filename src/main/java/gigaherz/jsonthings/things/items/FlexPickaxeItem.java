package gigaherz.jsonthings.things.items;

import com.google.common.collect.*;
import gigaherz.jsonthings.things.CompletionMode;
import gigaherz.jsonthings.things.IFlexItem;
import gigaherz.jsonthings.things.StackContext;
import gigaherz.jsonthings.things.events.FlexEventContext;
import gigaherz.jsonthings.things.events.FlexEventHandler;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
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
import java.util.stream.Collectors;

public class FlexPickaxeItem extends PickaxeItem implements IFlexItem
{
    public FlexPickaxeItem(Tier material, int damage, float speed, Item.Properties properties)
    {
        super(material, damage, speed, properties);
        initializeFlex();
    }

    //region IFlexItem
    private final Multimap<CreativeModeTab, StackContext> perTabStacks = ArrayListMultimap.create();
    private final List<StackContext> searchTabStacks = Lists.newArrayList();
    private final List<Component> tooltipStrings = Lists.newArrayList();
    private final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributeModifiers = Maps.newHashMap();
    private final Map<String, FlexEventHandler<InteractionResultHolder<ItemStack>>> eventHandlers = Maps.newHashMap();

    private UseAnim useAction;
    private int useTime;
    private CompletionMode useFinishMode;
    private InteractionResultHolder<ItemStack> containerResult;

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
    public void addEventHandler(String eventName, FlexEventHandler<InteractionResultHolder<ItemStack>> eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    @Override
    public FlexEventHandler<InteractionResultHolder<ItemStack>> getEventHandler(String eventName)
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
    //endregion

    //region Item
    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack heldItem = context.getItemInHand();

        InteractionResultHolder<ItemStack> result = runEvent("use_on_block", FlexEventContext.of(context), () -> new InteractionResultHolder<>(super.useOn(context), heldItem));

        if (result.getObject() != heldItem)
        {
            context.getPlayer().setItemInHand(context.getHand(), result.getObject());
        }

        return result.getResult();
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft)
    {
        runEvent("stopped_using",
                FlexEventContext.of(worldIn, entityLiving, stack).with(FlexEventContext.TIME_LEFT, timeLeft),
                () -> {
                    super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
                    return new InteractionResultHolder<>(InteractionResult.PASS, stack);
                });
    }

    @Override
    public ItemStack finishUsingItem(ItemStack heldItem, Level worldIn, LivingEntity entityLiving)
    {
        Supplier<InteractionResultHolder<ItemStack>> resultSupplier = () -> new InteractionResultHolder<>(InteractionResult.SUCCESS, super.finishUsingItem(heldItem, worldIn, entityLiving));

        InteractionResultHolder<ItemStack> result = runEvent("end_using", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier);
        if (result.getResult() != InteractionResult.SUCCESS)
            return result.getObject();

        return runEvent("use", FlexEventContext.of(worldIn, entityLiving, heldItem), resultSupplier).getObject();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if (useTime > 0)
            return runEvent("begin_using", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> super.use(worldIn, playerIn, handIn));
        else
            return runEvent("use_on_air", FlexEventContext.of(worldIn, playerIn, handIn, heldItem), () -> super.use(worldIn, playerIn, handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(tooltipStrings);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items)
    {
        if (tab == CreativeModeTab.TAB_SEARCH)
        {
            items.addAll(searchTabStacks.stream().map(s -> s.toStack(this)).collect(Collectors.toList()));
        }
        else if (perTabStacks.containsKey(tab))
        {
            items.addAll(perTabStacks.get(tab).stream().map(s -> s.toStack(this)).collect(Collectors.toList()));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        InteractionResultHolder<ItemStack> result = runEvent("update",
                FlexEventContext.of(worldIn, entityIn, stack).with(FlexEventContext.SLOT, itemSlot).with(FlexEventContext.SELECTED, isSelected),
                () -> {
                    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
                    return new InteractionResultHolder<>(InteractionResult.PASS, stack);
                });
        if (result.getObject() != stack)
        {
            entityIn.getSlot(itemSlot).set(result.getObject());
        }
    }

    private InteractionResultHolder<ItemStack> doContainerItem(ItemStack stack)
    {
        return runEvent("get_container_item", FlexEventContext.of(stack), () -> {
            InteractionResult typeIn = super.hasContainerItem(stack) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            if (typeIn == InteractionResult.SUCCESS)
                return new InteractionResultHolder<>(typeIn, super.getContainerItem(stack));
            return new InteractionResultHolder<>(typeIn, stack);
        });
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
