package gigaherz.jsonthings.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

public abstract class ThingParser<TBuilder>
{
    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public void parse()
    {
        ModContainer mc = Loader.instance().activeModContainer();
        Loader.instance().setActiveModContainer(null);
        Loader.instance().getActiveModList().forEach(this::loadThings);
        Loader.instance().setActiveModContainer(mc);
    }

    private void loadThings(ModContainer mod)
    {
        CraftingHelper.findFiles(mod, String.format("assets/%s/things/%s", mod.getModId(), getThingType()),
                (path) -> true,
                (root, file) -> {

                    Loader.instance().setActiveModContainer(mod);

                    String relative = root.relativize(file).toString();
                    if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
                        return true;

                    String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                    ResourceLocation key = new ResourceLocation(mod.getModId(), name);

                    try (BufferedReader reader = Files.newBufferedReader(file))
                    {
                        JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
                        processThing(key, json);
                    }
                    catch (JsonParseException e)
                    {
                        FMLLog.log.error("Parsing error loading recipe {}", key, e);
                        return false;
                    }
                    catch (IOException e)
                    {
                        FMLLog.log.error("Couldn't read recipe {} from {}", key, file, e);
                        return false;
                    }
                    return true;
                }, true, true);
    }

    public abstract String getThingType();

    public abstract TBuilder processThing(ResourceLocation key, JsonObject data);
}
