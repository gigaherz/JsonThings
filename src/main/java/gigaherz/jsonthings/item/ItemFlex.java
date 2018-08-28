package gigaherz.jsonthings.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import gigaherz.jsonthings.item.builder.DelayedUse;
import gigaherz.jsonthings.item.builder.StackContext;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemFlex extends Item implements IFlexItem
{
    public ItemFlex()
    {
        initializeDefaultStuff();
    }

    //region IFlexItem
    private final Multimap<CreativeTabs, StackContext> perTabStacks = ArrayListMultimap.create();
    private final List<StackContext> searchTabStacks = Lists.newArrayList();
    private final List<ITextComponent> tooltipStrings = Lists.newArrayList();
    private final Map<EntityEquipmentSlot, Multimap<String, AttributeModifier>> attributeModifiers = Maps.newHashMap();
    private final Map<String, ItemEventHandler> eventHandlers = Maps.newHashMap();

    private EnumAction useAction;
    private int useTime;
    private DelayedUse.CompletionMode useFinishMode;

    private void initializeDefaultStuff()
    {
        for(EntityEquipmentSlot slot1 : EntityEquipmentSlot.values())
        {
            Multimap<String, AttributeModifier> multimap = ArrayListMultimap.create();
            multimap.putAll(super.getAttributeModifiers(EntityEquipmentSlot.CHEST, ItemStack.EMPTY));
            attributeModifiers.put(slot1, multimap);
        }

        eventHandlers.put("get_container_item", (eventName, player, hand, stack) -> new ActionResult<>(EnumActionResult.SUCCESS, super.getContainerItem(stack)));
    }

    @Override
    public void setUseAction(EnumAction useAction)
    {
        this.useAction = useAction;
    }

    @Override
    public EnumAction getUseAction()
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
    public void setUseFinishMode(DelayedUse.CompletionMode onComplete)
    {
        this.useFinishMode = onComplete;
    }

    @Override
    public DelayedUse.CompletionMode getUseFinishMode()
    {
        return useFinishMode;
    }

    @Override
    public void addCreativeStack(StackContext stack, Iterable<CreativeTabs> tabs)
    {
        for (CreativeTabs tab : tabs)
        {
            perTabStacks.put(tab, stack);
        }
        searchTabStacks.add(stack);
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
    public void addAttributemodifier(@Nullable EntityEquipmentSlot slot, String attributeName, AttributeModifier modifier)
    {
        if (slot != null)
        {
            attributeModifiers.get(slot).put(attributeName, modifier);
        }
        else
        {
            for(EntityEquipmentSlot slot1 : EntityEquipmentSlot.values())
                attributeModifiers.get(slot1).put(attributeName, modifier);
        }
    }
    //endregion

    //region Item
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack heldItem = player.getHeldItem(hand);
        if (useTime > 0)
            return EnumActionResult.PASS;

        return runEvent("use_on_block", player, hand, heldItem, worldIn, pos, facing, () -> new ActionResult<>(EnumActionResult.PASS, heldItem)).getType();
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
        runEvent("stopped_using", entityLiving, null, stack, worldIn, null, null, () -> new ActionResult<>(EnumActionResult.PASS, stack));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack heldItem, World worldIn, EntityLivingBase entityLiving)
    {
        ActionResult<ItemStack> result = runEvent("end_using", entityLiving, null, heldItem, () -> new ActionResult<>(EnumActionResult.SUCCESS, heldItem));
        if (result.getType() != EnumActionResult.SUCCESS)
            return result.getResult();

        return runEvent("use", entityLiving, null, heldItem, () -> new ActionResult<>(EnumActionResult.SUCCESS, heldItem)).getResult();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (useTime > 0)
            return runEvent("begin_using", playerIn, handIn, heldItem, () -> new ActionResult<>(EnumActionResult.SUCCESS, heldItem));
        else
            return runEvent("use_on_air", playerIn, handIn, heldItem, () -> new ActionResult<>(EnumActionResult.PASS, heldItem));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        for (ITextComponent t : tooltipStrings)
        { tooltip.add(t.getFormattedText()); }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab == CreativeTabs.SEARCH)
        {
            items.addAll(searchTabStacks.stream().map(s -> s.toStack(this)).collect(Collectors.toList()));
        }
        else if (perTabStacks.containsKey(tab))
        {
            items.addAll(perTabStacks.get(tab).stream().map(s -> s.toStack(this)).collect(Collectors.toList()));
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        ActionResult<ItemStack> result = runEvent("update", (EntityLivingBase) entityIn, null, stack, () -> new ActionResult<>(EnumActionResult.PASS, stack));
        if (!ItemStack.areItemStacksEqual(result.getResult(), stack))
        {
            entityIn.replaceItemInInventory(itemSlot, result.getResult());
        }
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return runEvent("get_container_item", null, null, itemStack, () -> new ActionResult<>(EnumActionResult.PASS, itemStack)).getResult();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        return attributeModifiers.get(slot);
    }

    //endregion
}
