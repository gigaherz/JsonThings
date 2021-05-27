package gigaherz.jsonthings;

import com.google.common.collect.Lists;
import gigaherz.jsonthings.block.builder.BlockBuilder;
import gigaherz.jsonthings.client.ClientThingResources;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.microregistries.ThingsByName;
import gigaherz.jsonthings.parser.ThingResourceManager;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid=JsonThings.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
@Mod(JsonThings.MODID)
public class JsonThings
{
    public static final String MODID = "jsonthings";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    static {
        ThingResourceManager.staticInit();
        ThingsByName.initVanillaThings();
    }

    public JsonThings()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::finishLoading);
        bus.addGenericListener(Block.class, this::registerBlocks);
        bus.addGenericListener(Item.class, this::registerItems);
    }

    private static CompletableFuture<ThingResourceManager> loader;

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void construct(FMLConstructModEvent event)
    {
        event.enqueueWork(() -> {
            ResourcePackLoader.loadResourcePacks(ThingResourceManager.INSTANCE.getResourcePackList(), ModPackFinder::buildPackFinder);

            loader = ThingResourceManager.init(Util.getServerExecutor(), Runnable::run);

            if (FMLEnvironment.dist == Dist.CLIENT)
            {
                ClientThingResources.addClientPackFinder();
            }
        });
    }

    public void finishLoading(RegistryEvent.NewRegistry event)
    {
        try
        {
            loader.get();
            loader = null;
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException("Error loading thingpacks", e);
        }
    }

    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        LOGGER.info("Started registering Block things, errors about unexpected registry domains are harmless...");
        ThingResourceManager.INSTANCE.blockParser.getBuilders().stream().map(BlockBuilder::build).forEach(event.getRegistry()::register);
        LOGGER.info("Done processing thingpack Blocks.");
    }

    public void registerItems(RegistryEvent.Register<Item> event)
    {
        LOGGER.info("Started registering Item things, errors about unexpected registry domains are harmless...");
        ThingResourceManager.INSTANCE.itemParser.getBuilders().stream().map(ItemBuilder::build).forEach(event.getRegistry()::register);
        LOGGER.info("Done processing thingpack Items.");
    }

    private static class ListenerHandler<T>
    {
        final List<Reference<? extends T>> listeners = Lists.newArrayList();
        final ReferenceQueue<T> deadListeners = new ReferenceQueue<>();

        public void addListener(T listener)
        {
            //cleanup();
            listeners.add(new WeakReference<>(listener, deadListeners));
        }

        public void removeListener(T listener)
        {
            cleanup();

            listeners.removeIf(ref -> ref.get() == listener);
        }

        public void forEach(Consumer<T> consumer)
        {

            cleanup();
            listeners.forEach((w) -> consumer.accept(w.get()));
        }

        private void cleanup()
        {
            for (Reference<? extends T>
                 ref = deadListeners.poll();
                 ref != null;
                 ref = deadListeners.poll())
            {
                listeners.remove(ref);
            }
        }
    }
}
