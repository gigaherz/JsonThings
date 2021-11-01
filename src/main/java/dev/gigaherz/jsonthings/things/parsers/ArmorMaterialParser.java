package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.ArmorMaterialBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import dev.gigaherz.jsonthings.util.parse.value.IntValue;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

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
        getBuilders().forEach(thing -> Registry.register(ThingRegistries.ARMOR_MATERIALS, thing.getRegistryName(), thing.get()));
    }

    @Override
    public ArmorMaterialBuilder processThing(ResourceLocation key, JsonObject data)
    {
        final ArmorMaterialBuilder builder = ArmorMaterialBuilder.begin(key);

        JParse.begin(data)
                .obj()
                .key("toughness", val -> val.floatValue().min(0).handle(builder::setToughness))
                .key("knockback_resistance", val -> val.floatValue().min(0).handle(builder::withKnockbackResistance))
                .key("enchantment_value", val -> val.intValue().min(0).handle(builder::withEnchantmentValue))
                .key("repair_ingredient", val -> val.map(TierParser::parseMiniIngredient).handle(builder::withRepairIngredient))
                .key("equip_sound", val -> val.string().map(ResourceLocation::new).handle(builder::withEquipSound))
                .key("durability", val -> val.map(this::parseEquipmentSlotMap).handle(builder::withDurability))
                .key("armor", val -> val.map(this::parseEquipmentSlotMap).handle(builder::withDefense));

        return builder;
    }

    private Map<EquipmentSlotType, Integer> parseEquipmentSlotMap(Any data)
    {
        Map<EquipmentSlotType, Integer> map = new HashMap<>();

        data
                .ifObj(obj -> {
                    for (EquipmentSlotType slot : EquipmentSlotType.values())
                    {
                        obj.ifKey(slot.getName(), val -> val.intValue().handle(num -> map.put(slot, num)));
                    }
                })
                .ifInteger(val -> {
                    int num = val.getAsInt();
                    for (EquipmentSlotType slot : EquipmentSlotType.values())
                    {
                        map.put(slot, num);
                    }
                })
                .typeError();

        return map;
    }
}
