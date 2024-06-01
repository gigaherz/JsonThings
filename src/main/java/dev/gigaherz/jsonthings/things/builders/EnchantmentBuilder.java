package dev.gigaherz.jsonthings.things.builders;

import com.google.common.collect.Lists;
import dev.gigaherz.jsonthings.things.misc.FlexEnchantment;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EnchantmentBuilder extends BaseBuilder<FlexEnchantment, EnchantmentBuilder>
{
    public static EnchantmentBuilder begin(ThingParser<EnchantmentBuilder> ownerParser, ResourceLocation registryName)
    {
        return new EnchantmentBuilder(ownerParser, registryName);
    }

    private String itemCompatibilityTag;
    private String primaryItemsTag;
    private EquipmentSlot[] slots = EquipmentSlot.values();
    private int weight;
    private int maxLevel = 1;
    private Enchantment.Cost minCost;
    private Enchantment.Cost maxCost;
    private int anvilCost = 1;
    private List<ResourceLocation> blackList = Lists.newArrayList();
    private boolean isTreasure = false;
    private boolean isCurse = false;
    private boolean isTradeable = true;
    private boolean isDiscoverable = true;
    private boolean isAllowedOnBooks = true;

    private EnchantmentBuilder(ThingParser<EnchantmentBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Enchantment";
    }

    public void setItemCompatibilityTag(String itemCompatibilityTag)
    {
        this.itemCompatibilityTag = itemCompatibilityTag;
    }

    public void setPrimaryItemsTag(String primaryItemsTag)
    {
        this.primaryItemsTag = primaryItemsTag;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public void setMaxLevel(int macLevel)
    {
        this.maxLevel = macLevel;
    }

    public void setMinCost(Enchantment.Cost minCost)
    {
        this.minCost = minCost;
    }

    public void setMaxCost(Enchantment.Cost maxCost)
    {
        this.maxCost = maxCost;
    }

    public void setAnvilCost(int anvilCost)
    {
        this.anvilCost = anvilCost;
    }

    public void setIsTreasure(boolean treasure)
    {
        this.isTreasure = treasure;
    }

    public void setIsCurse(boolean curse)
    {
        this.isCurse = curse;
    }

    public void setIsTradeable(boolean tradeable)
    {
        this.isTradeable = tradeable;
    }

    public void setIsDiscoverable(boolean discoverable)
    {
        this.isDiscoverable = discoverable;
    }

    public void setBlacklist(List<ResourceLocation> blacklist)
    {
        this.blackList = blacklist;
    }

    public void setIsAllowedOnBooks(boolean allow_on_books)
    {
        this.isAllowedOnBooks = allow_on_books;
    }

    public void setSlots(EquipmentSlot[] slots)
    {
        this.slots = slots;
    }

    @Override
    protected FlexEnchantment buildInternal()
    {
        Enchantment.EnchantmentDefinition definition = new Enchantment.EnchantmentDefinition(
                TagKey.create(Registries.ITEM, new ResourceLocation(itemCompatibilityTag)),
                primaryItemsTag != null ? Optional.of(TagKey.create(Registries.ITEM, new ResourceLocation(itemCompatibilityTag))) : Optional.empty(),
                weight,
                maxLevel,
                minCost,
                maxCost,
                anvilCost,
                FeatureFlagSet.of(),
                slots
        );


        FlexEnchantment flexEnchantment = new FlexEnchantment(definition);

        flexEnchantment.setTreasure(isTreasure);
        flexEnchantment.setCurse(isCurse);
        flexEnchantment.setTradeable(isTradeable);
        flexEnchantment.setDiscoverable(isDiscoverable);
        flexEnchantment.setAllowedOnBooks(isAllowedOnBooks);
        flexEnchantment.setBlackList(blackList.stream().map(loc -> {
            // FIXME: this code is cursed
            var ro = DeferredHolder.create(Registries.ENCHANTMENT, loc);
            return (Predicate<Enchantment>) ((enchantment) -> ro.asOptional().filter(en -> en == enchantment).isPresent());
        }).toList());

        constructEventHandlers(flexEnchantment);

        return flexEnchantment;
    }
}
