package gigaherz.jsonthings;

import com.google.common.collect.Lists;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.client.BlockColorHandler;
import gigaherz.jsonthings.things.client.ItemColorHandler;
import gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.MultiLayerModel;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmllegacy.packs.ResourcePackLoader;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URL;
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
        bus.addGenericListener(Enchantment.class, this::registerEnchantments);

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> {
            ThingResourceManager thingPackManager = ThingResourceManager.INSTANCE;
            return new PackSelectionScreen(screen, thingPackManager.getRepository(),
                    rpl -> thingPackManager.onConfigScreenSave(), thingPackManager.getThingPacksLocation(),
                    new TextComponent("Thing Packs"));
        }));
    }

    private static CompletableFuture<ThingResourceManager> loader;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void construct(FMLConstructModEvent event)
    {
        event.enqueueWork(() -> {
            ResourcePackLoader.loadResourcePacks(ThingResourceManager.INSTANCE.getRepository(), ModResourcesFinder::buildPackFinder);

            loader = ThingResourceManager.init(Util.backgroundExecutor(), Runnable::run);

            if (FMLEnvironment.dist == Dist.CLIENT)
            {
                ClientHandlers.addClientPackFinder();
                BlockColorHandler.init();
                ItemColorHandler.init();
            }
        });
        URL url = null;
    }

    public void finishLoading(RegistryEvent.NewRegistry event)
    {
        try
        {
            loader.get();
            loader = null;

            // Foods
            ThingResourceManager.INSTANCE.foodParser.getBuilders().forEach(thing -> Registry.register(ThingRegistries.FOODS, thing.getRegistryName(), thing.build()));
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
        ThingResourceManager.INSTANCE.blockParser.getBuilders().forEach(thing -> registry.register(thing.build().self().setRegistryName(thing.getRegistryName())));
        LOGGER.info("Done processing thingpack Blocks.");
    }

    public void registerItems(RegistryEvent.Register<Item> event)
    {
        LOGGER.info("Started registering Item things, errors about unexpected registry domains are harmless...");
        IForgeRegistry<Item> registry = event.getRegistry();
        ThingResourceManager.INSTANCE.itemParser.getBuilders().forEach(thing -> registry.register(((Item) thing.build()).setRegistryName(thing.getRegistryName())));
        LOGGER.info("Done processing thingpack Items.");
    }

    public void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        LOGGER.info("Started registering Enchantment things, errors about unexpected registry domains are harmless...");
        IForgeRegistry<Enchantment> registry = event.getRegistry();
        ThingResourceManager.INSTANCE.enchantmentParser.getBuilders().forEach(thing -> registry.register((thing.build()).setRegistryName(thing.getRegistryName())));
        LOGGER.info("Done processing thingpack Enchantments.");
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
                if (layers.size() != 1 || !layers.contains("solid"))
                {
                    Set<RenderType> renderTypes = layers.stream().map(MultiLayerModel.Loader.BLOCK_LAYERS::get).collect(Collectors.toSet());
                    ;
                    ItemBlockRenderTypes.setRenderLayer(thing.getBuiltBlock().self(), renderTypes::contains);
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
                    BlockColor bc = BlockColorHandler.get(handlerName);
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
                    Function<BlockColors, ItemColor> handler = ItemColorHandler.get(handlerName);
                    ItemColor ic = handler.apply(event.getBlockColors());
                    event.getItemColors().register(ic, ((Item) thing.getBuiltItem()));
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
