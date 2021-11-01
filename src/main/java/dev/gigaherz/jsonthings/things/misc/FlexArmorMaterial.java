package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class FlexArmorMaterial implements IArmorMaterial
{
    private final String name;
    private final Map<EquipmentSlotType, Integer> durability;
    private final Map<EquipmentSlotType, Integer> defense;
    private final float toughness;
    private final float knockbackResistance;
    private final int enchantmentValue;
    private final Supplier<SoundEvent> equipSound;
    private final Supplier<Ingredient> repairIngredient;

    public FlexArmorMaterial(String name, Map<EquipmentSlotType, Integer> durability, Map<EquipmentSlotType, Integer> defense, float toughness, float knockbackResistance, int enchantmentValue, Supplier<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient)
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
    public int getDurabilityForSlot(EquipmentSlotType pSlot)
    {
        return durability.getOrDefault(pSlot, 0);
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType pSlot)
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
