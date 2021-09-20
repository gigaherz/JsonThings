package gigaherz.jsonthings.things.parsers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.EnchantmentBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EnchantmentParser extends ThingParser<EnchantmentBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public EnchantmentParser(IEventBus bus)
    {
        super(GSON, "enchantment");
        bus.addGenericListener(Enchantment.class, this::registerEnchantments);
    }

    public void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        LOGGER.info("Started registering Enchantment things, errors about unexpected registry domains are harmless...");
        IForgeRegistry<Enchantment> registry = event.getRegistry();
        getBuilders().forEach(thing -> registry.register((thing.build()).setRegistryName(thing.getRegistryName())));
        LOGGER.info("Done processing thingpack Enchantments.");
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
        ;
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

    @Override
    public EnchantmentBuilder processThing(ResourceLocation key, JsonObject data)
    {
        EnchantmentBuilder builder = EnchantmentBuilder.begin(key);

        if (data.has("rarity"))
            builder = builder.withRarity(parseRarity(data.get("rarity").getAsString()));

        if (data.has("type"))
            builder = builder.withEnchantmentType(parseEnchantmentType(data.get("type").getAsString()));

        int minLevel = 1;
        if (data.has("min_level"))
        {
            minLevel = data.get("min_level").getAsInt();
            if (minLevel > 0)
            {
                builder = builder.withMinLevel(minLevel);
            }
            else
            {
                throw new IllegalStateException("'min_level' must be positive and bigger than zero.");
            }
        }
        if (data.has("max_level"))
        {
            int maxLevel = data.get("max_level").getAsInt();
            if (maxLevel > minLevel)
            {
                builder = builder.withMaxLevel(minLevel);
            }
            else
            {
                throw new IllegalStateException("'min_level' must be positive and bigger than `min_level`.");
            }
        }

        if (data.has("base_cost"))
        {
            var value = data.get("base_cost").getAsInt();
            if (value >= 0)
            {
                builder = builder.withBaseCost(value);
            }
            else
            {
                throw new IllegalStateException("'base_cost' must be positive or zero.");
            }
        }
        if (data.has("per_level_cost"))
        {
            var value = data.get("per_level_cost").getAsInt();
            if (value >= 0)
            {
                builder = builder.withPerLevelCost(value);
            }
            else
            {
                throw new IllegalStateException("'per_level_cost' must be positive or zero.");
            }
        }
        if (data.has("random_cost"))
        {
            var value = data.get("random_cost").getAsInt();
            if (value >= 0)
            {
                builder = builder.withRandomCost(value);
            }
            else
            {
                throw new IllegalStateException("'random_cost' must be positive or zero.");
            }
        }

        if (data.has("item_compatibility"))
            builder = builder.withItemCompatibility(ItemPredicate.fromJson(data.get("item_compatibility")));

        if (data.has("disallow_enchants"))
            builder = builder.withBlacklist(parseBlacklist(data.get("disallow_enchants").getAsJsonArray()));

        if (data.has("treasure"))
            builder = builder.isTreasure(data.get("treasure").getAsBoolean());

        if (data.has("curse"))
            builder = builder.isCurse(data.get("curse").getAsBoolean());

        if (data.has("tradeable"))
            builder = builder.isTradeable(data.get("tradeable").getAsBoolean());

        if (data.has("discoverable"))
            builder = builder.isDiscoverable(data.get("discoverable").getAsBoolean());

        if (data.has("allow_on_books"))
            builder = builder.isAllowedOnBooks(data.get("allow_on_books").getAsBoolean());

        return builder;
    }

    private List<ResourceLocation> parseBlacklist(JsonArray blacklist)
    {
        var list = new ArrayList<ResourceLocation>();
        for (JsonElement e : blacklist)
        {
            list.add(new ResourceLocation(e.getAsString()));
        }
        return list;
    }

    private EnchantmentCategory parseEnchantmentType(String str)
    {
        EnchantmentCategory type = types.get(str);
        if (type == null) throw new IllegalStateException("No enchantment type known with name " + str);
        return type;
    }

    private Enchantment.Rarity parseRarity(String str)
    {
        Enchantment.Rarity rarity = rarities.get(str);
        if (rarity == null) throw new IllegalStateException("No enchantment rarity known with name " + str);
        return rarity;
    }
}
