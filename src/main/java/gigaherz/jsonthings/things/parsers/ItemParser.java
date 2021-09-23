package gigaherz.jsonthings.things.parsers;

import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.JsonThings;
import gigaherz.jsonthings.things.StackContext;
import gigaherz.jsonthings.things.builders.FoodBuilder;
import gigaherz.jsonthings.things.builders.ItemBuilder;
import joptsimple.internal.Strings;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
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
        getBuilders().forEach(thing -> registry.register(((Item) thing.build()).setRegistryName(thing.getRegistryName())));
        LOGGER.info("Done processing thingpack Items.");
    }

    @Override
    public ItemBuilder processThing(ResourceLocation key, JsonObject data)
    {
        ResourceLocation parentId = null;
        if (data.has("parent"))
        {
            parentId = new ResourceLocation(data.getAsString());
        }

        ItemBuilder builder = ItemBuilder.begin(key, parentId);

        if (data.has("type"))
            builder = builder.withType(data.get("type").getAsString(), data);

        if (data.has("max_stack_size"))
        {
            int stack_size = data.get("max_stack_size").getAsInt();
            if (stack_size >= 1 && stack_size <= 64)
            {
                builder = builder.withMaxStackSize(stack_size);
            }
            else
            {
                throw new RuntimeException("If present, max_stack_size must be an integer between 1 and 64, both inclusive.");
            }
        }

        if (data.has("group"))
        {
            if (data.has("creative_menu_stacks"))
            {
                throw new RuntimeException("Cannot have group and creative_menu_stacks at the same time.");
            }

            String name = data.get("group").getAsString();
            builder = builder.withCreativeMenuStack(new StackContext(null), new String[]{name});
        }

        if (data.has("creative_menu_stacks"))
        {
            builder = parseCreativeMenuStacks(key, data, builder);
        }

        if (data.has("attribute_modifiers"))
        {
            builder = parseAttributeModifiers(key, data, builder);
        }

        if (data.has("max_damage"))
        {
            int max_damage = data.get("max_damage").getAsInt();
            if (max_damage >= 1 && max_damage < 128)
            {
                builder = builder.makeDamageable(max_damage);
            }
            else
            {
                throw new RuntimeException("If present, max_stack_size must be an integer between 1 and 127, both inclusive.");
            }
        }

        if (data.has("food"))
        {
            builder = parseFoodInfo(data, builder);
        }

        if (data.has("color_handler"))
            builder = builder.withColorHandler(data.get("color_handler").getAsString());

        if (data.has("lore"))
            builder = builder.withLore(parseLore(data.get("lore").getAsJsonArray()));

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

    private ItemBuilder parseAttributeModifiers(ResourceLocation key, JsonObject data, ItemBuilder builder)
    {
        JsonArray list = data.get("attribute_modifiers").getAsJsonArray();
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

            builder = builder.withAttributeModifier(uuid, name, amount, operation);
        }
        return builder;
    }

    private ItemBuilder parseCreativeMenuStacks(ResourceLocation key, JsonObject data, ItemBuilder builder)
    {
        JsonArray list = data.get("creative_menu_stacks").getAsJsonArray();
        for (JsonElement e : list)
        {
            JsonObject item = e.getAsJsonObject();
            builder = builder.withCreativeMenuStack(parseStackContext(key, item), parseTabsList(item));
        }
        return builder;
    }

    private ItemBuilder parseFoodInfo(JsonObject data, ItemBuilder builder)
    {
        JsonElement foodData = data.get("food");
        if (foodData.isJsonPrimitive() && foodData.getAsJsonPrimitive().isString())
        {
            builder = builder.makeFood(new ResourceLocation(foodData.getAsString()));
        }
        else
        {
            FoodBuilder foodBuilder = JsonThings.foodParser.parseFromElement(builder.getRegistryName(), foodData);
            builder = builder.makeFood(foodBuilder.build());
        }
        return builder;
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
