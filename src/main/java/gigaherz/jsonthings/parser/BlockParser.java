package gigaherz.jsonthings.parser;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.block.builder.BlockBuilder;
import gigaherz.jsonthings.item.builder.StackContext;
import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BlockParser extends ThingParser<BlockBuilder>
{
    public static final List<BlockBuilder> BUILDERS = Lists.newArrayList();
    public static final BlockParser INSTANCE = new BlockParser();

    public static void init()
    {
        INSTANCE.parse();
    }

    @Override
    public String getThingType()
    {
        return "block";
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
                throw new RuntimeException("If present, 'item' must be a boolean or an object.");
            }
        }

        BUILDERS.add(builder);
        return builder;
    }

    private BlockBuilder createStockItemBlock(BlockBuilder builder)
    {
        ItemParser.INSTANCE.processThing(builder.getRegistryName(), new JsonObject()).makeBlock(builder.getRegistryName());
        return builder;
    }

    private BlockBuilder parseItemBlock(JsonObject data, BlockBuilder builder)
    {
        ItemParser.INSTANCE.processThing(builder.getRegistryName(), data).makeBlock(builder.getRegistryName());
        return builder;
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
