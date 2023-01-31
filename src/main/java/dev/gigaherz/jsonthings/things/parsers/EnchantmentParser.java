package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.EnchantmentBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.ArrayValue;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class EnchantmentParser extends ThingParser<EnchantmentBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public EnchantmentParser(IEventBus bus)
    {
        super(GSON, "enchantment");

        bus.addListener(this::register);
    }

    public void register(RegisterEvent event)
    {
        event.register(Registry.ENCHANTMENT_REGISTRY, helper -> {
            LOGGER.info("Started registering Enchantment things, errors about unexpected registry domains are harmless...");
            processAndConsumeErrors(getThingType(), getBuilders(), thing -> helper.register(thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
            LOGGER.info("Done processing thingpack Enchantments.");
        });
    }

    @Override
    public EnchantmentBuilder processThing(ResourceLocation key, JsonObject data, Consumer<EnchantmentBuilder> builderModification)
    {
        final EnchantmentBuilder builder = EnchantmentBuilder.begin(this, key);

        MutableInt minLevel = new MutableInt(1);

        JParse.begin(data)
                .ifKey("rarity", val -> val.string().map(this::parseEnchantmentRarity).handle(builder::setRarity))
                .ifKey("type", val -> val.string().map(this::parseEnchantmentType).handle(builder::setEnchantmentType))
                .ifKey("min_level", val -> val.intValue().min(1).handle(num -> {
                    minLevel.setValue(num);
                    builder.setMinLevel(num);
                }))
                .ifKey("max_level", val -> val.intValue().min(minLevel.getValue()).handle(builder::setMaxLevel))
                .ifKey("base_cost", val -> val.intValue().min(0).handle(builder::setBaseCost))
                .ifKey("per_level_cost", val -> val.intValue().min(0).handle(builder::setPerLevelCost))
                .ifKey("random_cost", val -> val.intValue().min(0).handle(builder::setRandomCost))
                .ifKey("treasure", val -> val.bool().handle(builder::setIsTreasure))
                .ifKey("curse", val -> val.bool().handle(builder::setIsCurse))
                .ifKey("tradeable", val -> val.bool().handle(builder::setIsTradeable))
                .ifKey("discoverable", val -> val.bool().handle(builder::setIsDiscoverable))
                .ifKey("allow_on_books", val -> val.bool().handle(builder::setIsAllowedOnBooks))
                .ifKey("item_compatibility", val -> val.map(ItemPredicate::fromJson).handle(builder::setItemCompatibility))
                .ifKey("disallow_enchants", val -> val.array().map(this::parseBlacklist).handle(builder::setBlacklist))
                .ifKey("events", val -> val.obj().map(this::parseEvents).handle(builder::setEventMap));

        builderModification.accept(builder);

        return builder;
    }

    private List<ResourceLocation> parseBlacklist(ArrayValue blacklist)
    {
        return blacklist.flatMap(entries -> entries.map(e -> new ResourceLocation(e.string().getAsString())).toList());
    }

    private EnchantmentCategory parseEnchantmentType(String str)
    {
        EnchantmentCategory type = types.get(str);
        if (type == null) throw new ThingParseException("No enchantment type known with name " + str);
        return type;
    }

    private Enchantment.Rarity parseEnchantmentRarity(String str)
    {
        Enchantment.Rarity rarity = rarities.get(str);
        if (rarity == null) throw new ThingParseException("No enchantment rarity known with name " + str);
        return rarity;
    }

    private static final Map<String, Enchantment.Rarity> rarities = ImmutableMap.<String, Enchantment.Rarity>builder()
            .put("common", Enchantment.Rarity.COMMON)
            .put("uncommon", Enchantment.Rarity.UNCOMMON)
            .put("rare", Enchantment.Rarity.RARE)
            .put("very_rare", Enchantment.Rarity.VERY_RARE)
            .build();
    private static final Map<String, EnchantmentCategory> types = buildTypesMap();

    private static Map<String, EnchantmentCategory> buildTypesMap()
    {
        Map<String, EnchantmentCategory> entries = Maps.newHashMap();
        entries.put("armor", EnchantmentCategory.ARMOR);
        entries.put("armor_feet", EnchantmentCategory.ARMOR_FEET);
        entries.put("armor_legs", EnchantmentCategory.ARMOR_LEGS);
        entries.put("armor_chest", EnchantmentCategory.ARMOR_CHEST);
        entries.put("armor_head", EnchantmentCategory.ARMOR_HEAD);
        entries.put("weapon", EnchantmentCategory.WEAPON);
        entries.put("digger", EnchantmentCategory.DIGGER);
        entries.put("fishing_rod", EnchantmentCategory.FISHING_ROD);
        entries.put("trident", EnchantmentCategory.TRIDENT);
        entries.put("breakable", EnchantmentCategory.BREAKABLE);
        entries.put("bow", EnchantmentCategory.BOW);
        entries.put("wearable", EnchantmentCategory.WEARABLE);
        entries.put("crossbow", EnchantmentCategory.CROSSBOW);
        entries.put("vanishable", EnchantmentCategory.VANISHABLE);
        for (EnchantmentCategory type : EnchantmentCategory.values())
        {
            if (!entries.containsValue(type))
            {
                String name = type.toString().toLowerCase(Locale.ROOT);
                entries.put(name, type);
            }
        }
        return entries;
    }
}
