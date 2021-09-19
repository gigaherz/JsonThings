package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.builders.ArmorMaterialBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class ArmorMaterialParser extends ThingParser<ArmorMaterialBuilder>
{
    public ArmorMaterialParser()
    {
        super(GSON, "armor_material");
    }

    @Override
    public void finishLoading()
    {
        getBuilders().forEach(thing -> Registry.register(ThingRegistries.ARMOR_MATERIALS, thing.getRegistryName(), thing.build()));
    }

    @Override
    public ArmorMaterialBuilder processThing(ResourceLocation key, JsonObject data)
    {
        ArmorMaterialBuilder builder = ArmorMaterialBuilder.begin(key);

        float toughness = GsonHelper.getAsFloat(data, "toughness");
        if (toughness >= 0)
        {
            builder = builder.withToughness(toughness);
        }
        else
        {
            throw new RuntimeException("'toughness' must be positive or zero.");
        }

        float knockbackResistance = GsonHelper.getAsFloat(data, "knockback_resistance");
        if (knockbackResistance >= 0)
        {
            builder = builder.withKnockbackResistance(knockbackResistance);
        }
        else
        {
            throw new RuntimeException("'knockback_resistance' must be positive or zero.");
        }

        int enchantmentValue = GsonHelper.getAsInt(data, "enchantment_value");
        if (enchantmentValue >= 0)
        {
            builder = builder.withEnchantmentValue(enchantmentValue);
        }
        else
        {
            throw new RuntimeException("'enchantment_value' must be positive or zero.");
        }

        if (data.has("repair_ingredient"))
        {
            builder = builder.withRepairIngredient(TierParser.parseMiniIngredient(data.get("repair_ingredient")));
        }
        else
        {
            throw new RuntimeException("Missing required 'repair_ingredient'.");
        }

        if (data.has("equip_sound"))
        {
            builder = builder.withEquipSound(new ResourceLocation(GsonHelper.getAsString(data, "equip_sound")));
        }
        else
        {
            throw new RuntimeException("Missing required 'equip_sound'.");
        }

        if (data.has("durability"))
        {
            builder = builder.withDurability(parseEquipmentSlotMap(data, "durability"));
        }
        else
        {
            throw new RuntimeException("Missing required 'durability'.");
        }

        if (data.has("armor"))
        {
            builder = builder.withDefense(parseEquipmentSlotMap(data, "armor"));
        }
        else
        {
            throw new RuntimeException("Missing required 'armor'.");
        }

        return builder;
    }

    private Map<EquipmentSlot, Integer> parseEquipmentSlotMap(JsonObject data, String name)
    {
        JsonElement element = data.get(name);

        Map<EquipmentSlot, Integer> map = new HashMap<>();
        if (element.isJsonObject())
        {
            var obj = element.getAsJsonObject();

            for(EquipmentSlot slot : EquipmentSlot.values())
            {
                map.put(slot, obj.get(slot.getName()).getAsInt());
            }
        }
        else if(GsonHelper.isNumberValue(element))
        {
            int number = element.getAsInt();

            for(EquipmentSlot slot : EquipmentSlot.values())
            {
                map.put(slot, number);
            }
        }
        else
        {
            throw new RuntimeException("'" + name + "' must be either a json object, or a string");
        }

        return map;
    }
}
