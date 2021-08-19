package gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.StackContext;
import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class ThingParser<TBuilder> extends SimpleJsonResourceReloadListener
{
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<ResourceLocation, TBuilder> buildersByName = Maps.newHashMap();
    private final List<TBuilder> builders = Lists.newArrayList();
    private final String thingType;

    public ThingParser(Gson gson, String thingType)
    {
        super(gson, thingType);
        this.thingType = thingType;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
    {
        objectIn.forEach(this::parseFromElement);
    }

    protected abstract TBuilder processThing(ResourceLocation key, JsonObject data);

    public TBuilder parseFromElement(ResourceLocation key, JsonElement json)
    {
        TBuilder builder = processThing(key, json.getAsJsonObject());
        buildersByName.put(key, builder);
        builders.add(builder);
        return builder;
    }

    public List<TBuilder> getBuilders()
    {
        return Collections.unmodifiableList(builders);
    }

    public Map<ResourceLocation, TBuilder> getBuildersMap()
    {
        return Collections.unmodifiableMap(buildersByName);
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
                CompoundTag nbt;
                if (element.isJsonObject())
                    nbt = TagParser.parseTag(GSON.toJson(element));
                else
                    nbt = TagParser.parseTag(element.getAsString());
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

    public void finishLoading()
    {
    }

    public String getThingType()
    {
        return thingType;
    }
}
