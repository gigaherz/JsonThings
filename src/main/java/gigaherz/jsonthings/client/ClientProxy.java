package gigaherz.jsonthings.client;

import com.google.common.base.Charsets;
import gigaherz.jsonthings.IModProxy;
import gigaherz.jsonthings.JsonThings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ClientProxy implements IModProxy
{
    private static boolean initialized;

    public ClientProxy()
    {
        injectCustomResourcePack();
    }

    private static Field _defaultResourcePacks = ReflectionHelper.findField(Minecraft.class, "field_110449_ao", "defaultResourcePacks");

    @SuppressWarnings("unchecked")
    public static void injectCustomResourcePack()
    {
        if (initialized) return;
        initialized = true;

        File resourcesFolder = new File(Loader.instance().getConfigDir(), "jsonthings");

        if (!resourcesFolder.exists())
        {
            if (!resourcesFolder.mkdirs())
            {
                return;
            }
        }

        if (!resourcesFolder.exists() || !resourcesFolder.isDirectory())
        {
            return;
        }

        try
        {
            List<IResourcePack> rp = (List<IResourcePack>) _defaultResourcePacks.get(Minecraft.getMinecraft());

            rp.add(new FolderResourcePack(resourcesFolder)
            {
                String prefix = "assets/" + JsonThings.MODID + "/";

                @Override
                protected InputStream getInputStreamByName(String name) throws IOException
                {
                    if ("pack.mcmeta".equals(name))
                    {
                        return new ByteArrayInputStream(("{\"pack\":{\"description\": \"dummy\",\"pack_format\": 3}}").getBytes(Charsets.UTF_8));
                    }
                    if (!name.startsWith(prefix))
                        throw new FileNotFoundException(name);
                    return super.getInputStreamByName(name.substring(prefix.length()));
                }

                @Override
                protected boolean hasResourceName(String name)
                {
                    if ("pack.mcmeta".equals(name))
                        return true;
                    if (!name.startsWith(prefix))
                        return false;
                    return super.hasResourceName(name.substring(prefix.length()));
                }

                @Override
                public Set<String> getResourceDomains()
                {
                    return Collections.singleton(JsonThings.MODID);
                }
            });
        }
        catch (IllegalAccessException e)
        {
            // Ignore
        }
    }
}
