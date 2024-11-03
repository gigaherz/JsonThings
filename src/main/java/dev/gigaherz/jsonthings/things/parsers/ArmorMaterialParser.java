package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.ArmorMaterialBuilder;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.util.Utils;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ArmorMaterialParser extends ThingParser<ArmorMaterial, ArmorMaterialBuilder>
{
    public ArmorMaterialParser(IEventBus bus)
    {
        super(GSON, "armor_material");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(ThingRegistries.ARMOR_MATERIAL, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }

    @Override
    public ArmorMaterialBuilder processThing(ResourceLocation key, JsonObject data, Consumer<ArmorMaterialBuilder> builderModification)
    {
        final ArmorMaterialBuilder builder = ArmorMaterialBuilder.begin(this, key);

        JParse.begin(data)
                .key("toughness", val -> val.floatValue().min(0).handle(builder::setToughness))
                .key("knockback_resistance", val -> val.floatValue().min(0).handle(builder::setKnockbackResistance))
                .key("enchantment_value", val -> val.intValue().min(0).handle(builder::setEnchantmentValue))
                .key("repair_ingredient", val -> val.string().map(Utils::itemTag).handle(builder::setRepairIngredient))
                .key("equip_sound", val -> val.string().map(ResourceLocation::parse).handle(builder::setEquipSound))
                .key("durability", val -> val.intValue().handle(builder::setDurability))
                .key("armor", val -> val.map(this::parseArmorType).handle(builder::setDefense));

        builderModification.accept(builder);

        return builder;
    }

    private Map<ArmorType, Integer> parseArmorType(Any data)
    {
        Map<ArmorType, Integer> map = new HashMap<>();

        data
                .ifObj(obj -> {
                    for (ArmorType slot : ArmorType.values())
                    {
                        obj.ifKey(slot.getName(), val -> val.intValue().min(0).handle(num -> map.put(slot, num)));
                    }
                })
                .ifInteger(val -> val.min(0).handle(num -> {
                    for (ArmorType slot : ArmorType.values())
                    {
                        map.put(slot, num);
                    }
                }))
                .typeError();

        return map;
    }
}
