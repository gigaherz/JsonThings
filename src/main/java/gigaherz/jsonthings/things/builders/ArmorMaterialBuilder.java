package gigaherz.jsonthings.things.builders;

import gigaherz.jsonthings.things.misc.FlexArmorMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorMaterialBuilder
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

    public ArmorMaterialBuilder withDurability(Map<EquipmentSlot, Integer> durability)
    {
        this.durability.putAll(durability);
        return this;
    }

    public ArmorMaterialBuilder withDefense(Map<EquipmentSlot, Integer> defense)
    {
        this.defense.putAll(defense);
        return this;
    }

    public ArmorMaterialBuilder setToughness(float toughness)
    {
        this.toughness = toughness;
        return this;
    }

    public ArmorMaterialBuilder withKnockbackResistance(float knockbackResistance)
    {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    public ArmorMaterialBuilder withEnchantmentValue(int enchantmentValue)
    {
        this.enchantmentValue = enchantmentValue;
        return this;
    }

    public ArmorMaterialBuilder withEquipSound(ResourceLocation equipSound)
    {
        this.equipSound = equipSound;
        return this;
    }

    public ArmorMaterialBuilder withRepairIngredient(Supplier<Ingredient> repairIngredient)
    {
        this.repairIngredient = repairIngredient;
        return this;
    }

    public FlexArmorMaterial build()
    {
        var se = RegistryObject.of(equipSound, ForgeRegistries.SOUND_EVENTS);
        return builtMaterial = new FlexArmorMaterial(registryName.toString(), durability, defense, toughness, knockbackResistance, enchantmentValue, se, repairIngredient);
    }

    public FlexArmorMaterial getBuiltMaterial()
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
