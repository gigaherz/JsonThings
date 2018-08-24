package gigaherz.jsonthings.parser;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.item.builder.StackContext;
import joptsimple.internal.Strings;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.List;

public class ItemParser extends ThingParser
{
    public static final List<ItemBuilder> BUILDERS = Lists.newArrayList();

    public static void init()
    {
        (new ItemParser()).parse();
    }

    @Override
    public String getThingType()
    {
        return "item";
    }

    @Override
    public void processThing(ResourceLocation key, JsonObject data)
    {
        ItemBuilder builder = ItemBuilder.begin(key);

        if (data.has("translation_key"))
        {
            String str = data.get("translation_key").getAsString();
            if (!Strings.isNullOrEmpty(str))
            {
                builder = builder.withTranslationKey(str);
            }
            else
            {
                throw new RuntimeException("If present, translation_key must be a non-empty string.");
            }
        }

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

        if (data.has("creative_menu_stacks"))
        {
            JsonArray list = data.get("creative_menu_stacks").getAsJsonArray();
            for(JsonElement e : list)
            {
                JsonObject item = e.getAsJsonObject();
                builder = builder.withCreativeMenuStack(parseStackContext(key, item), parseTabsList(item));
            }
        }

        if (data.has("durability"))
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
        }

        // TODO: more properties

        /*

  "tool": {
    "class": "axe",
    "material": "iron"
  },

         */
        if (data.has("tool"))
        {
            JsonObject toolData = data.get("tool").getAsJsonObject();

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

            builder = builder.makeTool(type, material);
        }

        BUILDERS.add(builder);
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

        if (item.has("data"))
        {
            int meta = item.get("data").getAsInt();
            ctx = ctx.withMetadata(meta);
        }

        if (item.has("count"))
        {
            int meta = item.get("count").getAsInt();
            ctx = ctx.withCount(meta);
        }

        if(item.has("nbt"))
        {
            try
            {
                JsonElement element = item.get("nbt");
                NBTTagCompound nbt;
                if(element.isJsonObject())
                    nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                else
                    nbt = JsonToNBT.getTagFromJson(element.getAsString());
                ctx = ctx.withTag(nbt);
            }
            catch (NBTException e)
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
