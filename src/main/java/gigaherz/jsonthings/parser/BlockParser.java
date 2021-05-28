package gigaherz.jsonthings.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.block.builder.BlockBuilder;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.item.builder.StackContext;
import gigaherz.jsonthings.microregistries.PropertyType;
import gigaherz.jsonthings.microregistries.ThingsByName;
import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class BlockParser extends ThingParser<BlockBuilder>
{
    public BlockParser()
    {
        super(GSON, "block");
    }

    @Override
    public BlockBuilder processThing(ResourceLocation key, JsonObject data)
    {
        BlockBuilder builder = BlockBuilder.begin(key);

        if (data.has("item"))
        {
            JsonElement item = data.get("item");
            if (item.isJsonPrimitive())
            {
                if (item.getAsBoolean())
                {
                    builder = createStockItemBlock(builder);
                }
            }
            else if (item.isJsonObject())
            {
                builder = parseItemBlock(data.get("item").getAsJsonObject(), builder);
            }
            else
            {
                throw new RuntimeException("If present, 'item' must be a boolean or an object declaring the item values.");
            }
        }

        if (data.has("properties"))
        {
            JsonObject props = data.get("properties").getAsJsonObject();
            builder = parseProperties(props, builder);
        }

        if (data.has("default_state"))
        {
            JsonObject props = data.get("default_state").getAsJsonObject();
            builder = parseBlockState(props, builder);
        }

        return builder;
    }

    private BlockBuilder parseBlockState(JsonObject props, BlockBuilder builder)
    {
        for(Map.Entry<String, JsonElement> entry : props.entrySet())
        {
            String name = entry.getKey();
            JsonElement value = entry.getValue();
            builder = builder.withDefaultState(name, value.getAsString());
        }
        return builder;
    }

    private BlockBuilder parseProperties(JsonObject props, BlockBuilder builder)
    {
        for(Map.Entry<String, JsonElement> entry : props.entrySet())
        {
            String name = entry.getKey();
            JsonElement value = entry.getValue();
            Property<?> property;
            if (value.isJsonPrimitive())
            {
                property = ThingsByName.PROPERTIES.get(value.getAsString());
                if (!property.getName().equals(name))
                {
                    throw new IllegalStateException("The stock property '" + value.getAsString() + "' does not have the expected name '" + name + "' != '" + property.getName() + "'");
                }
            }
            else
            {
                property = PropertyType.deserialize(name, value.getAsJsonObject());
            }
            builder = builder.withProperty(property);
        }
        return builder;
    }

    private BlockBuilder createStockItemBlock(BlockBuilder builder)
    {
        ItemBuilder itemBuilder = ThingResourceManager.INSTANCE.itemParser.parseFromElement(builder.getRegistryName(), new JsonObject()).makeBlock(builder.getRegistryName());
        return builder.withItem(itemBuilder);
    }

    private BlockBuilder parseItemBlock(JsonObject data, BlockBuilder builder)
    {
        ItemBuilder itemBuilder = ThingResourceManager.INSTANCE.itemParser.parseFromElement(builder.getRegistryName(), data).makeBlock(builder.getRegistryName());
        return builder.withItem(itemBuilder);
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
