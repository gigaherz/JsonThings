package gigaherz.jsonthings;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.builders.BlockBuilder;
import gigaherz.jsonthings.things.builders.ItemBuilder;
import gigaherz.jsonthings.things.client.BlockColorHandler;
import gigaherz.jsonthings.things.client.ItemColorHandler;
import gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.MultiLayerModel;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(JsonThings.MODID)
public class JsonThings
{
    public static final String MODID = "jsonthings";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    static
    {
        ThingResourceManager.staticInit();
        ThingRegistries.staticInit();
    }

    public JsonThings()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::finishLoading);
        bus.addGenericListener(Block.class, this::registerBlocks);
        bus.addGenericListener(Item.class, this::registerItems);

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> {
            ThingResourceManager thingPackManager = ThingResourceManager.INSTANCE;
            return new PackScreen(screen, thingPackManager.getResourcePackList(),
                    rpl -> thingPackManager.onConfigScreenSave(), thingPackManager.getThingPacksLocation(),
                    new StringTextComponent("Thing Packs"));
        });
    }

    private static CompletableFuture<ThingResourceManager> loader;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void construct(FMLConstructModEvent event)
    {
        event.enqueueWork(() -> {
            ResourcePackLoader.loadResourcePacks(ThingResourceManager.INSTANCE.getResourcePackList(), ModResourcesFinder::buildPackFinder);

            loader = ThingResourceManager.init(Util.backgroundExecutor(), Runnable::run);

            if (FMLEnvironment.dist == Dist.CLIENT)
            {
                ClientHandlers.addClientPackFinder();
                BlockColorHandler.init();
                ItemColorHandler.init();
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
        IForgeRegistry<Block> registry = event.getRegistry();
        ThingResourceManager.INSTANCE.blockParser.getBuilders().stream().map(BlockBuilder::build).forEach(thing -> registry.register(thing.self()));
        LOGGER.info("Done processing thingpack Blocks.");
    }

    public void registerItems(RegistryEvent.Register<Item> event)
    {
        LOGGER.info("Started registering Item things, errors about unexpected registry domains are harmless...");
        IForgeRegistry<Item> registry = event.getRegistry();
        ThingResourceManager.INSTANCE.itemParser.getBuilders().stream().map(ItemBuilder::build).forEach(thing -> registry.register(thing.self()));
        LOGGER.info("Done processing thingpack Items.");
    }

    @Mod.EventBusSubscriber(modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientHandlers
    {
        public static void addClientPackFinder()
        {
            Minecraft.getInstance().getResourcePackRepository().addPackFinder(ThingResourceManager.INSTANCE.getWrappedPackFinder());
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            ThingResourceManager.INSTANCE.blockParser.getBuilders().stream().forEach(thing -> {
                Set<String> layers = thing.getRenderLayersOrDefault();
                if (layers != null && (layers.size() != 1 || layers.contains("solid")))
                {
                    Set<RenderType> renderTypes = layers.stream().map(MultiLayerModel.Loader.BLOCK_LAYERS::get).collect(Collectors.toSet());;
                    RenderTypeLookup.setRenderLayer(thing.getBuiltBlock().self(), renderTypes::contains);
                }
            });
        }

        @SubscribeEvent
        public static void itemColorHandlers(ColorHandlerEvent.Block event)
        {
            ThingResourceManager.INSTANCE.blockParser.getBuilders().forEach(thing -> {
                String handlerName = thing.getColorHandler();
                if (handlerName != null)
                {
                    IBlockColor bc = BlockColorHandler.get(handlerName);
                    event.getBlockColors().register(bc, thing.getBuiltBlock().self());
                }
            });
        }

        @SubscribeEvent
        public static void itemColorHandlers(ColorHandlerEvent.Item event)
        {
            ThingResourceManager.INSTANCE.itemParser.getBuilders().forEach(thing -> {
                String handlerName = thing.getColorHandler();
                if (handlerName != null)
                {
                    Function<BlockColors, IItemColor> handler = ItemColorHandler.get(handlerName);
                    IItemColor ic = handler.apply(event.getBlockColors());
                    event.getItemColors().register(ic, thing.getBuiltItem().self());
                }
            });
        }
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
