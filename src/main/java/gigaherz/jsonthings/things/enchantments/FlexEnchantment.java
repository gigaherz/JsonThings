package gigaherz.jsonthings.things.enchantments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Predicate;

public class FlexEnchantment extends Enchantment
{
    private int minLevel;
    private int maxLevel;
    private Integer minCost;
    private Integer maxCost;
    private Predicate<Enchantment> whiteList = e -> false;
    private Predicate<Enchantment> blackList = e -> false;

    public FlexEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots)
    {
        super(rarityIn, typeIn, slots);
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

    public void setWhiteList(Predicate<Enchantment> whiteList)
    {
        this.whiteList = whiteList;
    }

    public void setBlackList(Predicate<Enchantment> blackList)
    {
        this.blackList = blackList;
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
        if (whiteList.test(ench)) return true;
        if (blackList.test(ench)) return false;
        return super.checkCompatibility(ench);
    }

    @Override
    protected String getOrCreateDescriptionId()
    {
        return super.getOrCreateDescriptionId();
    }

    @Override
    public String getDescriptionId()
    {
        return super.getDescriptionId();
    }

    @Override
    public Component getFullname(int level)
    {
        return super.getFullname(level);
    }

    @Override
    public boolean canEnchant(ItemStack stack)
    {
        return super.canEnchant(stack);
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level)
    {
        super.doPostAttack(user, target, level);
    }

    @Override
    public void doPostHurt(LivingEntity user, Entity attacker, int level)
    {
        super.doPostHurt(user, attacker, level);
    }

    @Override
    public boolean isTreasureOnly()
    {
        return super.isTreasureOnly();
    }

    @Override
    public boolean isCurse()
    {
        return super.isCurse();
    }

    @Override
    public boolean isTradeable()
    {
        return super.isTradeable();
    }

    @Override
    public boolean isDiscoverable()
    {
        return super.isDiscoverable();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean isAllowedOnBooks()
    {
        return super.isAllowedOnBooks();
    }
}
