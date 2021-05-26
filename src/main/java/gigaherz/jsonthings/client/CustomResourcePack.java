package gigaherz.jsonthings.client;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

public class CustomResourcePack
{
    private static boolean initialized;

    public CustomResourcePack()
    {
        injectCustomResourcePack();
    }

    //private static Field _defaultResourcePacks = ReflectionHelper.findField(Minecraft.class, "field_110449_ao", "defaultResourcePacks");

    @SuppressWarnings("unchecked")
    public static void injectCustomResourcePack()
    {
        if (initialized) return;
        initialized = true;

        File resourcesFolder = new File(FMLPaths.CONFIGDIR.get().toFile(), "jsonthings");

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

        /*try
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
        }*/
    }
}
