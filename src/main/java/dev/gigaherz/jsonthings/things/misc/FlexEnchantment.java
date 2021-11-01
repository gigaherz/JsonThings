package dev.gigaherz.jsonthings.things.misc;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class FlexEnchantment extends Enchantment implements IEventRunner<ActionResultType>
{
    private final Map<String, FlexEventHandler<ActionResultType>> eventHandlers = Maps.newHashMap();
    private int minLevel;
    private int maxLevel;
    private int baseCost;
    private int perLevelCost;
    private int randomCost;
    private List<Predicate<Enchantment>> blackList = Collections.emptyList();
    private ItemPredicate itemCompatibility;
    private boolean isTreasure;
    private boolean isCurse;
    private boolean isTradeable = true;
    private boolean isDiscoverable = true;
    private boolean isAllowedOnBooks = true;

    public FlexEnchantment(Rarity rarity, EnchantmentType enchantmentCategory, EquipmentSlotType[] slots)
    {
        super(rarity, enchantmentCategory, slots);
    }

    @Override
    public void addEventHandler(String eventName, FlexEventHandler<ActionResultType> eventHandler)
    {
        eventHandlers.put(eventName, eventHandler);
    }

    @Override
    public FlexEventHandler<ActionResultType> getEventHandler(String eventName)
    {
        return eventHandlers.get(eventName);
    }

    public void setMinLevel(int minLevel)
    {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel)
    {
        this.maxLevel = maxLevel;
    }

    public void setBaseCost(int baseCost)
    {
        this.baseCost = baseCost;
    }

    public void setPerLevelCost(int perLevelCost)
    {
        this.perLevelCost = perLevelCost;
    }

    public void setRandomCost(int randomCost)
    {
        this.randomCost = randomCost;
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
        return baseCost * enchantmentLevel * perLevelCost;
    }

    @Override
    public int getMaxCost(int enchantmentLevel)
    {
        return getMinCost(enchantmentLevel) + randomCost;
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
            return ActionResultType.SUCCESS;
        });
    }

    @Override
    public void doPostHurt(LivingEntity user, Entity attacker, int level)
    {
        runEvent("post_hurt", FlexEventContext.of(this, level).with(FlexEventContext.ATTACKER, attacker).with(FlexEventContext.TARGET, user), () -> {
            super.doPostHurt(user, attacker, level);
            return ActionResultType.SUCCESS;
        });
    }
}
