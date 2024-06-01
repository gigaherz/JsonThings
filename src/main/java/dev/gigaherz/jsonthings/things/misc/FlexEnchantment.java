package dev.gigaherz.jsonthings.things.misc;

import com.google.common.collect.Maps;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class FlexEnchantment extends Enchantment implements IEventRunner
{
    @SuppressWarnings("rawtypes")
    private final Map<FlexEventType, FlexEventHandler> eventHandlers = Maps.newHashMap();
    private List<Predicate<Enchantment>> blackList = List.of();
    private boolean isTreasure;
    private boolean isCurse;
    private boolean isTradeable = true;
    private boolean isDiscoverable = true;
    private boolean isAllowedOnBooks = true;

    public FlexEnchantment(EnchantmentDefinition definition)
    {
        super(definition);
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
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level)
    {
        runEvent(FlexEventType.POST_ATTACK, FlexEventContext.of(this, level)
                .with(FlexEventContext.ATTACKER, user)
                .with(FlexEventContext.TARGET, target), () -> super.doPostAttack(user, target, level));
    }

    @Override
    public void doPostHurt(LivingEntity user, Entity attacker, int level)
    {
        runEvent(FlexEventType.POST_HURT, FlexEventContext.of(this, level)
                .with(FlexEventContext.ATTACKER, attacker)
                .with(FlexEventContext.TARGET, user), () -> super.doPostHurt(user, attacker, level));
    }
}
