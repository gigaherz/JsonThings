package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.misc.FlexArmorMaterial;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorMaterialBuilder extends BaseBuilder<FlexArmorMaterial, ArmorMaterialBuilder>
{
    public static ArmorMaterialBuilder begin(ThingParser<ArmorMaterialBuilder> ownerParser, ResourceLocation registryName)
    {
        return new ArmorMaterialBuilder(ownerParser, registryName);
    }

    private final Map<EquipmentSlot, Integer> durability = new HashMap<>();
    private final Map<EquipmentSlot, Integer> defense = new HashMap<>();
    private float toughness;
    private float knockbackResistance;
    private int enchantmentValue;
    private ResourceLocation equipSound;
    private Supplier<Ingredient> repairIngredient;

    private ArmorMaterialBuilder(ThingParser<ArmorMaterialBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Armor Material";
    }

    public void setDurability(Map<EquipmentSlot, Integer> durability)
    {
        this.durability.putAll(durability);
    }

    public void setDefense(Map<EquipmentSlot, Integer> defense)
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

    public void setRepairIngredient(Supplier<Ingredient> repairIngredient)
    {
        this.repairIngredient = repairIngredient;
    }

    @Override
    protected FlexArmorMaterial buildInternal()
    {
        var se = RegistryObject.create(equipSound, ForgeRegistries.SOUND_EVENTS);
        return new FlexArmorMaterial(getRegistryName().toString(), durability, defense, toughness, knockbackResistance, enchantmentValue, se, repairIngredient);
    }
}
