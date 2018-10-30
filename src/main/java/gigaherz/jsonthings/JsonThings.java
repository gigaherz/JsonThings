package gigaherz.jsonthings;

import com.google.common.collect.Lists;
import gigaherz.jsonthings.block.builder.BlockBuilder;
import gigaherz.jsonthings.item.IFlexItem;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.item.builder.ModelInfo;
import gigaherz.jsonthings.parser.BlockParser;
import gigaherz.jsonthings.parser.ItemParser;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.reflect.internal.util.WeakHashSet;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
@Mod(modid = JsonThings.MODID, version = JsonThings.VERSION)
public class JsonThings
{
    public static final String MODID = "jsonthings";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "gigaherz.jsonthings.client.ClientProxy")
    public static IModProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    ItemArmor.ArmorMaterial CUSTOM_MATERIAL = EnumHelper.addArmorMaterial("TEST1", "test1", 100, new int[]{1,2,3}, 5, SoundEvents.BLOCK_METAL_STEP, 5);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        BlockParser.init();
        BlockParser.BUILDERS.stream().map(BlockBuilder::build).forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        ItemParser.init();
        ItemParser.BUILDERS.stream().map(ItemBuilder::build).forEach(event.getRegistry()::register);
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = JsonThings.MODID)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            for (ItemBuilder b : ItemParser.BUILDERS)
            {
                Item item = b.getBuiltItem();
                ModelInfo modelInfo = b.getModelInfo();
                if (modelInfo != null)
                {
                    for (ModelInfo.ModelMapping mapping : modelInfo.mappings)
                    {
                        ModelLoader.setCustomModelResourceLocation(
                                item, mapping.metadata, new ModelResourceLocation(mapping.fileName, mapping.variantName));
                    }
                }
                else
                {
                    ModelLoader.setCustomModelResourceLocation(
                            item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
                }
            }
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
