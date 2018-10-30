package gigaherz.jsonthings.parser;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.block.builder.BlockBuilder;
import gigaherz.jsonthings.item.builder.AttributeModifierOperation;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.item.builder.StackContext;
import joptsimple.internal.Strings;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.UUID;

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
        return "item";
    }

    @Override
    public BlockBuilder processThing(ResourceLocation key, JsonObject data)
    {
        BlockBuilder builder = BlockBuilder.begin(key);

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

        if (data.has("item"))
        {
            builder = parseItemBlock(data.get("item").getAsJsonObject(), builder);
        }

        BUILDERS.add(builder);
        return builder;
    }

    private BlockBuilder parseItemBlock(JsonObject data, BlockBuilder builder)
    {
        ItemParser.INSTANCE.processThing(builder.getRegistryName(), data);
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

        if (item.has("nbt"))
        {
            try
            {
                JsonElement element = item.get("nbt");
                NBTTagCompound nbt;
                if (element.isJsonObject())
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
