package dev.gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import dev.gigaherz.jsonthings.RunnableQueue;
import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.*;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ThingResourceManager
{
    private static class InstanceHolder
    {
        private static final ThingResourceManager instance = new ThingResourceManager();
    }

    public static final Logger LOGGER = LogUtils.getLogger();

    public static ThingResourceManager instance()
    {
        return InstanceHolder.instance;
    }

    private static final Set<String> disabledPacks = Sets.newHashSet();

    private RunnableQueue mainThreadExecutor;

    private final ReloadableResourceManager resourceManager;
    private final RepositorySource folderPackFinder;
    private final PackRepository packList;

    private final List<ThingParser<?, ?>> thingParsers = Lists.newArrayList();
    private final Map<String, ThingParser<?, ?>> parsersMap = Maps.newHashMap();

    private ThingResourceManager()
    {
        resourceManager = new ReloadableResourceManager(CustomPackType.THINGS);
        folderPackFinder = new FolderRepositorySource(getThingPacksLocation(), CustomPackType.THINGS, PackSource.DEFAULT, LevelStorageSource.parseValidator(FMLPaths.GAMEDIR.get().resolve("allowed_symlinks.txt")));
        packList = new PackRepository(folderPackFinder);
    }

    public synchronized <TParser extends ThingParser<?, ?>> TParser registerParser(TParser parser)
    {
        if (parsersMap.containsKey(parser.getThingType()))
            throw new IllegalStateException("There is already a parser registered for type " + parser.getThingType());
        thingParsers.add(parser);
        //noinspection deprecation
        resourceManager.registerReloadListener(parser);
        parsersMap.put(parser.getThingType(), parser);
        return parser;
    }

    public RepositorySource getWrappedPackFinder(PackType packType)
    {
        return (infoConsumer) -> folderPackFinder.loadPacks(pack -> {
            if (!disabledPacks.contains(pack.getId()))
            {
                var location = new PackLocationInfo(
                        "thingpack:" + pack.location.id(),
                        Component.translatable("text.jsonthings.wrappedpack." + packType.getSerializedName() + ".prefix").append(pack.location.title()),
                        pack.location.source(),
                        pack.location.knownPackInfo()
                );
                var metadata = new Pack.Metadata(
                        Component.translatable("text.jsonthings.wrappedpack.description"),
                        pack.metadata.compatibility(),
                        pack.metadata.requestedFeatures(),
                        pack.metadata.overlays(),
                        pack.metadata.isHidden()
                );
                var selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);
                infoConsumer.accept(new Pack(location, pack.resources, metadata, selectionConfig));
            }
        });
    }

    public Path getThingPacksLocation()
    {
        Path thingpacks = FMLPaths.GAMEDIR.get().resolve("thingpacks");
        if (!Files.exists(thingpacks) && !thingpacks.toFile().mkdirs())
            throw new ThingParseException("Could not create thingspacks directory! Please create the directory yourself, or make sure the name is not taken by a file and you have permission to create directories.");
        return thingpacks;
    }

    /**
     * Call during mod construction **without enqueueWork**!
     */
    public synchronized void addPackFinder(RepositorySource finder)
    {
        packList.addPackFinder(finder);
    }

    /**
     * Call during mod construction **without enqueueWork**!
     */
    public synchronized void addResourceReloadListener(PreparableReloadListener listener)
    {
        resourceManager.registerReloadListener(listener);
    }

    private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);

    public CompletableFuture<ThingResourceManager> beginLoading()
    {
        packList.reload();

        loadConfig();

        mainThreadExecutor = new RunnableQueue();

        return resourceManager
                .createReload(Util.backgroundExecutor(), mainThreadExecutor, RESOURCE_RELOAD_INITIAL_TASK, packList.openAllSelected())
                .done()
                .whenComplete((unit, throwable) -> {
                    if (throwable != null)
                    {
                        resourceManager.close();
                    }
                })
                .thenRun(mainThreadExecutor::finish)
                .thenApply((unit) -> this);
    }

    public void waitForLoading(CompletableFuture<ThingResourceManager> loaderFuture)
    {
        try
        {
            //DSLHelpers.debugDumpBindings();

            while (!loaderFuture.isDone())
            {
                if (!mainThreadExecutor.runQueue())
                    mainThreadExecutor.waitForTasks();
            }

            mainThreadExecutor.runQueue();

            loaderFuture.get().finishLoading();
        }
        catch (InterruptedException e)
        {
            LOGGER.error("Thingpack loader future interrupted!");
        }
        catch (ExecutionException e)
        {
            Throwable pCause = e.getCause();
            throw new ReportedException(CrashReport.forThrowable(pCause, "Error loading thingpacks"));
        }
    }

    public void finishLoading()
    {
        thingParsers.forEach(ThingParser::finishLoading);
    }

    public void validateAll()
    {
        thingParsers.forEach(ThingParser::validateBuilders);
    }

    public PackRepository getRepository()
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
