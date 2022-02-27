package dev.gigaherz.jsonthings.things.scripting.scriptengine;
/*
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.gigaherz.jsonthings.things.scripting.ThingScript;
import net.minecraft.server.packs.resources.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.WillClose;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;

public class ScriptSystemThingScript extends ThingScript
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

    public static ScriptSystemThingScript fromResource(@WillClose Resource resource) throws ScriptException
    {
        try(resource;
            var stream = resource.getInputStream();
            var reader = new InputStreamReader(stream))
        {
            ScriptEngine engine = SCRIPT_ENGINE_MANAGER.getEngineByName("nashorn");

            var context = engine.getContext();
            context.setAttribute("sourceName", resource.getLocation().toString(), 0);

            var logger = LogManager.getLogger("ThingScript/" + resource.getLocation());

            engine.put("FlexEventResult", FlexEventResult.class);
            engine.put("Log", logger);

            var script = engine.eval(reader, context);

            if (script instanceof Invocable inv)
            {
                return new ScriptSystemThingScript(inv);
            }
            else
            {
                throw new ScriptException("Error evaluating script " + resource.getLocation() + ": Function 'apply' not found or not a function.");
            }
        }
        catch(Exception e)
        {
            throw new ScriptException(e);
        }
    }

    private final Invocable script;

    public ScriptSystemThingScript(Invocable script)
    {
        this.script = script;
    }

    @Override
    public FlexEventResult apply(String eventName, FlexEventContext context)
    {
        try
        {
            var wrappedContext = new FlexEventScriptable(context);
            var result = script.invokeFunction("apply", eventName, wrappedContext);
            return (FlexEventResult)result;
        }
        catch (ScriptException | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }
}
*/