package dev.gigaherz.jsonthings.things.misc;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class FlexEnchantment extends Enchantment implements IEventRunner
{
    private final Map<String, FlexEventHandler> eventHandlers = Maps.newHashMap();
    private int minLevel;
    private int maxLevel;
    private int baseCost;
    private int perLevelCost;
    private int randomCost;
    private List<Predicate<Enchantment>> blackList = List.of();
    private ItemPredicate itemCompatibility;
    private boolean isTreasure;
    private boolean isCurse;
    private boolean isTradeable = true;
    private boolean isDiscoverable = true;
    private boolean isAllowedOnBooks = true;

    public FlexEnchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] slots)
    {
        super(rarity, enchantmentCategory, slots);
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
            return FlexEventResult.success();
        });
    }

    @Override
    public void doPostHurt(LivingEntity user, Entity attacker, int level)
    {
        runEvent("post_hurt", FlexEventContext.of(this, level).with(FlexEventContext.ATTACKER, attacker).with(FlexEventContext.TARGET, user), () -> {
            super.doPostHurt(user, attacker, level);
            return FlexEventResult.success();
        });
    }
}
