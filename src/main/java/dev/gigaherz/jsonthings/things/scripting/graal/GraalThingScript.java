package dev.gigaherz.jsonthings.things.scripting.graal;
/*
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.gigaherz.jsonthings.things.scripting.ThingScript;
import net.minecraft.server.packs.resources.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import javax.annotation.WillClose;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;

public class GraalThingScript extends ThingScript
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static GraalThingScript fromResource(@WillClose Resource resource) throws IOException, ScriptException
    {
        try(resource;
            var stream = resource.getInputStream();
            var reader = new InputStreamReader(stream);
            var context = Context.newBuilder("js")
                    .allowHostAccess(HostAccess.ALL)
                    //allows access to all Java classes
                    .allowHostClassLookup(className -> true)
                    .build())
        {

            var source = Source.newBuilder("js", reader, resource.getLocation().toString()).build();

            var script = context.parse(source);

            var logger = LogManager.getLogger("ThingScript/" + resource.getLocation());

            script.putMember("FlexEventResult", FlexEventResult.class);
            script.putMember("Log", logger);

            var ctx = script.execute();

            var applyFunction = ctx.getMember("apply");
            if (applyFunction.canExecute())
            {
                return new GraalThingScript(applyFunction);
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

    private final Value function;

    public GraalThingScript(Value function)
    {
        this.function = function;
    }

    @Override
    public FlexEventResult apply(String eventName, FlexEventContext context)
    {
        var wrappedContext = new FlexEventGraalProxy(context);
        Value result = function.execute(eventName, wrappedContext);
        return result.as(FlexEventResult.class);
    }
}
*/