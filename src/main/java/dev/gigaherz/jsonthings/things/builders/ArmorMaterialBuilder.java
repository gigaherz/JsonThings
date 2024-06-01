package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorMaterialBuilder extends BaseBuilder<ArmorMaterial, ArmorMaterialBuilder>
{
    public static ArmorMaterialBuilder begin(ThingParser<ArmorMaterialBuilder> ownerParser, ResourceLocation registryName)
    {
        return new ArmorMaterialBuilder(ownerParser, registryName);
    }

    private final Map<ArmorItem.Type, Integer> durability = new HashMap<>();
    private final Map<ArmorItem.Type, Integer> defense = new HashMap<>();
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

    public void setDurability(Map<ArmorItem.Type, Integer> durability)
    {
        this.durability.putAll(durability);
    }

    public void setDefense(Map<ArmorItem.Type, Integer> defense)
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
    protected ArmorMaterial buildInternal()
    {
        var se = DeferredHolder.create(Registries.SOUND_EVENT, equipSound);
        var defaultLayer = new ArmorMaterial.Layer(getRegistryName()); // FIXME: custom layers
        return new ArmorMaterial(defense, enchantmentValue, se, repairIngredient, List.of(defaultLayer), toughness, knockbackResistance);
    }
}
