package dev.gigaherz.jsonthings;

import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.client.BlockColorHandler;
import dev.gigaherz.jsonthings.things.client.ItemColorHandler;
import dev.gigaherz.jsonthings.things.parsers.*;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(JsonThings.MODID)
public class JsonThings
{
    public static final String MODID = "jsonthings";
    public static final Logger LOGGER = LogManager.getLogger();

    static
    {
        ThingRegistries.staticInit();
    }

    public static BlockParser blockParser;
    public static ItemParser itemParser;
    public static EnchantmentParser enchantmentParser;
    public static FoodParser foodParser;
    public static ShapeParser shapeParser;
    public static TierParser tierParser;
    public static BlockMaterialParser blockMaterialParser;
    public static ArmorMaterialParser armorMaterialParser;
    public static CreativeModeTabParser creativeModeTabParser;

    public JsonThings()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ThingResourceManager manager = ThingResourceManager.instance();
        blockParser = manager.registerParser(new BlockParser(bus));
        itemParser = manager.registerParser(new ItemParser(bus));
        enchantmentParser = manager.registerParser(new EnchantmentParser(bus));
        foodParser = manager.registerParser(new FoodParser());
        shapeParser = manager.registerParser(new ShapeParser());
        tierParser = manager.registerParser(new TierParser());
        blockMaterialParser = manager.registerParser(new BlockMaterialParser());
        armorMaterialParser = manager.registerParser(new ArmorMaterialParser());
        creativeModeTabParser = manager.registerParser(new CreativeModeTabParser());
    }

    private static CompletableFuture<ThingResourceManager> loaderFuture;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void construct(FMLConstructModEvent event)
    {
        event.enqueueWork(() -> {
            ThingResourceManager instance = ThingResourceManager.instance();

            ResourcePackLoader.loadResourcePacks(instance.getRepository(), ModResourcesFinder::buildPackFinder);

            loaderFuture = instance.beginLoading(Util.backgroundExecutor(), Runnable::run);

            if (FMLEnvironment.dist == Dist.CLIENT)
            {
                BlockColorHandler.init();
                ItemColorHandler.init();
            }
        });
    }

    @SubscribeEvent
    public static void finishLoading(RegistryEvent.NewRegistry event)
    {
        try
        {
            loaderFuture.get().finishLoading();
            loaderFuture = null;
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException("Error loading thingpacks", e);
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientHandlers
    {
        @SubscribeEvent
        public static void constructMod(FMLConstructModEvent event)
        {
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> {
                ThingResourceManager thingPackManager = ThingResourceManager.instance();
                return new PackScreen(screen, thingPackManager.getRepository(),
                        rpl -> thingPackManager.onConfigScreenSave(), thingPackManager.getThingPacksLocation(),
                        new StringTextComponent("Thing Packs"));
            });
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            JsonThings.blockParser.getBuilders().forEach(thing -> {
                Set<String> layers = thing.getRenderLayers();
                if (layers.size() != 1 || !layers.contains("solid"))
                {
                    Set<RenderType> renderTypes = layers.stream().map(MultiLayerModel.Loader.BLOCK_LAYERS::get).collect(Collectors.toSet());
                    RenderTypeLookup.setRenderLayer(thing.get().self(), renderTypes::contains);
                }
            });
        }

        @SubscribeEvent
        public static void itemColorHandlers(ColorHandlerEvent.Block event)
        {
            JsonThings.blockParser.getBuilders().forEach(thing -> {
                String handlerName = thing.getColorHandler();
                if (handlerName != null)
                {
                    IBlockColor bc = BlockColorHandler.get(handlerName);
                    event.getBlockColors().register(bc, thing.get().self());
                }
            });
        }

        @SubscribeEvent
        public static void itemColorHandlers(ColorHandlerEvent.Item event)
        {
            JsonThings.itemParser.getBuilders().forEach(thing -> {
                String handlerName = thing.getColorHandler();
                if (handlerName != null)
                {
                    Function<BlockColors, IItemColor> handler = ItemColorHandler.get(handlerName);
                    IItemColor ic = handler.apply(event.getBlockColors());
                    event.getItemColors().register(ic, ((Item) thing.get()));
                }
            });
        }
    }
}
