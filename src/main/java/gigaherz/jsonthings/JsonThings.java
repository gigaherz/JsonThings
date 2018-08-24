package gigaherz.jsonthings;

import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.parser.ItemParser;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = JsonThings.MODID, version = JsonThings.VERSION)
public class JsonThings
{
    public static final String MODID = "jsonthings";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        ItemParser.init();
        ItemParser.BUILDERS.stream().map(ItemBuilder::build).forEach(event.getRegistry()::register);
    }
}
