package gigaherz.jsonthings.parser;

import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.item.builder.StackContext;
import joptsimple.internal.Strings;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Food;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

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
            String name = data.get("group").getAsString();
            builder = builder.withItemGroup(name);
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

        return builder;
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
                    operation = AttributeModifier.Operation.valueOf(opName.toUpperCase()).getId();
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
            for(JsonElement e : toolArray)
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
        ToolType type;
        if (toolData.has("class"))
        {
            String str = toolData.get("class").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                type = ToolType.get(str);
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

        builder = builder.withTool(type, material);
        return builder;
    }

    private ItemBuilder parseFoodInfo(JsonObject data, ItemBuilder builder)
    {
        JsonElement foodData = data.get("food");
        if (foodData.isJsonPrimitive() && foodData.getAsJsonPrimitive().isString())
        {
            builder = builder.makeFood(foodData.getAsJsonPrimitive().getAsString());
        }
        else
        {
            JsonObject toolData = data.get("food").getAsJsonObject();

            int healAmount;
            if (toolData.has("heal_amount"))
            {
                int str = toolData.get("heal_amount").getAsInt();
                if (str > 0)
                {
                    healAmount = str;
                }
                else
                {
                    throw new RuntimeException("Heal amount must be > 0.");
                }
            }
            else
            {
                throw new RuntimeException("Food info must have a non-empty 'heal_amount' number.");
            }

            float saturation;
            if (toolData.has("saturation"))
            {
                float str = toolData.get("saturation").getAsFloat();
                if (str >= 0)
                {
                    saturation = str;
                }
                else
                {
                    throw new RuntimeException("Food saturation not be negative.");
                }
            }
            else
            {
                throw new RuntimeException("Food info must have a non-empty 'saturation' number.");
            }

            boolean isMeat = false;
            if (toolData.has("meat"))
            {
                isMeat = toolData.get("meat").getAsBoolean();
            }

            Food.Builder foodBuilder = new Food.Builder().hunger(healAmount).saturation(saturation);
            if (isMeat) foodBuilder.meat();
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

    private StackContext parseStackContext(ResourceLocation key, JsonObject item)
    {
        StackContext ctx;

        if (item.has("item"))
        {
            String name = item.get("item").getAsString();
            if (!Strings.isNullOrEmpty(name))
            {
                ctx = new StackContext(makeResourceLocation(key, name));
            }
            else
            {
                throw new RuntimeException("If present, item must be a non-empty string.");
            }
        }
        else
        {
            ctx = new StackContext(null);
        }

        if (item.has("count"))
        {
            int meta = item.get("count").getAsInt();
            ctx = ctx.withCount(meta);
        }

        if (item.has("nbt"))
        {
            try
            {
                JsonElement element = item.get("nbt");
                CompoundNBT nbt;
                if (element.isJsonObject())
                    nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                else
                    nbt = JsonToNBT.getTagFromJson(element.getAsString());
                ctx = ctx.withTag(nbt);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to parse NBT json.", e);
            }
        }

        return ctx;
    }

    private ResourceLocation makeResourceLocation(ResourceLocation key, String name)
    {
        if (name.contains(":"))
            return new ResourceLocation(name);
        return new ResourceLocation(key.getNamespace(), name);
    }
}
