package gigaherz.jsonthings.client;

import gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.client.Minecraft;

public class ClientThingResources
{
    public static void addClientPackFinder()
    {
        Minecraft.getInstance().getResourcePackList().addPackFinder(ThingResourceManager.INSTANCE.getFolderPackFinder());
    }
}
