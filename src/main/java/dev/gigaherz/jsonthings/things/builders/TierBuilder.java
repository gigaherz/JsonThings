package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.function.Supplier;

public class TierBuilder extends BaseBuilder<SimpleTier, TierBuilder>
{
    public static TierBuilder begin(ThingParser<TierBuilder> ownerParser, ResourceLocation registryName)
    {
        return new TierBuilder(ownerParser, registryName);
    }

    private int uses;
    private float speed;
    private float attackDamageBonus;
    private int enchantmentValue;
    private TagKey<Block> tag;
    private Supplier<Ingredient> repairIngredient;

    private TierBuilder(ThingParser<TierBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Item Tier";
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

    @Override
    protected SimpleTier buildInternal()
    {
        return new SimpleTier(tag, uses, speed, attackDamageBonus, enchantmentValue, repairIngredient);
    }

}
