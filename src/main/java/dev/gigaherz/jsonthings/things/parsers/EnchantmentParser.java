package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.EnchantmentBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import dev.gigaherz.jsonthings.util.parse.value.ArrayValue;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
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
        event.register(Registries.ENCHANTMENT, helper -> {
            LOGGER.info("Started registering Enchantment things, errors about unexpected registry domains are harmless...");
            processAndConsumeErrors(getThingType(), getBuilders(), thing -> helper.register(thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
            LOGGER.info("Done processing thingpack Enchantments.");
        });
    }

    @Override
    public EnchantmentBuilder processThing(ResourceLocation key, JsonObject data, Consumer<EnchantmentBuilder> builderModification)
    {
        final EnchantmentBuilder builder = EnchantmentBuilder.begin(this, key);

        JParse.begin(data)
                .key("supported_items", val -> val.string().handle(builder::setItemCompatibilityTag))
                .key("weight", val -> val.intValue().min(1).handle(builder::setWeight))
                .key("slots", val -> val.array().map(this::parseSlot).flatten(EquipmentSlot[]::new).handle(builder::setSlots))
                .ifKey("max_level", val -> val.intValue().min(1).handle(builder::setMaxLevel))
                .ifKey("min_cost", val -> val.map(this::parseCost).handle(builder::setMinCost))
                .ifKey("max_cost", val -> val.map(this::parseCost).handle(builder::setMaxCost))
                .ifKey("anvil_cost", val -> val.intValue().min(0).handle(builder::setAnvilCost))
                .ifKey("treasure", val -> val.bool().handle(builder::setIsTreasure))
                .ifKey("curse", val -> val.bool().handle(builder::setIsCurse))
                .ifKey("tradeable", val -> val.bool().handle(builder::setIsTradeable))
                .ifKey("discoverable", val -> val.bool().handle(builder::setIsDiscoverable))
                .ifKey("allow_on_books", val -> val.bool().handle(builder::setIsAllowedOnBooks))
                .ifKey("disallow_enchants", val -> val.array().mapWhole(this::parseBlacklist).handle(builder::setBlacklist))
                .ifKey("events", val -> val.obj().map(this::parseEvents).handle(builder::setEventMap));

        builderModification.accept(builder);

        return builder;
    }

    private EquipmentSlot parseSlot(Any any)
    {
        return any.string().map(EquipmentSlot::byName).value();
    }

    private Enchantment.Cost parseCost(Any val)
    {
        final var cost = new MutableObject<Enchantment.Cost>();
        val
                .ifInteger(ival -> ival.min(0).map(Enchantment::constantCost).handle(cost::setValue))
                .ifObj(obj -> {
                    var base = new MutableInt();
                    var perLevel = new MutableInt(0);
                    obj
                            .key("base", val1 -> val1.intValue().min(0).handle(base::setValue))
                            .ifKey("per_level_above_first", val1 -> val1.intValue().min(0).handle(perLevel::setValue));
                    cost.setValue(Enchantment.dynamicCost(base.intValue(), perLevel.intValue()));
                })
                .typeError();
        return cost.getValue();
    }

    private List<ResourceLocation> parseBlacklist(ArrayValue blacklist)
    {
        return blacklist.flatMap(entries -> entries.map(e -> new ResourceLocation(e.string().getAsString())).toList());
    }
}
