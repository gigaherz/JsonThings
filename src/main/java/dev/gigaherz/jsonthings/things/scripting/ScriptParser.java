package dev.gigaherz.jsonthings.things.scripting;

import dev.gigaherz.jsonthings.things.parsers.McFunctionScriptParser;
import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;
import dev.gigaherz.jsonthings.things.scripting.rhino.RhinoThingScript;
import dev.gigaherz.jsonthings.util.KeyNotFoundException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScriptParser extends SimplePreparableReloadListener<Map<ResourceLocation, ThingScript>> {
    public static final Logger LOGGER = LogManager.getLogger();

    private static final ScriptParser instance = new ScriptParser();
    public static final String SCRIPTS_FOLDER = "scripts";
    public static final int SCRIPTS_FOLDER_LENGTH = SCRIPTS_FOLDER.length();
    public static final String JS_EXTENSION = ".js";
    public static final int JS_EXTENSION_LENGTH = JS_EXTENSION.length();

    private static boolean enabled = false;

    public static ScriptParser instance() {
        return instance;
    }

    public static void enable(ThingResourceManager manager) {
        if (!enabled) {
            manager.addResourceReloadListener(instance());
            enabled = true;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private Map<ResourceLocation, ThingScript> scripts = new HashMap<>();

    @Override
    protected Map<ResourceLocation, ThingScript> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        var resources = pResourceManager.listResources(SCRIPTS_FOLDER, t -> t.getPath().endsWith(JS_EXTENSION));

        var map = new HashMap<ResourceLocation, ThingScript>();

        for (var entry : resources.entrySet()) {
            var key = entry.getKey();
            var res = entry.getValue();
            var path = key.getPath();
            var cleanPath = path.substring(SCRIPTS_FOLDER_LENGTH + 1, path.length() - JS_EXTENSION_LENGTH);
            var id = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), cleanPath);
            try {
                map.put(id, RhinoThingScript.fromResource(res, id.toString()));
            } catch (IOException | ScriptException e) {
                LOGGER.error("Error parsing script " + res, e);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, ThingScript> pObject, ResourceManager pResourceManager,
            ProfilerFiller pProfiler) {
        scripts = pObject;
    }

    @Nonnull
    public ThingScript getEvent(ResourceLocation id) {
        if (scripts.containsKey(id))
            return scripts.get(id);
        if (McFunctionScriptParser.scripts.containsKey(id))
            return McFunctionScriptParser.scripts.get(id);
        throw new KeyNotFoundException("Script with id " + id + " not found.");

    }
}
