package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.ArmorMaterialBuilder;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ArmorMaterialParser extends ThingParser<ArmorMaterialBuilder>
{
    public ArmorMaterialParser(IEventBus bus)
    {
        super(GSON, "armor_material");

        bus.addListener(this::register);
    }

    public void register(RegisterEvent event)
    {
        event.register(Registries.ARMOR_MATERIAL, helper -> {
            LOGGER.info("Started registering ArmorMaterial things, errors about unexpected registry domains are harmless...");
            processAndConsumeErrors(getThingType(), getBuilders(), thing -> helper.register(thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
            LOGGER.info("Done processing thingpack ArmorMaterials.");
        });
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
                .key("equip_sound", val -> val.string().map(ResourceLocation::parse).handle(builder::setEquipSound))
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
                        obj.ifKey(slot.getName(), val -> val.intValue().min(0).handle(num -> map.put(slot, num)));
                    }
                })
                .ifInteger(val -> val.min(0).handle(num -> {
                    for (ArmorItem.Type slot : ArmorItem.Type.values())
                    {
                        map.put(slot, num);
                    }
                }))
                .typeError();

        return map;
    }
}
