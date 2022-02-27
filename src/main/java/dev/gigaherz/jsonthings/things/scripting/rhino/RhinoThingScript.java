package dev.gigaherz.jsonthings.things.scripting.rhino;

import com.google.common.collect.Sets;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.gigaherz.jsonthings.things.scripting.ThingScript;
import dev.latvian.mods.rhino.*;
import net.minecraft.server.packs.resources.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.WillClose;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class RhinoThingScript extends ThingScript
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static RhinoThingScript fromResource(@WillClose Resource resource) throws IOException, ScriptException
    {
        Context cx = Context.enter();
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
                scope.put("Log", scope, new NativeJavaObject(scope, logger, Logger.class));
                scope.put("Java", scope, new NativeJavaObject(scope, new JavaTypeAdapter(scope), JavaTypeAdapter.class));

                script.exec(cx, scope);

                var result = scope.get("apply", scope);
                if (result instanceof Function function)
                {
                    return new RhinoThingScript(scope, function);
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

    public RhinoThingScript(Scriptable scope, Function function)
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

    @SuppressWarnings("ClassCanBeRecord")
    public static class JavaTypeAdapter
    {
        public static Set<String> BLOCKED_PACKAGE_PREFIXES = Sets.newHashSet(
                "java.",
                "javax.",
                "sun."
        );
        public static Set<String> BLOCKED_PACKAGE_PREFIXE_EXCEPTIONS = Sets.newHashSet(
                "java.lang.",
                "java.math.",
                "java.util."
        );

        private final Scriptable scope;

        public JavaTypeAdapter(Scriptable scope)
        {
            this.scope = scope;
        }

        public Object type(String typename) throws ClassNotFoundException
        {
            if (!BLOCKED_PACKAGE_PREFIXES.contains(typename) || BLOCKED_PACKAGE_PREFIXE_EXCEPTIONS.contains(typename))
            {
                var cls = Class.forName(typename);
                return new NativeJavaClass(scope, cls, false);
            }
            throw new ClassNotFoundException(typename);
        }
    }
}