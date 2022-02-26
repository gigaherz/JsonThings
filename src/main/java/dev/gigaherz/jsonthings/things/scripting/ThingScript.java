package dev.gigaherz.jsonthings.things.scripting;

import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.latvian.mods.rhino.*;
import net.minecraft.server.packs.resources.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.WillClose;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;

@SuppressWarnings("ClassCanBeRecord")
public class ThingScript implements FlexEventHandler
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static ThingScript fromResource(@WillClose Resource resource) throws IOException, ScriptException
    {
        Context cx = Context.enter();
        cx.isStrictMode()
        try
        {
            try(resource;
                var stream = resource.getInputStream();
                var reader = new InputStreamReader(stream))
            {
                Scriptable scope = cx.initStandardObjects();

                Script script = cx.compileReader(reader, resource.getLocation().toString(), 0, null);

                var logger = LogManager.getLogger("ThingScript/" + resource.getLocation());

                scope.put("FlexEventResult", scope, new NativeJavaClass(scope, FlexEventResult.class));
                scope.put("LOGGER", scope, new NativeJavaObject(scope, logger, Logger.class));

                script.exec(cx, scope);

                var result = scope.get("apply", scope);
                if (result instanceof Function function)
                {
                    return new ThingScript(scope, function);
                }
                else
                {
                    throw new ScriptException("Error evaluating script " + resource.getLocation() + ": Function 'apply' not found or not a function.");
                }
            }
            catch(EcmaError e)
            {
                throw new ScriptException(e);
            }
        }
        finally
        {
            Context.exit();
        }
    }

    private final Scriptable scope;
    private final Function function;

    public ThingScript(Scriptable scope, Function function)
    {
        this.scope = scope;
        this.function = function;
    }

    @Override
    public FlexEventResult apply(String eventName, FlexEventContext context)
    {
        Context cx = Context.enter();
        try
        {
            var wrappedContext = new FlexEventScriptable(scope, context);
            Object result = function.call(cx, scope, scope, new Object[]{eventName, wrappedContext});
            return (FlexEventResult) ((NativeJavaObject)result).unwrap();
        }
        finally
        {
            Context.exit();
        }
    }
}
