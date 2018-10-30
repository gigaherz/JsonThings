package gigaherz.jsonthings.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gigaherz.jsonthings.JsonThings;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.BiFunction;

public abstract class ThingParser<TBuilder>
{
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public void parse()
    {
        ModContainer mc = Loader.instance().activeModContainer();
        Loader.instance().setActiveModContainer(null);
        Loader.instance().getActiveModList().forEach(this::loadThings);
        Loader.instance().setActiveModContainer(mc);

        // TODO: implement thingpacks (zip files with content in them)
        File dataPack = new File(Loader.instance().getConfigDir(), "jsonthings");

        findFilesInDataPack(dataPack, getThingType(),
                (root, file) -> loadThing(root, file, "jsonthings"));
    }

    private void loadThings(ModContainer mod)
    {
        CraftingHelper.findFiles(mod, String.format("assets/%s/things/%s", mod.getModId(), getThingType()), null,
                (root, file) -> {
                    Loader.instance().setActiveModContainer(mod);
                    String domain = mod.getModId();

                    return loadThing(root, file, domain);
                }, true, true);
    }

    private boolean loadThing(Path root, Path file, String domain)
    {
        String relative = root.relativize(file).toString();
        if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
            return true;

        String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");

        ResourceLocation key = new ResourceLocation(domain, name);

        try (BufferedReader reader = Files.newBufferedReader(file))
        {
            JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
            processThing(key, json);
        }
        catch (JsonParseException e)
        {
            JsonThings.LOGGER.error("Parsing error loading recipe {}", key, e);
            return false;
        }
        catch (IOException e)
        {
            JsonThings.LOGGER.error("Couldn't read recipe {} from {}", key, file, e);
            return false;
        }
        return true;
    }

    private static boolean findFilesInDataPack(
            File dataPack, String thingType,
            BiFunction<Path, Path, Boolean> processor)
    {
        FileSystem fs = null;
        try
        {
            Path root = null;
            if (dataPack.isFile())
            {
                try
                {
                    fs = FileSystems.newFileSystem(dataPack.toPath(), null);
                    root = fs.getPath(String.format("/things/%s", thingType));
                }
                catch (IOException e)
                {
                    JsonThings.LOGGER.error("Error loading FileSystem from jar: ", e);
                    return false;
                }
            }
            else if (dataPack.isDirectory())
            {
                root = dataPack.toPath().resolve(String.format("things/%s", thingType));
            }

            if (root == null || !Files.exists(root))
                return true;

            boolean success = true;

            try
            {
                Iterator<Path> itr = Files.walk(root).iterator();
                while (itr.hasNext())
                {
                    success &= processor.apply(root, itr.next());
                }
            }
            catch (IOException e)
            {
                JsonThings.LOGGER.error("Error iterating filesystem for: {}", dataPack, e);
                return false;
            }

            return success;
        }
        finally
        {
            IOUtils.closeQuietly(fs);
        }
    }

    public abstract String getThingType();

    public abstract TBuilder processThing(ResourceLocation key, JsonObject data);
}
