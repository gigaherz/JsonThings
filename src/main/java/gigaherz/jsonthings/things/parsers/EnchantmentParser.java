package gigaherz.jsonthings.things.parsers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.EnchantmentBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Locale;
import java.util.Map;

public class EnchantmentParser extends ThingParser<EnchantmentBuilder>
{
    public EnchantmentParser()
    {
        super(GSON, "enchantment");
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

        if (data.has("minLevel"))
            builder = builder.withMinLevel(data.get("minLevel").getAsInt());
        if (data.has("maxLevel"))
            builder = builder.withMaxLevel(data.get("maxLevel").getAsInt());

        if (data.has("minCost"))
            builder = builder.withMinCost(data.get("minCost").getAsInt());
        if (data.has("maxCost"))
            builder = builder.withMaxCost(data.get("maxCost").getAsInt());

        return builder;
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
