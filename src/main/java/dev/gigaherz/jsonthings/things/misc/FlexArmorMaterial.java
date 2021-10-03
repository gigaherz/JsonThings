package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class FlexArmorMaterial implements ArmorMaterial
{
    private final String name;
    private final Map<EquipmentSlot, Integer> durability;
    private final Map<EquipmentSlot, Integer> defense;
    private final float toughness;
    private final float knockbackResistance;
    private final int enchantmentValue;
    private final Supplier<SoundEvent> equipSound;
    private final Supplier<Ingredient> repairIngredient;

    public FlexArmorMaterial(String name, Map<EquipmentSlot, Integer> durability, Map<EquipmentSlot, Integer> defense, float toughness, float knockbackResistance, int enchantmentValue, Supplier<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient)
    {
        this.name = name;
        this.durability = durability;
        this.defense = defense;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot pSlot)
    {
        return durability.getOrDefault(pSlot, 0);
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot pSlot)
    {
        return defense.getOrDefault(pSlot, 0);
    }

    @Override
    public int getEnchantmentValue()
    {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound()
    {
        return equipSound.get();
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return repairIngredient.get();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public float getToughness()
    {
        return toughness;
    }

    @Override
    public float getKnockbackResistance()
    {
        return knockbackResistance;
    }
}
