package gigaherz.jsonthings.things.builders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeTier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class TierBuilder
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

    public TierBuilder withLevel(int level)
    {
        this.level = level;
        return this;
    }

    public TierBuilder withUses(int uses)
    {
        this.uses = uses;
        return this;
    }

    public TierBuilder withSpeed(float speed)
    {
        this.speed = speed;
        return this;
    }

    public TierBuilder withAttackDamageBonus(float attackDamageBonus)
    {
        this.attackDamageBonus = attackDamageBonus;
        return this;
    }

    public TierBuilder withEnchantmentValue(int enchantmentValue)
    {
        this.enchantmentValue = enchantmentValue;
        return this;
    }

    public TierBuilder withTag(Tag<Block> tag)
    {
        this.tag = tag;
        return this;
    }

    public TierBuilder withRepairIngredient(Supplier<Ingredient> repairIngredient)
    {
        this.repairIngredient = repairIngredient;
        return this;
    }

    public TierBuilder withAfterDependencies(List<Object> sortAfter)
    {
        this.sortAfter = sortAfter;
        return this;
    }

    public TierBuilder withBeforeDependencies(List<Object> sortBefore)
    {
        this.sortBefore = sortBefore;
        return this;
    }

    public ForgeTier build()
    {
        return builtTier = new ForgeTier(level, uses, speed, attackDamageBonus, enchantmentValue, tag, repairIngredient);
    }

    public ForgeTier getBuiltTier()
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