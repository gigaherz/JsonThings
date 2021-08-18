package gigaherz.jsonthings.things.parsers;

import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.FoodBuilder;
import gigaherz.jsonthings.things.builders.ItemBuilder;
import gigaherz.jsonthings.things.builders.StackContext;
import joptsimple.internal.Strings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class ItemParser extends ThingParser<ItemBuilder>
{
    public ItemParser()
    {
        super(GSON, "item");
    }

    @Override
    public ItemBuilder processThing(ResourceLocation key, JsonObject data)
    {
        ItemBuilder builder = ItemBuilder.begin(key);

        if (data.has("parent"))
            builder = parseParent(data.get("parent"), builder);

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
            builder = builder.withCreativeMenuStack(new StackContext(key), new String[]{name});
        }

        if (data.has("creative_menu_stacks"))
        {
            builder = parseCreativeMenuStacks(key, data, builder);
        }

        if (data.has("attribute_modifiers"))
        {
            builder = parseAttributeModifiers(key, data, builder);
        }

        if (data.has("durability"))
        {
            builder = parseDurabilityInfo(data, builder);
        }

        if (data.has("tool"))
        {
            builder = parseToolInfo(data, builder);
        }

        if (data.has("food"))
        {
            builder = parseFoodInfo(data, builder);
        }

        if (data.has("armor"))
        {
            builder = parseArmorInfo(data, builder);
        }

        if (data.has("color_handler"))
            builder = builder.withColorHandler(data.get("color_handler").getAsString());

        return builder;
    }

    private ItemBuilder parseParent(JsonElement data, ItemBuilder builder)
    {
        if (data.isJsonObject())
        {
            JsonObject obj = data.getAsJsonObject();
            String id = GsonHelper.getAsString(obj, "id");
            boolean isBuilder = GsonHelper.getAsBoolean(obj, "is_builder", true);
            if (isBuilder)
                return builder.withParentBuilder(new ResourceLocation(id));
            else
                return builder.withParentItem(new ResourceLocation(id));
        }
        return builder.withParentBuilder(new ResourceLocation(data.getAsString()));
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
                throw new RuntimeException("Attribute modifier amount must be a floating point number.");
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

    private ItemBuilder parseDurabilityInfo(JsonObject data, ItemBuilder builder)
    {
        JsonObject durability = data.get("durability").getAsJsonObject();

        if (durability.has("max_damage"))
        {
            int max_damage = durability.get("max_damage").getAsInt();
            if (max_damage >= 1)
            {
                builder = builder.makeDamageable(max_damage);
            }
            else
            {
                throw new RuntimeException("If present, max_stack_size must be an integer between 1 and 64, both inclusive.");
            }
        }
        return builder;
    }

    private ItemBuilder parseArmorInfo(JsonObject data, ItemBuilder builder)
    {
        JsonObject toolData = data.get("armor").getAsJsonObject();

        String slot;
        if (toolData.has("equipment_slot"))
        {
            String str = toolData.get("equipment_slot").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                slot = str;
            }
            else
            {
                throw new RuntimeException("Armor equipment slot must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Armor info must have a non-empty 'equipment_slot' string.");
        }

        String material;
        if (toolData.has("material"))
        {
            String str = toolData.get("material").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                material = str;
            }
            else
            {
                throw new RuntimeException("Armor material must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Armor info must have a non-empty 'material' string.");
        }

        builder = builder.makeArmor(slot, material);
        return builder;
    }

    private ItemBuilder parseToolInfo(JsonObject data, ItemBuilder builder)
    {
        JsonElement tool = data.get("tool");
        if (tool.isJsonArray())
        {
            JsonArray toolArray = tool.getAsJsonArray();
            for (JsonElement e : toolArray)
            {
                JsonObject toolData = e.getAsJsonObject();

                builder = parseSingleTool(builder, toolData);
            }
        }
        else
        {
            JsonObject toolData = tool.getAsJsonObject();

            builder = parseSingleTool(builder, toolData);
        }
        return builder;
    }

    private ItemBuilder parseSingleTool(ItemBuilder builder, JsonObject toolData)
    {
        String type;
        if (toolData.has("class"))
        {
            String str = toolData.get("class").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                type = str;
            }
            else
            {
                throw new RuntimeException("Tool class must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Tool info must have a non-empty 'class' string.");
        }

        String material;
        if (toolData.has("material"))
        {
            String str = toolData.get("material").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                material = str;
            }
            else
            {
                throw new RuntimeException("Tool material must be a non-empty string.");
            }
        }
        else
        {
            throw new RuntimeException("Tool info must have a non-empty 'material' string.");
        }

        builder = builder.withTool(type, new ResourceLocation(material));
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
            FoodBuilder foodBuilder = ThingResourceManager.INSTANCE.foodParser.parseFromElement(builder.getRegistryName(), foodData);
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
