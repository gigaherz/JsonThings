package gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.StackContext;
import joptsimple.internal.Strings;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class ThingParser<TBuilder> extends JsonReloadListener
{
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final List<TBuilder> builders = Lists.newArrayList();

    public ThingParser(Gson gson, String thingType)
    {
        super(gson, thingType);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        objectIn.forEach(this::parseFromElement);
    }

    protected abstract TBuilder processThing(ResourceLocation key, JsonObject data);

    public TBuilder parseFromElement(ResourceLocation key, JsonElement json)
    {
        TBuilder builder = processThing(key, json.getAsJsonObject());
        builders.add(builder);
        return builder;
    }

    public List<TBuilder> getBuilders()
    {
        return Collections.unmodifiableList(builders);
    }

    protected StackContext parseStackContext(ResourceLocation key, JsonObject item)
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

    protected ResourceLocation makeResourceLocation(ResourceLocation key, String name)
    {
        if (name.contains(":"))
            return new ResourceLocation(name);
        return new ResourceLocation(key.getNamespace(), name);
    }
}
