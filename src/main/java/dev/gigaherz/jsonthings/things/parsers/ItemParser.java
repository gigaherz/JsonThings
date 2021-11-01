package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.util.parse.JParse;
import joptsimple.internal.Strings;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemParser extends ThingParser<ItemBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public ItemParser(IEventBus bus)
    {
        super(GSON, "item");
        bus.addGenericListener(Item.class, this::registerItems);
    }

    public void registerItems(RegistryEvent.Register<Item> event)
    {
        LOGGER.info("Started registering Item things, errors about unexpected registry domains are harmless...");
        IForgeRegistry<Item> registry = event.getRegistry();
        getBuilders().forEach(thing -> registry.register((thing.get().self()).setRegistryName(thing.getRegistryName())));
        LOGGER.info("Done processing thingpack Items.");
    }

    @Override
    public ItemBuilder processThing(ResourceLocation key, JsonObject data)
    {
        final ItemBuilder builder = ItemBuilder.begin(key, data);

        JParse.begin(data)
                .obj()
                .ifKey("parent", val -> val.string().map(ResourceLocation::new).handle(builder::setParent))
                .ifKey("type", val -> val.string().handle(builder::setType))
                .ifKey("max_stack_size", val -> val.intValue().range(1, 128).handle(builder::setMaxStackSize))
                .mutex(Lists.newArrayList("group", "creative_menu_stacks"), () -> new RuntimeException("Cannot have group and creative_menu_stacks at the same time."))
                .ifKey("group", val -> val.string().handle(name -> builder.withCreativeMenuStack(new StackContext(null), new String[]{name})))
                .ifKey("creative_menu_stacks", val -> val
                        .array().forEach((i, entry) -> entry
                                .obj().raw(item -> builder.withCreativeMenuStack(parseStackContext(item), parseTabsList(item))))
                )
                .ifKey("attribute_modifiers", val -> val.array().raw(arr -> parseAttributeModifiers(arr, builder)))
                .ifKey("max_damage", val -> val.intValue().range(1, 128).handle(builder::setMaxDamage))
                .ifKey("food", val -> val
                        .ifString(str -> str.map(ResourceLocation::new).handle(builder::setFood))
                        .ifObj(obj -> obj.raw(food -> builder.setFood(JsonThings.foodParser.parseFromElement(builder.getRegistryName(), food).get())))
                        .typeError()
                )
                .ifKey("color_handler", val -> val.string().handle(builder::setColorHandler))
                .ifKey("lore", val -> val.array().map(this::parseLore).handle(builder::setLore));

        return builder;
    }

    private List<IFormattableTextComponent> parseLore(JsonArray lines)
    {
        List<IFormattableTextComponent> lore = new ArrayList<IFormattableTextComponent>();
        for (JsonElement e : lines)
        {
            lore.add(TextComponent.Serializer.fromJson(e));
        }
        return lore;
    }

    private void parseAttributeModifiers(JsonArray list, ItemBuilder builder)
    {
        for (JsonElement e : list)
        {
            JsonObject item = e.getAsJsonObject();

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
                    throw new RuntimeException("If present, uuid must be an UUID-formatted string.");
                }
            }

            String name;
            if (item.has("name"))
            {
                name = item.get("name").getAsString();
                if (Strings.isNullOrEmpty(name))
                {
                    throw new RuntimeException("Attribute modifier name must be a non-empty string.");
                }
            }
            else
            {
                throw new RuntimeException("Attribute modifier name must be a non-empty string.");
            }

            double amount;
            if (item.has("amount"))
            {
                amount = item.get("amount").getAsDouble();
            }
            else
            {
                throw new RuntimeException("Attribute modifier amount must be a floating point number.");
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
                throw new RuntimeException("Attribute modifier amount must have an operation type.");
            }

            builder.withAttributeModifier(uuid, name, amount, operation);
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
                    throw new RuntimeException("Tabs array must contain non-empty strings.");
                }
            }
            return tabsArray;
        }
        else
        {
            throw new RuntimeException("Creative menu entry must contain a list of tabs.");
        }
    }
}
