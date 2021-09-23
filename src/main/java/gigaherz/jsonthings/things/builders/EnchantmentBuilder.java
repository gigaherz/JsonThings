package gigaherz.jsonthings.things.builders;

import com.google.common.collect.Lists;
import gigaherz.jsonthings.things.misc.FlexEnchantment;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Predicate;

public class EnchantmentBuilder
{
    private FlexEnchantment builtEnchantment = null;

    private final ResourceLocation registryName;

    private Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;
    private EnchantmentCategory type = EnchantmentCategory.BREAKABLE;
    private EquipmentSlot[] slots = EquipmentSlot.values();
    private int minLevel = 1;
    private int maxLevel = 1;
    private int baseCost = 1;
    private int perLevelCost = 10;
    private int randomCost = 5;
    private List<ResourceLocation> blackList = Lists.newArrayList();
    private ItemPredicate itemCompatibility;
    private boolean isTreasure = false;
    private boolean isCurse = false;
    private boolean isTradeable = true;
    private boolean isDiscoverable = true;
    private boolean isAllowedOnBooks = true;

    private EnchantmentBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static EnchantmentBuilder begin(ResourceLocation registryName)
    {
        return new EnchantmentBuilder(registryName);
    }

    public EnchantmentBuilder setRarity(Enchantment.Rarity rarity)
    {
        this.rarity = rarity;
        return this;
    }

    public EnchantmentBuilder setEnchantmentType(EnchantmentCategory type)
    {
        this.type = type;
        return this;
    }

    public EnchantmentBuilder setMinLevel(int minLevel)
    {
        this.minLevel = minLevel;
        return this;
    }

    public EnchantmentBuilder setMaxLevel(int macLevel)
    {
        this.maxLevel = macLevel;
        return this;
    }

    public EnchantmentBuilder setBaseCost(int baseCost)
    {
        this.baseCost = baseCost;
        return this;
    }

    public EnchantmentBuilder setPerLevelCost(int perLevelCost)
    {
        this.perLevelCost = perLevelCost;
        return this;
    }

    public EnchantmentBuilder setRandomCost(int randomCost)
    {
        this.randomCost = randomCost;
        return this;
    }

    public EnchantmentBuilder setIsTreasure(boolean treasure)
    {
        this.isTreasure = treasure;
        return this;
    }

    public EnchantmentBuilder setIsCurse(boolean curse)
    {
        this.isCurse = curse;
        return this;
    }

    public EnchantmentBuilder setIsTradeable(boolean tradeable)
    {
        this.isTradeable = tradeable;
        return this;
    }

    public EnchantmentBuilder setIsDiscoverable(boolean discoverable)
    {
        this.isDiscoverable = discoverable;
        return this;
    }

    public EnchantmentBuilder setItemCompatibility(ItemPredicate item_compatibility)
    {
        this.itemCompatibility = item_compatibility;
        return this;
    }

    public EnchantmentBuilder setBlacklist(List<ResourceLocation> blacklist)
    {
        this.blackList = blacklist;
        return this;
    }

    public FlexEnchantment build()
    {
        FlexEnchantment flexEnchantment = new FlexEnchantment(rarity, type, slots);

        flexEnchantment.setMinLevel(minLevel);
        flexEnchantment.setMaxLevel(maxLevel);
        flexEnchantment.setBaseCost(baseCost);
        flexEnchantment.setPerLevelCost(perLevelCost);
        flexEnchantment.setRandomCost(randomCost);
        flexEnchantment.setItemCompatibility(itemCompatibility);
        flexEnchantment.setTreasure(isTreasure);
        flexEnchantment.setCurse(isCurse);
        flexEnchantment.setTradeable(isTradeable);
        flexEnchantment.setDiscoverable(isDiscoverable);
        flexEnchantment.setAllowedOnBooks(isAllowedOnBooks);
        flexEnchantment.setBlackList(blackList.stream().map(loc -> {
            var ro = RegistryObject.of(loc, ForgeRegistries.ENCHANTMENTS);
            return (Predicate<Enchantment>) ((enchantment) -> ro.filter(en -> en == enchantment).isPresent());
        }).toList());

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

    public EnchantmentBuilder setIsAllowedOnBooks(boolean allow_on_books)
    {
        this.isAllowedOnBooks = allow_on_books;
        return this;
    }
}
