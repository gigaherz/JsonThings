package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeTier;

import java.util.List;
import java.util.function.Supplier;

public class TierBuilder extends BaseBuilder<ForgeTier, TierBuilder>
{
    public static TierBuilder begin(ThingParser<TierBuilder> ownerParser, ResourceLocation registryName)
    {
        return new TierBuilder(ownerParser, registryName);
    }

    private int level = 0;
    private int uses;
    private float speed;
    private float attackDamageBonus;
    private int enchantmentValue;
    private TagKey<Block> tag;
    private Supplier<Ingredient> repairIngredient;
    private List<Object> sortAfter;
    private List<Object> sortBefore;

    private TierBuilder(ThingParser<TierBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Item Tier";
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

    public void setTag(TagKey<Block> tag)
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

    @Override
    protected ForgeTier buildInternal()
    {
        return new ForgeTier(level, uses, speed, attackDamageBonus, enchantmentValue, tag, repairIngredient);
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
