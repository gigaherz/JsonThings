package gigaherz.jsonthings.client;

import gigaherz.jsonthings.JsonThings;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.item.builder.ModelInfo;
import gigaherz.jsonthings.parser.ItemParser;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = JsonThings.MODID)
public class ClientEvents
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        for (ItemBuilder b : ItemParser.BUILDERS)
        {
            ModelInfo modelInfo = b.getModelInfo();
            if (modelInfo != null)
            {
                Item item = b.getBuiltItem();
                for (ModelInfo.ModelMapping mapping : modelInfo.mappings)
                {
                    ModelLoader.setCustomModelResourceLocation(
                            item, mapping.metadata, new ModelResourceLocation(mapping.fileName, mapping.variantName));
                }
            }
        }
    }
}
