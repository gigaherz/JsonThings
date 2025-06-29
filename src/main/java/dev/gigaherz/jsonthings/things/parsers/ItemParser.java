package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.UseFinishMode;
import dev.gigaherz.jsonthings.things.builders.FoodPropertiesBuilder;
import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.StringValue;
import joptsimple.internal.Strings;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;

public class ItemParser extends ThingParser<Item, ItemBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    private final Map<ResourceKey<CreativeModeTab>, List<ItemStack>> creativeStacks = new HashMap<>();

    public ItemParser(IEventBus bus)
    {
        super(GSON, "item");

        register(bus, Registries.ITEM);

        bus.addListener(this::addToTabs);
    }

    public void addToTabs(BuildCreativeModeTabContentsEvent event)
    {
        getBuilders().forEach(thing -> thing.provideVariants(event.getTabKey(), event, event.getParameters(), thing, false));
    }

    @Override
    public ItemBuilder processThing(ResourceLocation key, JsonObject data, Consumer<ItemBuilder> builderModification)
    {
        final ItemBuilder builder = ItemBuilder.begin(this, key);

        JParse.begin(data)
                .ifKey("parent", val -> val.string().map(ResourceLocation::parse).handle(builder::setParent))
                .ifKey("type", val -> val.string().handle(builder::setType))
                .ifKey("max_stack_size", val -> val.intValue().range(1, 128).handle(builder::setMaxStackSize))
                .mutex(List.of("group", "creative_menu_stacks"), () -> new ThingParseException("Cannot have group and creative_menu_stacks at the same time."))
                .ifKey("group", val -> val.string().map(ResourceLocation::parse).handle(builder::setGroup))
                .ifKey("creative_menu_stacks", val -> val
                        .array().forEach((i, entry) -> entry
                                .obj().raw(item -> builder.withCreativeMenuStack(parseStackContext(item, false, false), parseTabsList(item))))
                )
                .ifKey("attribute_modifiers", val -> val.array().raw(arr -> parseAttributeModifiers(arr, builder)))
                .ifKey("max_damage", val -> val.intValue().min(1).handle(builder::setMaxDamage))
                .ifKey("fire_resistant", val -> val.bool().handle(builder::setFireResistant))
                .ifKey("food", val -> val
                        .ifString(str -> str.map(ResourceLocation::parse).handle(builder::setFood))
                        .ifObj(obj -> obj.raw(food -> {
                            try
                            {
                                FoodPropertiesBuilder foodPropertiesBuilder = JsonThings.foodPropertiesParser.parseFromElement(builder.getRegistryName(), food);
                                if (foodPropertiesBuilder != null)
                                    builder.setFood(foodPropertiesBuilder.get());
                            }
                            catch (Exception e)
                            {
                                throw new ThingParseException("Exception while parsing nested food in " + builder.getRegistryName() + ": " + e.getMessage(), e);
                            }
                        }))
                        .typeError()
                )
                .ifKey("container", val -> val.string().map(ResourceLocation::parse).handle(builder::setContainerItem))
                .ifKey("delayed_use", val -> val.obj()
                        .key("duration", val1 -> val1.intValue().handle(builder::setUseTime))
                        .key("animation", val1 -> val1.string().map(str -> ItemUseAnimation.valueOf(str.toUpperCase())).handle(builder::setUseAnim))
                        .ifKey("on_complete", val1 -> val1.string().map(str -> UseFinishMode.valueOf(str.toUpperCase())).handle(builder::setUseFinishMode)
                        )
                )
                .ifKey("color_handler", val -> val.string().handle(builder::setColorHandler))
                .ifKey("lore", val -> val.array().unwrapRaw(this::parseLore).handle(builder::setLore))
                .ifKey("tool_actions", val -> val.array().strings().flatten(StringValue::getAsString, String[]::new).handle(builder::setToolActions))
                .ifKey("events", val -> val.obj().map(this::parseEvents).handle(builder::setEventMap))
                .ifKey("burn_duration", val -> val.intValue().min(1).handle(builder::setBurnDuration))
                .ifKey("components", val -> val.obj().raw(builder::setComponents));

        builderModification.accept(builder);

        builder.setFactory(builder.getType().getFactory(data));

        return builder;
    }

    private static final Codec<List<Component>> LORE_CODEC = ComponentSerialization.CODEC.listOf();
    private List<Component> parseLore(JsonArray lines)
    {
        return LORE_CODEC.decode(JsonOps.INSTANCE, lines).getOrThrow(ThingParseException::new).getFirst();
    }

    private void parseAttributeModifiers(JsonArray list, ItemBuilder builder)
    {
        for (JsonElement e : list)
        {
            JsonObject item = e.getAsJsonObject();

            EquipmentSlotGroup slot;
            if (item.has("slot"))
            {
                var name = item.get("slot").getAsString();
                slot = EquipmentSlotGroup.bySlot(EquipmentSlot.byName(name));
            }
            else
            {
                throw new ThingParseException("Attribute modifier slot must be a non-empty string.");
            }

            ResourceLocation attribute;
            if (item.has("attribute"))
            {
                var loc = item.get("attribute").getAsString();
                if (Strings.isNullOrEmpty(loc))
                {
                    throw new ThingParseException("Attribute must be present and a valid resource location.");
                }
                attribute = ResourceLocation.parse(loc);
            }
            else
            {
                throw new ThingParseException("Attribute must be present and a valid resource location.");
            }

            ResourceLocation id;
            if (item.has("id"))
            {
                String uuidString = item.get("id").getAsString();
                if (!Strings.isNullOrEmpty(uuidString))
                {
                    id = ResourceLocation.parse(uuidString);
                }
                else
                {
                    throw new ThingParseException("Attribute modifier id must be present and a valid resource location.");
                }
            }
            else
            {
                throw new ThingParseException("Attribute modifier id must be present and a valid resource location.");
            }

            double amount;
            if (item.has("amount"))
            {
                amount = item.get("amount").getAsDouble();
            }
            else
            {
                throw new ThingParseException("Attribute modifier amount must be a floating point number.");
            }

            AttributeModifier.Operation operation;
            if (item.has("operation"))
            {
                String opName = item.get("operation").getAsString();
                Integer opInt = Ints.tryParse(opName);
                operation = opInt != null
                        ? AttributeModifier.Operation.BY_ID.apply(opInt)
                        : AttributeModifier.Operation.valueOf(opName.toUpperCase());
            }
            else
            {
                throw new ThingParseException("Attribute modifier amount must have an operation type.");
            }

            builder.withAttributeModifier(slot, attribute, id, amount, operation);
        }
    }

    public static ResourceLocation[] parseTabsList(JsonObject stackEntry)
    {
        if (stackEntry.has("tabs"))
        {
            JsonArray tabs = stackEntry.get("tabs").getAsJsonArray();

            ResourceLocation[] tabsArray = new ResourceLocation[tabs.size()];
            int tabIndex = 0;
            for (JsonElement e : tabs)
            {
                String str = e.getAsString();
                if (!Strings.isNullOrEmpty(str))
                {
                    tabsArray[tabIndex++] = ResourceLocation.parse(str);
                }
                else
                {
                    throw new ThingParseException("Tabs array must contain non-empty strings.");
                }
            }
            return tabsArray;
        }
        else
        {
            throw new ThingParseException("Creative menu entry must contain a list of tabs.");
        }
    }
}
