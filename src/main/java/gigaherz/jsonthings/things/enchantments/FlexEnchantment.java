package gigaherz.jsonthings.things.enchantments;

import com.google.common.collect.Maps;
import gigaherz.jsonthings.things.events.EnchantmentEventHandler;
import gigaherz.jsonthings.things.events.FlexEventContext;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FlexEnchantment extends Enchantment
{
    private final Map<String, EnchantmentEventHandler> eventHandlers = Maps.newHashMap();
    private int minLevel;
    private int maxLevel;
    private Integer minCost;
    private Integer maxCost;
    private List<Predicate<Enchantment>> blackList = List.of();
    private ItemPredicate itemCompatibility;
    private boolean isTreasure;
    private boolean isCurse;
    private boolean isTradeable = true;
    private boolean isDiscoverable;
    private boolean isAllowedOnBooks;

    public FlexEnchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] slots)
    {
        super(rarity, enchantmentCategory, slots);
    }

    public void addEventHandler(String eventName, EnchantmentEventHandler eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    public EnchantmentEventHandler getEventHandler(String eventName)
    {
        return eventHandlers.get(eventName);
    }

    protected InteractionResult runEvent(String eventName, FlexEventContext context, Supplier<InteractionResult> defaultValue)
    {
        EnchantmentEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.get();
    }

    protected InteractionResult runEventThrowing(String eventName, FlexEventContext context, Callable<InteractionResult> defaultValue) throws Exception
    {
        EnchantmentEventHandler handler = getEventHandler(eventName);
        if (handler != null)
            return handler.apply(eventName, context);
        return defaultValue.call();
    }

    public void setMinLevel(int minLevel)
    {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel)
    {
        this.maxLevel = maxLevel;
    }

    public void setMinCost(Integer minCost)
    {
        this.minCost = minCost;
    }

    public void setMaxCost(Integer maxCost)
    {
        this.maxCost = maxCost;
    }

    public void setTreasure(boolean treasure)
    {
        this.isTreasure = treasure;
    }

    public void setCurse(boolean curse)
    {
        this.isCurse = curse;
    }

    public void setTradeable(boolean tradeable)
    {
        this.isTradeable = tradeable;
    }

    public void setDiscoverable(boolean discoverable)
    {
        this.isDiscoverable = discoverable;
    }

    public void setAllowedOnBooks(boolean allowedOnBooks)
    {
        this.isAllowedOnBooks = allowedOnBooks;
    }

    public void setBlackList(List<Predicate<Enchantment>> blackList)
    {
        this.blackList = blackList;
    }

    public void setItemCompatibility(ItemPredicate itemCompatibility)
    {
        this.itemCompatibility = itemCompatibility;
    }

    @Override
    public int getMinLevel()
    {
        return minLevel;
    }

    @Override
    public int getMaxLevel()
    {
        return maxLevel;
    }

    @Override
    public int getMinCost(int enchantmentLevel)
    {
        if (minCost != null) return minCost;
        return super.getMinCost(enchantmentLevel);
    }

    @Override
    public int getMaxCost(int enchantmentLevel)
    {
        if (maxCost != null) return maxCost;
        return super.getMaxCost(enchantmentLevel);
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench)
    {
        if (blackList.stream().anyMatch(pred -> pred.test(ench))) return false;
        return super.checkCompatibility(ench);
    }

    @Override
    public boolean isTreasureOnly()
    {
        return isTreasure;
    }

    @Override
    public boolean isCurse()
    {
        return isCurse;
    }

    @Override
    public boolean isTradeable()
    {
        return isTradeable;
    }

    @Override
    public boolean isDiscoverable()
    {
        return isDiscoverable;
    }

    @Override
    public boolean isAllowedOnBooks()
    {
        return isAllowedOnBooks;
    }

    @Override
    public boolean canEnchant(ItemStack stack)
    {
        if (itemCompatibility != null && !itemCompatibility.matches(stack))
            return false;
        return super.canEnchant(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level)
    {
        runEvent("post_attack", FlexEventContext.of(this, level).with(FlexEventContext.ATTACKER, user).with(FlexEventContext.TARGET, target), () -> {
            super.doPostAttack(user, target, level);
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public void doPostHurt(LivingEntity user, Entity attacker, int level)
    {
        runEvent("post_hurt", FlexEventContext.of(this, level).with(FlexEventContext.ATTACKER, attacker).with(FlexEventContext.TARGET, user), () -> {
            super.doPostHurt(user, attacker, level);
            return InteractionResult.SUCCESS;
        });
    }
}
