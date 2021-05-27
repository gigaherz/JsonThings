package gigaherz.jsonthings.parser;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.client.resources.JsonReloadListener;
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
}
