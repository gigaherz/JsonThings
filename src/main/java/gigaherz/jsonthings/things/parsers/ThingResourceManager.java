package gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.*;
import net.minecraft.util.Unit;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ThingResourceManager
{
    private static final Method M_CREATE = ObfuscationReflectionHelper.findMethod(ResourcePackType.class, "create", String.class, String.class);
    private static final ResourcePackType PACK_TYPE_THINGS;

    static
    {
        try
        {
            PACK_TYPE_THINGS = (ResourcePackType) M_CREATE.invoke(null, "JSONTHINGS_THINGS", "things");
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException("Error calling private method", e);
        }
    }

    public static void staticInit()
    {
        /* do nothing */
    }

    public static final ThingResourceManager INSTANCE = new ThingResourceManager();

    private final IReloadableResourceManager resourceManager;
    private final IPackFinder folderPackFinder;
    private final ResourcePackList packList;
    public final BlockParser blockParser = new BlockParser();
    public final ItemParser itemParser = new ItemParser();

    public ThingResourceManager()
    {
        resourceManager = new SimpleReloadableResourceManager(PACK_TYPE_THINGS);
        folderPackFinder = new FolderPackFinder(getThingPacksLocation(), IPackNameDecorator.DEFAULT);
        packList = new ResourcePackList(folderPackFinder);
        resourceManager.registerReloadListener(blockParser);
        resourceManager.registerReloadListener(itemParser);
    }

    public IPackFinder getWrappedPackFinder()
    {
        return (infoConsumer, infoFactory) -> folderPackFinder.loadPacks(info -> {
            if (!disabledPacks.contains(info.getId()))
                infoConsumer.accept(info);
        }, (a, b, c, d, e, f, g) ->
                infoFactory.create("thingpack:" + a, true, c, d, e, f, g));
    }

    public File getThingPacksLocation()
    {
        return FMLPaths.GAMEDIR.get().resolve("thingpacks").toFile();
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

    private static final Set<String> disabledPacks = Sets.newHashSet();

    private static final CompletableFuture<Unit> COMPLETED_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);

    public static CompletableFuture<ThingResourceManager> init(Executor backgroundExecutor, Executor gameExecutor)
    {
        INSTANCE.packList.reload();

        INSTANCE.load();

        CompletableFuture<Unit> completablefuture = INSTANCE.resourceManager.reload(backgroundExecutor, gameExecutor, INSTANCE.packList.openAllSelected(), COMPLETED_FUTURE);
        return completablefuture.whenComplete((unit, throwable) -> {
            if (throwable != null)
            {
                INSTANCE.resourceManager.close();
            }
        }).thenApply((unit) -> INSTANCE);
    }

    public ResourcePackList getResourcePackList()
    {
        return packList;
    }

    public void onConfigScreenSave()
    {
        disabledPacks.clear();
        disabledPacks.addAll(packList.getAvailableIds());
        disabledPacks.removeAll(packList.getSelectedIds());
        save();
    }

    public void save()
    {
        JsonArray disabled = new JsonArray();
        disabledPacks.forEach(disabled::add);
        JsonArray order = new JsonArray();
        packList.getSelectedIds().forEach(order::add);
        JsonObject obj = new JsonObject();
        obj.add("disabled", disabled);
        obj.add("order", order);
        String json = (new Gson()).toJson(obj);
        try(FileOutputStream stream = new FileOutputStream(getConfigFile());
                Writer w = new OutputStreamWriter(stream, StandardCharsets.UTF_8))
        {
            w.write(json);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void load()
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

        for(String s : packList.getAvailableIds())
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
