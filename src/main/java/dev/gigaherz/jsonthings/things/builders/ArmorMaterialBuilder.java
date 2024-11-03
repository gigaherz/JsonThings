package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;

public class ArmorMaterialBuilder extends BaseBuilder<ArmorMaterial, ArmorMaterialBuilder>
{
    public static ArmorMaterialBuilder begin(ThingParser<ArmorMaterial, ArmorMaterialBuilder> ownerParser, ResourceLocation registryName)
    {
        return new ArmorMaterialBuilder(ownerParser, registryName);
    }

    private Integer durability;
    private final Map<ArmorType, Integer> defense = new HashMap<>();
    private float toughness;
    private float knockbackResistance;
    private int enchantmentValue;
    private ResourceLocation equipSound;
    private TagKey<Item> repairIngredient;

    private ArmorMaterialBuilder(ThingParser<ArmorMaterial, ArmorMaterialBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Armor Material";
    }

    public void setDurability(int durability)
    {
        this.durability = durability;
    }

    public void setDefense(Map<ArmorType, Integer> defense)
    {
        this.defense.putAll(defense);
    }

    public void setToughness(float toughness)
    {
        this.toughness = toughness;
    }

    public void setKnockbackResistance(float knockbackResistance)
    {
        this.knockbackResistance = knockbackResistance;
    }

    public void setEnchantmentValue(int enchantmentValue)
    {
        this.enchantmentValue = enchantmentValue;
    }

    public void setEquipSound(ResourceLocation equipSound)
    {
        this.equipSound = equipSound;
    }

    public void setRepairIngredient(TagKey<Item> repairIngredient)
    {
        this.repairIngredient = repairIngredient;
    }

    @Override
    protected ArmorMaterial buildInternal()
    {
        var se = DeferredHolder.create(Registries.SOUND_EVENT, equipSound);
        return new ArmorMaterial(durability, defense, enchantmentValue, se, toughness, knockbackResistance, repairIngredient, getRegistryName());
    }
}
