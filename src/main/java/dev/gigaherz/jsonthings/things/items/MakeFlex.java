package dev.gigaherz.jsonthings.things.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.JsonOps;
import dev.gigaherz.jsonthings.codegen.ClassMaker;
import dev.gigaherz.jsonthings.things.CompletionMode;
import dev.gigaherz.jsonthings.things.IFlexItem;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class MakeFlex
{
    public static void main(String[] args)
    {
        var perTabStacks = ClassMaker.fieldToken("perTabStacks", Multimap.class);
        var searchTabStacks = ClassMaker.fieldToken("searchTabStacks", List.class);
        var attributeModifiers = ClassMaker.fieldToken("attributeModifiers", Map.class);
        var eventHandlers = ClassMaker.fieldToken("eventHandlers", Map.class);
        var useAction = ClassMaker.fieldToken("useAction", UseAnim.class);
        var useTime = ClassMaker.fieldToken("useTime", int.class);
        var useFinishMode = ClassMaker.fieldToken("useFinishMode", CompletionMode.class);
        var containerResult = ClassMaker.fieldToken("containerResult", InteractionResultHolder.class);
        var lore = ClassMaker.fieldToken("lore", List.class);

        var builder = new ClassMaker(Thread.currentThread().getContextClassLoader()).begin()
                .setPublic().extending(Item.class).implementing(IFlexItem.class)
                .field(perTabStacks).setPrivate().setFinal().initializer(cb -> cb.staticCall(ArrayListMultimap.class, "create", ml -> ml))
                .field(searchTabStacks).setPrivate().setFinal().initializer(cb -> cb.staticCall(Lists.class, "newArrayList", ml -> ml))
                .field(attributeModifiers).setPrivate().setFinal().initializer(cb -> cb.staticCall(Maps.class, "newHashMap"))
                .field(eventHandlers).setPrivate().setFinal().initializer(cb -> cb.staticCall(Maps.class, "newHashMap"))

                .field(useAction).setPrivate()
                .field(useTime).setPrivate()
                .field(useFinishMode).setPrivate()
                .field(containerResult).setPrivate()
                .field(lore).setPrivate()

                .replicateParentConstructors(
                        cb -> cb
                                .autoSuperCall()
                                .exec(cb.thisVar().methodCall("initializeFlex"))
                                .returnVoid()
                )

                .method("initializeFlex", void.class).implementation(
                        cb -> {
                            var slot1 = ClassMaker.varToken("slot1", EquipmentSlot.class);
                            cb.forEach(slot1, cb.staticCall(EquipmentSlot.class, "values"),
                                    cf -> {
                                        cf
                                                .local("multimap", Multimap.class)
                                                .assign(cf.localRef("multimap"), cf.staticCall(ArrayListMultimap.class, "create", ml -> ml))
                                                .methodCall(cf.localVar("multimap"), "putAll",
                                                        cf.superVar().methodCall("getAttributeModifiers", cf.staticField(EquipmentSlot.class, "CHEST"), cf.staticField(ItemStack.class, "EMPTY")))
                                                .field(attributeModifiers).methodCall("put", cf.localVar(slot1), cf.localVar("multimap"));
                                    }
                            )
                            .returnVoid();
                        }
                )

                ;


    }

    /*

public class FlexItem extends Item implements IFlexItem
{
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
        tooltip.addAll(lore);
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

     */
}
