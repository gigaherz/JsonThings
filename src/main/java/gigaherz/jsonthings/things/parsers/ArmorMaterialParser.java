package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.builders.ArmorMaterialBuilder;
import gigaherz.jsonthings.util.parse.JParse;
import gigaherz.jsonthings.util.parse.value.Any;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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

    private Map<EquipmentSlot, Integer> parseEquipmentSlotMap(Any data)
    {
        Map<EquipmentSlot, Integer> map = new HashMap<>();

        data
                .ifObj(obj -> {
                    for (EquipmentSlot slot : EquipmentSlot.values())
                    {
                        obj.ifKey(slot.getName(), val -> val.intValue().handle(num -> map.put(slot, num)));
                    }
                })
                .ifInteger(val -> {
                    var num = val.getAsInt();
                    for (EquipmentSlot slot : EquipmentSlot.values())
                    {
                        map.put(slot, num);
                    }
                })
                .typeError();

        return map;
    }
}
