package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.UseFinishMode;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.FoodBuilder;
import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.StringValue;
import joptsimple.internal.Strings;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemParser extends ThingParser<ItemBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    private Map<CreativeModeTab, List<ItemStack>> creativeStacks;

    public ItemParser(IEventBus bus)
    {
        super(GSON, "item");

        bus.addListener(this::register);
        bus.addListener(this::addToTabs);
    }

    public void register(RegisterEvent event)
    {
        event.register(Registries.ITEM, helper -> {
            LOGGER.info("Started registering Item things, errors about unexpected registry domains are harmless...");
            processAndConsumeErrors(getThingType(), getBuilders(), thing -> helper.register(thing.getRegistryName(), thing.get().self()), BaseBuilder::getRegistryName);
            LOGGER.info("Done processing thingpack Items.");
        });
    }

    public void addToTabs(CreativeModeTabEvent.BuildContents event)
    {
        if (creativeStacks == null)
        {
            Map<String, List<ItemStack>> map = new HashMap<>();
            getBuilders().forEach(thing ->
            {
                for (var entry : thing.getCreativeMenuStacks())
                {
                    var stack = entry.getFirst();
                    for (var tab : entry.getSecond())
                    {
                        var list = map.computeIfAbsent(tab, key -> new ArrayList<>());
                        list.add(stack.toStack(thing.get().self()));
                    }
                }
            });

            creativeStacks = new HashMap<>();
            for (var entry : map.entrySet())
            {
                var tab = CreativeModeTabRegistry.getTab(new ResourceLocation(entry.getKey()));
                if (tab == null)
                {
                    throw new ThingParseException("Could not find tab with name " + entry.getKey() + " used by: " + entry.getValue().stream()
                            .map(ItemStack::getDisplayName).map(Component::getString).collect(Collectors.joining(", ")));
                }
                creativeStacks.put(tab, entry.getValue());
            }
        }

        var list = creativeStacks.get(event.getTab());
        if (list != null)
            event.acceptAll(list);
    }

    @Override
    public ItemBuilder processThing(ResourceLocation key, JsonObject data, Consumer<ItemBuilder> builderModification)
    {
        final ItemBuilder builder = ItemBuilder.begin(this, key);

        JParse.begin(data)
                .ifKey("parent", val -> val.string().map(ResourceLocation::new).handle(builder::setParent))
                .ifKey("type", val -> val.string().handle(builder::setType))
                .ifKey("max_stack_size", val -> val.intValue().range(1, 128).handle(builder::setMaxStackSize))
                .mutex(List.of("group", "creative_menu_stacks"), () -> new ThingParseException("Cannot have group and creative_menu_stacks at the same time."))
                .ifKey("group", val -> val.string().handle(name -> builder.withCreativeMenuStack(new StackContext(null), new String[]{name})))
                .ifKey("creative_menu_stacks", val -> val
                        .array().forEach((i, entry) -> entry
                                .obj().raw(item -> builder.withCreativeMenuStack(parseStackContext(item), parseTabsList(item))))
                )
                .ifKey("attribute_modifiers", val -> val.array().raw(arr -> parseAttributeModifiers(arr, builder)))
                .ifKey("max_damage", val -> val.intValue().range(1, 128).handle(builder::setMaxDamage))
                .ifKey("food", val -> val
                        .ifString(str -> str.map(ResourceLocation::new).handle(builder::setFood))
                        .ifObj(obj -> obj.raw(food -> {
                            try
                            {
                                FoodBuilder foodBuilder = JsonThings.foodParser.parseFromElement(builder.getRegistryName(), food);
                                builder.setFood(foodBuilder.get());
                            }
                            catch (Exception e)
                            {
                                throw new ThingParseException("Exception while parsing nested food in " + builder.getRegistryName(), e);
                            }
                        }))
                        .typeError()
                )
                .ifKey("container", val -> val.string().map(ResourceLocation::new).handle(builder::setContainerItem))
                .ifKey("delayed_use", val -> val.obj()
                        .key("duration", val1 -> val1.intValue().handle(builder::setUseTime))
                        .key("animation", val1 -> val1.string().map(str -> UseAnim.valueOf(str.toUpperCase())).handle(builder::setUseAnim))
                        .ifKey("on_complete", val1 -> val1.string().map(str -> UseFinishMode.valueOf(str.toUpperCase())).handle(builder::setUseFinishMode)
                        )
                )
                .ifKey("color_handler", val -> val.string().handle(builder::setColorHandler))
                .ifKey("lore", val -> val.array().unwrapRaw(this::parseLore).handle(builder::setLore))
                .ifKey("tool_actions", val -> val.array().strings().flatten(StringValue::getAsString, String[]::new).handle(builder::setToolActions))
                .ifKey("events", val -> val.obj().map(this::parseEvents).handle(builder::setEventMap));

        builderModification.accept(builder);

        builder.setFactory(builder.getType().getFactory(data));

        return builder;
    }

    private List<MutableComponent> parseLore(JsonArray lines)
    {
        var lore = new ArrayList<MutableComponent>();
        for (JsonElement e : lines)
        {
            lore.add(Component.Serializer.fromJson(e));
        }
        return lore;
    }

    private void parseAttributeModifiers(JsonArray list, ItemBuilder builder)
    {
        for (JsonElement e : list)
        {
            JsonObject item = e.getAsJsonObject();

            EquipmentSlot slot;
            if (item.has("slot"))
            {
                var name = item.get("slot").getAsString();
                var names = Arrays.stream(EquipmentSlot.values()).map(EquipmentSlot::getName).toList();
                if (Strings.isNullOrEmpty(name) || !names.contains(name))
                {
                    throw new ThingParseException("Attribute modifier slot must be a valid equipment slot name: " + String.join(", ", names));
                }
                slot = EquipmentSlot.byName(name);
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
                attribute = new ResourceLocation(loc);
            }
            else
            {
                throw new ThingParseException("Attribute must be present and a valid resource location.");
            }

            UUID uuid = null;
            if (item.has("uuid"))
            {
                String uuidString = item.get("uuid").getAsString();
                if (!Strings.isNullOrEmpty(uuidString))
                {
                    uuid = UUID.fromString(uuidString);
                }
                else
                {
                    throw new ThingParseException("If present, uuid must be an UUID-formatted string.");
                }
            }

            String name;
            if (item.has("name"))
            {
                name = item.get("name").getAsString();
                if (Strings.isNullOrEmpty(name))
                {
                    throw new ThingParseException("Attribute modifier name must be a non-empty string.");
                }
            }
            else
            {
                throw new ThingParseException("Attribute modifier name must be a non-empty string.");
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

            int operation;
            if (item.has("operation"))
            {
                String opName = item.get("operation").getAsString();
                Integer opInt = Ints.tryParse(opName);
                if (opInt == null)
                {
                    operation = AttributeModifier.Operation.valueOf(opName.toUpperCase()).toValue();
                }
                else
                {
                    operation = opInt;
                }
            }
            else
            {
                throw new ThingParseException("Attribute modifier amount must have an operation type.");
            }

            builder.withAttributeModifier(slot, attribute, uuid, name, amount, operation);
        }
    }

    private String[] parseTabsList(JsonObject stackEntry)
    {
        if (stackEntry.has("tabs"))
        {
            JsonArray tabs = stackEntry.get("tabs").getAsJsonArray();

            String[] tabsArray = new String[tabs.size()];
            int tabIndex = 0;
            for (JsonElement e : tabs)
            {
                String str = e.getAsString();
                if (!Strings.isNullOrEmpty(str))
                {
                    tabsArray[tabIndex++] = str;
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
