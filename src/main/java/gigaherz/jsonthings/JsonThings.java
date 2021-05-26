package gigaherz.jsonthings;

import com.google.common.collect.Lists;
import gigaherz.jsonthings.block.builder.BlockBuilder;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.parser.BlockParser;
import gigaherz.jsonthings.parser.ItemParser;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
@Mod(JsonThings.MODID)
public class JsonThings
{
    public static final String MODID = "jsonthings";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    //ArmorItem.ArmorMaterial CUSTOM_MATERIAL = EnumHelper.addArmorMaterial("TEST1", "test1", 100, new int[]{1,2,3}, 5, SoundEvents.BLOCK_METAL_STEP, 5);

    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        BlockParser.init();
        BlockParser.BUILDERS.stream().map(BlockBuilder::build).forEach(event.getRegistry()::register);
    }

    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        ItemParser.init();
        ItemParser.BUILDERS.stream().map(ItemBuilder::build).forEach(event.getRegistry()::register);
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
