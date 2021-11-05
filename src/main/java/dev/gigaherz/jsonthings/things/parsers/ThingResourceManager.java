package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.resources.*;
import net.minecraft.util.Unit;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ThingResourceManager
{
    private static ThingResourceManager instance = new ThingResourceManager();

    public static ThingResourceManager instance()
    {
        return instance;
    }

    private static final Set<String> disabledPacks = Sets.newHashSet();

    private final SimpleReloadableResourceManager resourceManager;
    private final IPackFinder folderPackFinder;
    private final ResourcePackList packList;

    private final List<ThingParser<?>> thingParsers = Lists.newArrayList();
    private final Map<String, ThingParser<?>> parsersMap = Maps.newHashMap();

    private ThingResourceManager()
    {
        resourceManager = new SimpleReloadableResourceManager(CustomPackType.THINGS);
        folderPackFinder = new FolderPackFinder(getThingPacksLocation(), IPackNameDecorator.DEFAULT);
        packList = new ResourcePackList(folderPackFinder);
    }

    public <TParser extends ThingParser<?>> TParser registerParser(TParser parser)
    {
        if (parsersMap.containsKey(parser.getThingType()))
            throw new IllegalStateException("There is already a parser registered for type " + parser.getThingType());
        thingParsers.add(parser);
        resourceManager.registerReloadListener(parser);
        parsersMap.put(parser.getThingType(), parser);
        return parser;
    }

    public IPackFinder getWrappedPackFinder()
    {
        return (infoConsumer, infoFactory) -> folderPackFinder.loadPacks(info -> {
            if (!disabledPacks.contains(info.getId()))
                infoConsumer.accept(info);
        }, (a, n, b, c, d, e, f) -> infoFactory.create("thingpack:" + a, true, b, c, d, e, f));
    }

    public File getThingPacksLocation()
    {
        File thingpacks = FMLPaths.GAMEDIR.get().resolve("thingpacks").toFile();
        if (!thingpacks.exists() && !thingpacks.mkdirs())
            throw new RuntimeException("Could not create thingspacks directory! Please create the directory yourself, or make sure the name is not taken by a file and you have permission to create directories.");
        return thingpacks;
    }

    /**
     * Call during mod construction **without enqueueWork**!
     */
    public synchronized void addPackFinder(IPackFinder finder)
    {
        packList.addPackFinder(finder);
    }

    /**
     * Call during mod construction **without enqueueWork**!
     */
    public synchronized void addResourceReloadListener(IFutureReloadListener listener)
    {
        resourceManager.registerReloadListener(listener);
    }


    public CompletableFuture<ThingResourceManager> beginLoading(Executor backgroundExecutor, Executor gameExecutor)
    {
        packList.reload();

        loadConfig();

        return resourceManager
                .reload(backgroundExecutor, gameExecutor, packList.openAllSelected(), CompletableFuture.completedFuture(Unit.INSTANCE))
                .whenComplete((unit, throwable) -> {
                    if (throwable != null)
                    {
                        resourceManager.close();
                    }
                })
                .thenApply((unit) -> this);
    }

    public void finishLoading()
    {
        thingParsers.forEach(ThingParser::finishLoading);
    }

    public ResourcePackList getRepository()
    {
        return packList;
    }

    public void onConfigScreenSave()
    {
        disabledPacks.clear();
        disabledPacks.addAll(packList.getAvailableIds());
        disabledPacks.removeAll(packList.getSelectedIds());
        saveConfig();
    }

    public void saveConfig()
    {
        JsonArray disabled = new JsonArray();
        disabledPacks.forEach(disabled::add);
        JsonArray order = new JsonArray();
        packList.getSelectedIds().forEach(order::add);
        JsonObject obj = new JsonObject();
        obj.add("disabled", disabled);
        obj.add("order", order);
        String json = (new Gson()).toJson(obj);
        try (FileOutputStream stream = new FileOutputStream(getConfigFile());
             Writer w = new OutputStreamWriter(stream, StandardCharsets.UTF_8))
        {
            w.write(json);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void loadConfig()
    {
        File configFile = getConfigFile();
        List<String> orderList = new ArrayList<>();
        if (configFile.exists())
        {
            try (FileInputStream stream = new FileInputStream(configFile);
                 Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8))
            {
                JsonObject obj = (new Gson()).fromJson(reader, JsonObject.class);
                JsonArray disabled = obj.get("disabled").getAsJsonArray();
                disabledPacks.clear();
                disabled.forEach(element -> disabledPacks.add(element.getAsString()));

                JsonArray order = obj.get("order").getAsJsonArray();
                order.forEach(element -> orderList.add(element.getAsString()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        for (String s : packList.getAvailableIds())
        {
            if (!orderList.contains(s) && !disabledPacks.contains(s))
                orderList.add(s);
        }

        packList.setSelected(orderList);
    }

    private File getConfigFile()
    {
        return FMLPaths.CONFIGDIR.get().resolve("jsonthings-thingpacks.json").toFile();
    }
}
