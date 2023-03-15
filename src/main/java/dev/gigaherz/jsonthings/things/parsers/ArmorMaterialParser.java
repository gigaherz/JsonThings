package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.ArmorMaterialBuilder;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ArmorMaterialParser extends ThingParser<ArmorMaterialBuilder>
{
    public ArmorMaterialParser()
    {
        super(GSON, "armor_material");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(ThingRegistries.ARMOR_MATERIALS, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }

    @Override
    public ArmorMaterialBuilder processThing(ResourceLocation key, JsonObject data, Consumer<ArmorMaterialBuilder> builderModification)
    {
        final ArmorMaterialBuilder builder = ArmorMaterialBuilder.begin(this, key);

        JParse.begin(data)
                .key("toughness", val -> val.floatValue().min(0).handle(builder::setToughness))
                .key("knockback_resistance", val -> val.floatValue().min(0).handle(builder::setKnockbackResistance))
                .key("enchantment_value", val -> val.intValue().min(0).handle(builder::setEnchantmentValue))
                .key("repair_ingredient", val -> val.map(TierParser::parseMiniIngredient).handle(builder::setRepairIngredient))
                .key("equip_sound", val -> val.string().map(ResourceLocation::new).handle(builder::setEquipSound))
                .key("durability", val -> val.map(this::parseArmorType).handle(builder::setDurability))
                .key("armor", val -> val.map(this::parseArmorType).handle(builder::setDefense));

        builderModification.accept(builder);

        return builder;
    }

    private Map<ArmorItem.Type, Integer> parseArmorType(Any data)
    {
        Map<ArmorItem.Type, Integer> map = new HashMap<>();

        data
                .ifObj(obj -> {
                    for (ArmorItem.Type slot : ArmorItem.Type.values())
                    {
                        obj.ifKey(slot.getName(), val -> val.intValue().handle(num -> map.put(slot, num)));
                    }
                })
                .ifInteger(val -> {
                    var num = val.getAsInt();
                    for (ArmorItem.Type slot : ArmorItem.Type.values())
                    {
                        map.put(slot, num);
                    }
                })
                .typeError();

        return map;
    }
}
