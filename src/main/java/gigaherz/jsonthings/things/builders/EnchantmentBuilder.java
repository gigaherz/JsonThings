package gigaherz.jsonthings.things.builders;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import gigaherz.jsonthings.things.IFlexBlock;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.enchantments.FlexEnchantment;
import gigaherz.jsonthings.things.parsers.ThingResourceManager;
import gigaherz.jsonthings.things.serializers.BlockType;
import gigaherz.jsonthings.things.serializers.MaterialColors;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EnchantmentBuilder
{
    private final Multimap<String, String> eventHandlers = ArrayListMultimap.create();

    private FlexEnchantment builtEnchantment = null;

    private ResourceLocation registryName;

    private Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;
    private EnchantmentType type = EnchantmentType.BREAKABLE;
    private EquipmentSlotType[] slots = EquipmentSlotType.values();
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

    public EnchantmentBuilder withEnchantmentType(EnchantmentType type)
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
