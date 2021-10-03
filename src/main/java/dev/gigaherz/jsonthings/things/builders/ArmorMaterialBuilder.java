package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.misc.FlexArmorMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorMaterialBuilder implements Supplier<FlexArmorMaterial>
{
    private FlexArmorMaterial builtMaterial = null;

    private final ResourceLocation registryName;

    private final Map<EquipmentSlot, Integer> durability = new HashMap<>();
    private final Map<EquipmentSlot, Integer> defense = new HashMap<>();
    private float toughness;
    private float knockbackResistance;
    private int enchantmentValue;
    private ResourceLocation equipSound;
    private Supplier<Ingredient> repairIngredient;

    private ArmorMaterialBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static ArmorMaterialBuilder begin(ResourceLocation registryName)
    {
        return new ArmorMaterialBuilder(registryName);
    }

    public void withDurability(Map<EquipmentSlot, Integer> durability)
    {
        this.durability.putAll(durability);
    }

    public void withDefense(Map<EquipmentSlot, Integer> defense)
    {
        this.defense.putAll(defense);
    }

    public void setToughness(float toughness)
    {
        this.toughness = toughness;
    }

    public void withKnockbackResistance(float knockbackResistance)
    {
        this.knockbackResistance = knockbackResistance;
    }

    public void withEnchantmentValue(int enchantmentValue)
    {
        this.enchantmentValue = enchantmentValue;
    }

    public void withEquipSound(ResourceLocation equipSound)
    {
        this.equipSound = equipSound;
    }

    public void withRepairIngredient(Supplier<Ingredient> repairIngredient)
    {
        this.repairIngredient = repairIngredient;
    }

    private FlexArmorMaterial build()
    {
        var se = RegistryObject.of(equipSound, ForgeRegistries.SOUND_EVENTS);
        return builtMaterial = new FlexArmorMaterial(registryName.toString(), durability, defense, toughness, knockbackResistance, enchantmentValue, se, repairIngredient);
    }

    public FlexArmorMaterial get()
    {
        if (builtMaterial == null)
            return build();
        return builtMaterial;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }
}
