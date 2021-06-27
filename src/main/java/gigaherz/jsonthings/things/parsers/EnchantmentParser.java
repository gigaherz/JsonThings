package gigaherz.jsonthings.things.parsers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.EnchantmentBuilder;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

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
    private static final Map<String, EnchantmentType> types = buildTypesMap();

    private static Map<String, EnchantmentType> buildTypesMap()
    {
        Map<String, EnchantmentType> entries = Maps.newHashMap();
        entries.put("armor", EnchantmentType.ARMOR);
        entries.put("armor_feet", EnchantmentType.ARMOR_FEET);
        entries.put("armor_legs", EnchantmentType.ARMOR_LEGS);
        entries.put("armor_chest", EnchantmentType.ARMOR_CHEST);
        entries.put("armor_head", EnchantmentType.ARMOR_HEAD);
        entries.put("weapon", EnchantmentType.WEAPON);
        entries.put("digger", EnchantmentType.DIGGER);
        entries.put("fishing_rod", EnchantmentType.FISHING_ROD);
        entries.put("trident", EnchantmentType.TRIDENT);
        entries.put("breakable", EnchantmentType.BREAKABLE);
        entries.put("bow", EnchantmentType.BOW);
        entries.put("wearable", EnchantmentType.WEARABLE);
        entries.put("crossbow", EnchantmentType.CROSSBOW);
        entries.put("vanishable", EnchantmentType.VANISHABLE);;
        for(EnchantmentType type : EnchantmentType.values())
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

    private EnchantmentType parseEnchantmentType(String str)
    {
        EnchantmentType type = types.get(str);
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
