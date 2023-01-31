package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.client.BlockColorHandler;
import dev.gigaherz.jsonthings.things.client.ItemColorHandler;
import dev.gigaherz.jsonthings.things.parsers.*;
import dev.gigaherz.jsonthings.things.scripting.ScriptParser;
import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.resource.ResourcePackLoader;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(JsonThings.MODID)
public class JsonThings
{
    public static final String MODID = "jsonthings";
    public static final Logger LOGGER = LogUtils.getLogger();

    static
    {
        ThingRegistries.staticInit();
    }

    public static BlockParser blockParser;
    public static ItemParser itemParser;
    public static FluidParser fluidParser;
    public static EnchantmentParser enchantmentParser;
    public static FoodParser foodParser;
    public static ShapeParser shapeParser;
    public static TierParser tierParser;
    public static FluidTypeParser fluidTypeParser;
    public static BlockMaterialParser blockMaterialParser;
    public static ArmorMaterialParser armorMaterialParser;
    public static CreativeModeTabParser creativeModeTabParser;
    public static MobEffectInstanceParser mobEffectInstanceParser;

    public JsonThings()
    {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        var manager = ThingResourceManager.initialize(bus);
        if (ModList.get().isLoaded("rhino"))
        {
            ScriptParser.enable(manager);
        }
        blockParser = manager.registerParser(new BlockParser(bus));
        itemParser = manager.registerParser(new ItemParser(bus));
        fluidParser = manager.registerParser(new FluidParser(bus));
        enchantmentParser = manager.registerParser(new EnchantmentParser(bus));
        foodParser = manager.registerParser(new FoodParser());
        shapeParser = manager.registerParser(new ShapeParser());
        tierParser = manager.registerParser(new TierParser());
        fluidTypeParser = manager.registerParser(new FluidTypeParser(bus));
        blockMaterialParser = manager.registerParser(new BlockMaterialParser());
        armorMaterialParser = manager.registerParser(new ArmorMaterialParser());
        creativeModeTabParser = manager.registerParser(new CreativeModeTabParser(bus));
        mobEffectInstanceParser = manager.registerParser(new MobEffectInstanceParser());
    }

    private static CompletableFuture<ThingResourceManager> loaderFuture;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void construct(FMLConstructModEvent event)
    {
        event.enqueueWork(() -> {
            ThingResourceManager instance = ThingResourceManager.instance();

            ResourcePackLoader.loadResourcePacks(instance.getRepository(), modResourcePacks -> ModResourcesFinder.buildPackFinder(modResourcePacks, CustomPackType.THINGS));

            loaderFuture = instance.beginLoading();
        });
    }

    @SubscribeEvent
    public static void packFinder(AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.SERVER_DATA)
        {
            event.addRepositorySource(ThingResourceManager.instance().getWrappedPackFinder());
        }
    }

    @SubscribeEvent
    public static void finishLoading(NewRegistryEvent event)
    {
        ThingResourceManager.instance().waitForLoading(loaderFuture);
        loaderFuture = null;
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientHandlers
    {
        public static void addClientPackFinder()
        {
            Minecraft.getInstance().getResourcePackRepository().addPackFinder(ThingResourceManager.instance().getWrappedPackFinder());
        }

        @SubscribeEvent
        public static void constructMod(FMLConstructModEvent event)
        {
            event.enqueueWork(() -> {
                ClientHandlers.addClientPackFinder();
                BlockColorHandler.init();
                ItemColorHandler.init();
            });

            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, returnTo) -> {
                var thingPackManager = ThingResourceManager.instance();
                return new PackSelectionScreen(returnTo, thingPackManager.getRepository(),
                        rpl -> thingPackManager.onConfigScreenSave(), thingPackManager.getThingPacksLocation(),
                        Component.literal("Thing Packs"));
            }));
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            final ResourceLocation solid = new ResourceLocation("solid");
            JsonThings.blockParser.getBuilders().forEach(thing -> {
                if (thing.isInErrorState()) return;
                ResourceLocation layer = thing.getDefaultRenderLayer();
                if (!layer.equals(solid))
                {
                    ItemBlockRenderTypes.setRenderLayer(thing.get().self(), NamedRenderTypeManager.get(layer).block());
                }
            });
            JsonThings.fluidParser.getBuilders().forEach(thing -> {
                if (thing.isInErrorState()) return;
                ResourceLocation layer = thing.getDefaultRenderLayer();
                for(var fluid : thing.getAllSiblings())
                {
                    if (!layer.equals(solid))
                    {
                        ItemBlockRenderTypes.setRenderLayer(fluid, NamedRenderTypeManager.get(layer).block());
                    }
                }
            });
        }

        @SubscribeEvent
        public static void itemColorHandlers(RegisterColorHandlersEvent.Block event)
        {
            JsonThings.blockParser.getBuilders().forEach(thing -> {
                String handlerName = thing.getColorHandler();
                if (handlerName != null)
                {
                    BlockColor bc = BlockColorHandler.get(handlerName);
                    event.register(bc, thing.get().self());
                }
            });
        }

        @SubscribeEvent
        public static void itemColorHandlers(RegisterColorHandlersEvent.Item event)
        {
            JsonThings.itemParser.getBuilders().forEach(thing -> {
                if (thing.isInErrorState()) return;
                String handlerName = thing.getColorHandler();
                if (handlerName != null)
                {
                    Function<BlockColors, ItemColor> handler = ItemColorHandler.get(handlerName);
                    ItemColor ic = handler.apply(event.getBlockColors());
                    event.register(ic, ((Item) thing.get()));
                }
            });
        }
    }
}
