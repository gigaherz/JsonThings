package gigaherz.jsonthings.things.builders;

import com.google.common.collect.Lists;
import gigaherz.jsonthings.things.enchantments.FlexEnchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;
import java.util.function.Predicate;

public class EnchantmentBuilder
{
    private FlexEnchantment builtEnchantment = null;

    private ResourceLocation registryName;

    private Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;
    private EnchantmentCategory type = EnchantmentCategory.BREAKABLE;
    private EquipmentSlot[] slots = EquipmentSlot.values();
    private int minLevel = 1;
    private int maxLevel = 1;
    private Integer minCost;
    private Integer maxCost;
    private List<Predicate<Enchantment>> whiteList = Lists.newArrayList();
    private List<Predicate<Enchantment>> blackList = Lists.newArrayList();

    private EnchantmentBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static EnchantmentBuilder begin(ResourceLocation registryName)
    {
        return new EnchantmentBuilder(registryName);
    }

    public EnchantmentBuilder withRarity(Enchantment.Rarity rarity)
    {
        this.rarity = rarity;
        return this;
    }

    public EnchantmentBuilder withEnchantmentType(EnchantmentCategory type)
    {
        this.type = type;
        return this;
    }

    public EnchantmentBuilder withMinLevel(int minLevel)
    {
        this.minLevel = minLevel;
        return this;
    }

    public EnchantmentBuilder withMaxLevel(int macLevel)
    {
        this.maxLevel = macLevel;
        return this;
    }

    public EnchantmentBuilder withMinCost(int minCost)
    {
        this.minCost = minCost;
        return this;
    }

    public EnchantmentBuilder withMaxCost(int macCost)
    {
        this.maxCost = macCost;
        return this;
    }

    public FlexEnchantment build()
    {
        FlexEnchantment flexEnchantment = new FlexEnchantment(rarity, type, slots);

        flexEnchantment.setMinLevel(minLevel);
        flexEnchantment.setMaxLevel(maxLevel);
        flexEnchantment.setMinCost(minCost);
        flexEnchantment.setMaxCost(maxCost);

        builtEnchantment = flexEnchantment;
        return flexEnchantment;
    }

    public FlexEnchantment getBuiltEnchantment()
    {
        if (builtEnchantment == null)
            return build();
        return builtEnchantment;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }
}
