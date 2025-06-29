package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.util.KeyNotFoundException;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import dev.gigaherz.jsonthings.util.parse.value.ArrayValue;
import dev.gigaherz.jsonthings.util.parse.value.ObjValue;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.*;

public abstract class ThingParser<TThing, TBuilder extends BaseBuilder<TThing, TBuilder>> extends SimpleJsonResourceReloadListener<JsonElement>
{
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<ResourceLocation, ThingCondition> CONDITIONS_REGISTRY = new HashMap<>();

    public synchronized static void registerCondition(ResourceLocation id, ThingCondition condition)
    {
        CONDITIONS_REGISTRY.put(id, condition);
    }

    public static boolean parseAndTestConditions(String thingType, ResourceLocation thingId, JsonElement json)
    {
        var conditions = json.getAsJsonObject().get("conditions");
        if (conditions == null)
            return true;
        var conditionArray = conditions.getAsJsonArray();
        for(var e : conditionArray)
        {
            if (!parseAndTestCondition(thingType, thingId, e.getAsJsonObject()))
                return false;
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean parseAndTestCondition(String thingType, ResourceLocation thingId, JsonObject condition)
    {
        var type = ResourceLocation.parse(condition.get("type").getAsString());

        var conditionHandler = CONDITIONS_REGISTRY.get(type);

        if (conditionHandler == null)
            throw new JsonParseException("Unknown condition type for id '" + type + "' while parsing " + thingId);

        return conditionHandler.test(thingType, thingId, condition);
    }

    static {
        registerCondition(ResourceLocation.parse("mod_loaded"), (type, id, data) -> ModList.get().isLoaded(data.get("modid").getAsString()));
        registerCondition(ResourceLocation.parse("not"), (type, id, data) -> !parseAndTestCondition(type, id, data.get("condition").getAsJsonObject()));
    }

    public static <T> void processAndConsumeErrors(String thingType, Iterable<T> list, Consumer<T> consumer, Function<T, ResourceLocation> keyGetter)
    {
        list.forEach((thing) -> {
            processAndConsumeErrors(thingType, () -> consumer.accept(thing), () -> keyGetter.apply(thing));
        });
    }

    public static <K, V> void processAndConsumeErrors(String thingType, Map<K, V> list, BiConsumer<K, V> consumer, Function<K, ResourceLocation> keyGetter)
    {
        list.forEach((key, value) -> {
            processAndConsumeErrors(thingType, () -> consumer.accept(key, value), () -> keyGetter.apply(key));
        });
    }

    public static void processAndConsumeErrors(String thingType, Runnable r, Supplier<ResourceLocation> keyGetter)
    {
        try
        {
            r.run();
        }
        catch (JsonParseException | KeyNotFoundException | ThingParseException | IllegalStateException | NullPointerException e)
        {
            processParseException(thingType, keyGetter.get(), e);
        }
    }

    public static void processParseException(String thingType, ResourceLocation key, Throwable e)
    {
        var message = String.format("[Json Things] Error parsing %s with id '%s': %s", thingType, key,  e.getMessage());
        LOGGER.error(message);
        LOGGER.debug("Details for message above", e);
        ModLoader.addLoadingIssue(new ModLoadingIssue(ModLoadingIssue.Severity.WARNING, message, List.of()));
    }

    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<ResourceLocation, @NotNull TBuilder> buildersByName = Maps.newHashMap();
    private final List<TBuilder> builders = Lists.newArrayList();
    private final String thingType;
    private final Gson gson;
    private final RegistryAccess registryAccess;

    public ThingParser(Gson gson, String thingType)
    {
        super(ExtraCodecs.JSON, FileToIdConverter.json(thingType));
        this.gson = gson;
        this.thingType = thingType;
        this.registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }

    public final RegistryAccess registryAccess()
    {
        return registryAccess;
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profilerIn)
    {
        processAndConsumeErrors(thingType, objectIn, (key, json) -> {
            var builder = parseFromElement(key, json);
            if (builder != null)
                buildersByName.put(key, builder);
        }, Function.identity());
    }

    protected abstract TBuilder processThing(ResourceLocation key, JsonObject data, Consumer<TBuilder> builderModification);

    @Nullable
    public TBuilder parseFromElement(ResourceLocation key, JsonElement json)
    {
        return parseFromElement(key, json, (b) -> {
        });
    }

    @Nullable
    public TBuilder parseFromElement(ResourceLocation thingId, JsonElement json, Consumer<TBuilder> builderModification)
    {
        if (!parseAndTestConditions(thingType, thingId, json)) return null;

        TBuilder builder = processThing(thingId, json.getAsJsonObject(), builderModification);
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

    protected StackContext parseStackContext(JsonObject item, boolean allowItem, boolean requireItem)
    {
        ResourceLocation itemName = null;
        if (item.has("item"))
        {
            if (!allowItem)
                throw new JsonParseException("'item' key provided in a context that doesn't allow customizing the item.");
            itemName = ResourceLocation.parse(item.get("item").getAsString());
        }
        else
        {
            if (requireItem)
                throw new JsonParseException("'item' key missing in a context that requires the item.");
        }

        StackContext ctx = new StackContext(itemName);

        if (item.has("count"))
        {
            int meta = item.get("count").getAsInt();
            ctx = ctx.withCount(meta);
        }

        if (item.has("components"))
        {
            try
            {
                JsonElement element = item.getAsJsonObject("components");
                var componentMap = DataComponentMap.CODEC.decode(JsonOps.INSTANCE, element).result().orElseThrow().getFirst();
                ctx = ctx.withComponents(componentMap);
            }
            catch (Exception e)
            {
                throw new ThingParseException("Failed to parse NBT json.", e);
            }
        }

        return ctx;
    }

    protected Map<String, List<ResourceLocation>> parseEvents(ObjValue objValue)
    {
        var map = new HashMap<String, List<ResourceLocation>>();

        objValue.forEach((str, any) -> {
            any
                    .ifString(val -> map.put(str, List.of(ResourceLocation.parse(val.getAsString()))))
                    .ifArray(arr -> map.put(str, arr.flatMap(f -> f.map(val -> ResourceLocation.parse(val.string().getAsString())).toList())))
                    .typeError();
        });

        return map;
    }

    protected static Rarity parseRarity(String str)
    {
        Rarity rarity = rarities.get(str);
        if (rarity == null) throw new ThingParseException("No item rarity known with name " + str);
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
            throw new ThingParseException("There is no known " + thingType + " with name " + name);
        return b;
    }

    public static int parseColor(String color)
    {
        if (color.startsWith("#"))
        {
            color = color.toUpperCase(Locale.ROOT).substring(1);

            if (color.length() == 8)
            {
                return (int) Long.parseLong(color, 16);
            }
            else if (color.length() == 6)
            {
                return 0xFF000000 | Integer.parseInt(color, 16);
            }
            else
            {
                throw new ThingParseException("Color hex string must be either 6 or 8 digits long.");
            }
        }
        return (int) Long.parseLong(color);
    }

    public static int parseColor(ObjValue color)
    {
        int[] values = new int[4];

        values[0] = 0xFF;

        color
                .ifKey("a", any -> any.intValue().handle(i -> values[0] = i))
                .ifKey("r", any -> any.intValue().handle(i -> values[1] = i))
                .ifKey("g", any -> any.intValue().handle(i -> values[2] = i))
                .ifKey("b", any -> any.intValue().handle(i -> values[3] = i));

        return (values[0] << 24) | (values[1] << 16) | (values[2] << 8) | (values[3]);
    }

    public static int parseColor(ArrayValue color)
    {
        int[] values = new int[4];

        color.between(3, 4).raw(arr -> {
            int i = 0;
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
            throw new ThingParseException("Render layer " + layerName + " is not a valid block chunk layer.");
        return layerName;
    }

    protected void register(IEventBus modEventBus, ResourceKey<Registry<TThing>> registryKey)
    {
        register(modEventBus, registryKey, (thing, obj) -> obj);
    }

    protected <TReg> void register(IEventBus modEventBus, ResourceKey<Registry<TReg>> registryKey, Function<TThing, TReg> converter)
    {
        register(modEventBus, registryKey, (builder, thing) -> converter.apply(thing));
    }

    protected <TReg> void register(IEventBus modEventBus, ResourceKey<Registry<TReg>> registryKey, BiFunction<TBuilder, TThing, TReg> converter)
    {
        modEventBus.addListener(RegisterEvent.class, event -> {
            event.register(registryKey, helper -> {
                processAndConsumeErrors(getThingType(), getBuilders(), thing -> helper.register(thing.getRegistryName(), converter.apply(thing, thing.get())), BaseBuilder::getRegistryName);
            });
        });
    }

    public void validateBuilders()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), BaseBuilder::validate, BaseBuilder::getRegistryName);
    }
}
