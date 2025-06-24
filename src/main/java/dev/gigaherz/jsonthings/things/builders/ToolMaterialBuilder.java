package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public class ToolMaterialBuilder extends BaseBuilder<ToolMaterial, ToolMaterialBuilder>
{
    public static ToolMaterialBuilder begin(ThingParser<ToolMaterial, ToolMaterialBuilder> ownerParser, ResourceLocation registryName)
    {
        return new ToolMaterialBuilder(ownerParser, registryName);
    }

    private int uses;
    private float speed;
    private float attackDamageBonus;
    private int enchantmentValue;
    private TagKey<Block> tag;
    private TagKey<Item> repairIngredient;

    private ToolMaterialBuilder(ThingParser<ToolMaterial, ToolMaterialBuilder> ownerParser, ResourceLocation registryName)
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

    public void setRepairIngredient(TagKey<Item> repairIngredient)
    {
        this.repairIngredient = repairIngredient;
    }

    @Override
    protected ToolMaterial buildInternal()
    {
        return new ToolMaterial(tag, uses, speed, attackDamageBonus, enchantmentValue, repairIngredient);
    }

    @Override
    public void validate()
    {
    }
}
