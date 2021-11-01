package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.FlexTier;
import net.minecraft.block.Block;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Supplier;

public class TierBuilder implements Supplier<IItemTier>
{
    private IItemTier builtTier = null;

    private final ResourceLocation registryName;

    private int level = 0;
    private int uses;
    private float speed;
    private float attackDamageBonus;
    private int enchantmentValue;
    private Supplier<Ingredient> repairIngredient;
    private List<Object> sortAfter;
    private List<Object> sortBefore;

    private TierBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static TierBuilder begin(ResourceLocation registryName)
    {
        return new TierBuilder(registryName);
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public void setUses(int uses)
    {
        this.uses = uses;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public void setAttackDamageBonus(float attackDamageBonus)
    {
        this.attackDamageBonus = attackDamageBonus;
    }

    public void setEnchantmentValue(int enchantmentValue)
    {
        this.enchantmentValue = enchantmentValue;
    }

    public void setRepairIngredient(Supplier<Ingredient> repairIngredient)
    {
        this.repairIngredient = repairIngredient;
    }

    public void setAfterDependencies(List<Object> sortAfter)
    {
        this.sortAfter = sortAfter;
    }

    public void setBeforeDependencies(List<Object> sortBefore)
    {
        this.sortBefore = sortBefore;
    }

    private IItemTier build()
    {
        return builtTier = new FlexTier(level, uses, speed, attackDamageBonus, enchantmentValue, repairIngredient);
    }

    public IItemTier get()
    {
        if (builtTier == null)
            return build();
        return builtTier;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public List<Object> getSortAfter()
    {
        return sortAfter;
    }

    public List<Object> getSortBefore()
    {
        return sortBefore;
    }
}
