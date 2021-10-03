package dev.gigaherz.jsonthings.things.builders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeTier;

import java.util.List;
import java.util.function.Supplier;

public class TierBuilder implements Supplier<ForgeTier>
{
    private ForgeTier builtTier = null;

    private final ResourceLocation registryName;

    private int level = 0;
    private int uses;
    private float speed;
    private float attackDamageBonus;
    private int enchantmentValue;
    private Tag<Block> tag;
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

    public void setTag(Tag<Block> tag)
    {
        this.tag = tag;
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

    private ForgeTier build()
    {
        return builtTier = new ForgeTier(level, uses, speed, attackDamageBonus, enchantmentValue, tag, repairIngredient);
    }

    public ForgeTier get()
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
