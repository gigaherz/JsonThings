package gigaherz.jsonthings.client;

import gigaherz.jsonthings.parser.ThingResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.packs.ResourcePackLoader;

import java.io.File;

public class ClientThingResources
{
    public static void addClientPackFinder() {
        Minecraft.getInstance().getResourcePackList().addPackFinder(ThingResourceManager.INSTANCE.getFolderPackFinder());
    }
}
