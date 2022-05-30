package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import dev.gigaherz.jsonthings.util.parse.value.ArrayValue;
import dev.gigaherz.jsonthings.util.parse.value.ObjValue;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Rarity;

import java.util.*;
import java.util.function.Consumer;

public abstract class ThingParser<TBuilder extends BaseBuilder<?>> extends SimpleJsonResourceReloadListener
{
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<ResourceLocation, TBuilder> buildersByName = Maps.newHashMap();
    private final List<TBuilder> builders = Lists.newArrayList();
    private final String thingType;
    private final Gson gson;

    public ThingParser(Gson gson, String thingType)
    {
        super(gson, thingType);
        this.gson = gson;
        this.thingType = thingType;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
    {
        objectIn.forEach(this::parseFromElement);
    }

    protected abstract TBuilder processThing(ResourceLocation key, JsonObject data, Consumer<TBuilder> builderModification);

    public TBuilder parseFromElement(ResourceLocation key, JsonElement json)
    {
        return parseFromElement(key, json, (b) -> {});
    }

    public TBuilder parseFromElement(ResourceLocation key, JsonElement json, Consumer<TBuilder> builderModification)
    {
        try
        {
            TBuilder builder = processThing(key, json.getAsJsonObject(), builderModification);
            buildersByName.put(key, builder);
            builders.add(builder);
            return builder;
        }
        catch (Exception e)
        {
            CrashReport crashReport = CrashReport.forThrowable(e, "Error while parsing " + thingType + " from " + key);

            CrashReportCategory reportCategory = crashReport.addCategory("Thing", 1);
            reportCategory.setDetail("Resource name", key);

            throw new ReportedException(crashReport);
        }
    }

    public List<TBuilder> getBuilders()
    {
        return Collections.unmodifiableList(builders);
    }

    public Map<ResourceLocation, TBuilder> getBuildersMap()
    {
        return Collections.unmodifiableMap(buildersByName);
    }

    protected StackContext parseStackContext(JsonObject item)
    {
        StackContext ctx = new StackContext(null);

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
                    nbt = TagParser.parseTag(gson.toJson(element));
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

    protected Map<String, List<ResourceLocation>> parseEvents(ObjValue objValue)
    {
        var map = new HashMap<String, List<ResourceLocation>>();

        objValue.forEach((str, any) -> {
            any
                    .ifString(val -> map.put(str, List.of(new ResourceLocation(val.getAsString()))))
                    .ifArray(arr -> map.put(str, arr.flatMap(f -> f.map(val -> new ResourceLocation(val.string().getAsString())).toList())))
                    .typeError();

        });

        return map;
    }

    protected ResourceLocation makeResourceLocation(ResourceLocation key, String name)
    {
        if (name.contains(":"))
            return new ResourceLocation(name);
        return new ResourceLocation(key.getNamespace(), name);
    }

    protected static Rarity parseRarity(String str)
    {
        Rarity rarity = rarities.get(str);
        if (rarity == null) throw new IllegalStateException("No item rarity known with name " + str);
        return rarity;
    }

    private static final Map<String, Rarity> rarities = ImmutableMap.<String, Rarity>builder()
            .put("common", Rarity.COMMON)
            .put("uncommon", Rarity.UNCOMMON)
            .put("rare", Rarity.RARE)
            .put("epic", Rarity.EPIC)
            .build();

    public final void finishLoading()
    {
        finishLoadingInternal();
    }

    protected void finishLoadingInternal()
    {
    }

    public String getThingType()
    {
        return thingType;
    }

    public TBuilder getOrCrash(ResourceLocation name)
    {
        TBuilder b = buildersByName.get(name);
        if (b == null)
            throw new RuntimeException("There is no known " + thingType + " with name " + name);
        return b;
    }

    public static int parseColor(String color)
    {
        if (color.startsWith("#"))
        {
            color = color.toUpperCase(Locale.ROOT).substring(1);

            if (color.length() == 8)
            {
                return (int)Long.parseLong(color, 16);
            }
            else if(color.length() == 6)
            {
                return 0xFF | Integer.parseInt(color, 16);
            }
            else
            {
                throw new RuntimeException("Color hex string must be either 6 or 8 digits long.");
            }
        }
        return (int)Long.parseLong(color);
    }

    public static int parseColor(ObjValue color)
    {
        int[] values = new int[4];

        values[0] = 0xFF;

        color
                .ifKey("a", any -> any.intValue().handle(i -> values[0]=i))
                .ifKey("r", any -> any.intValue().handle(i -> values[1]=i))
                .ifKey("g", any -> any.intValue().handle(i -> values[2]=i))
                .ifKey("b", any -> any.intValue().handle(i -> values[3]=i));

        return (values[0] << 24) | (values[1] << 16) | (values[2] << 8) | (values[3]);
    }

    public static int parseColor(ArrayValue color)
    {
        int[] values = new int[4];

        color.between(3,4).raw(arr -> {
            int i=0;
            values[0] = arr.size() == 4 ? arr.get(i++).getAsInt() : 0xFF;
            values[1] = arr.get(i++).getAsInt();
            values[2] = arr.get(i++).getAsInt();
            values[3] = arr.get(i).getAsInt();
        });

        return (values[0] << 24) | (values[1] << 16) | (values[2] << 8) | (values[3]);
    }

    public static Set<String> parseRenderLayers(Any data)
    {
        Set<String> types = Sets.newHashSet();
        data.ifString(str -> str.handle(name -> types.add(verifyRenderLayer(name))))
                .ifArray(arr -> arr.forEach((i, val) -> types.add(verifyRenderLayer(val.string().getAsString()))))
                .typeError();
        return types;
    }

    private static final Set<String> VALID_BLOCK_LAYERS = Sets.newHashSet("solid", "cutout_mipped", "cutout", "translucent", "tripwire");

    private static String verifyRenderLayer(String layerName)
    {
        if (!VALID_BLOCK_LAYERS.contains(layerName))
            throw new IllegalStateException("Render layer " + layerName + " is not a valid block chunk layer.");
        return layerName;
    }

}
