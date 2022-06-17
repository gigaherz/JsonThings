package dev.gigaherz.jsonthings.things.scripting.rhino;

import com.google.common.collect.Sets;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.gigaherz.jsonthings.things.scripting.ThingScript;
import dev.gigaherz.jsonthings.things.scripting.rhino.dsl.*;
import dev.latvian.mods.rhino.*;
import net.minecraft.server.packs.resources.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.WillClose;
import javax.script.ScriptException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Set;

public class RhinoThingScript extends ThingScript
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static RhinoThingScript fromResource(@WillClose Resource resource, String name) throws IOException, ScriptException
    {
        Context cx = Context.enter();
        try
        {
            try(var reader = resource.openAsReader())
            {
                Scriptable scope = cx.initStandardObjects();

                Script script = cx.compileReader(reader, name, 0, null);

                var logger = LogManager.getLogger("ThingScript/" + name);

                scope = initDSL(scope, logger);

                script.exec(cx, scope);

                var result = scope.get("apply", scope);
                if (result instanceof Function function)
                {
                    return new RhinoThingScript(scope, function);
                }
                else
                {
                    throw new ScriptException("Error evaluating script " + name + ": Function 'apply' not found or not a function.");
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

    private static Scriptable initDSL(Scriptable _scope, Logger logger)
    {
        final var scope = _scope;
        final var flex = new NativeJavaClass(_scope, FlexEventResult.class);
        scope.put("FlexEventResult", scope, flex);
        for(var flexMethod : flex.getIds())
        {
            String name = (String)flexMethod;
            scope.put(name, scope, flex.get(name, flex));
        }
        scope.put("Log", scope, new NativeJavaObject(scope, logger, Logger.class));
        scope.put("Java", scope, new NativeJavaObject(scope, new JavaTypeAdapter(scope), JavaTypeAdapter.class));
        scope.put("useClass", scope, new BaseFunction(){
            final JavaTypeAdapter adapter = new JavaTypeAdapter(scope);

            @Override
            public Object call(Context cx, Scriptable _scope, Scriptable thisObj, Object[] args)
            {
                for (Object arg : args)
                {
                    adapter.doImport((String) arg);
                }

                return Undefined.instance;
            }
        });
        scope.put("use", scope, new LambdaBaseFunction((cx, __scope, thisObj, args) -> {
                for (Object arg : args)
                {
                    switch ((String)arg)
                    {
                        case "nbt" -> NbtDSL.use(cx, scope);
                        case "items" -> ItemsDSL.use(cx, scope);
                        case "blocks" -> BlocksDSL.use(cx, scope);
                        case "levels" -> LevelsDSL.use(cx, scope);
                        case "entities" -> EntitiesDSL.use(cx, scope);
                        case "effects" -> EffectsDSL.use(cx, scope);
                        case "attributes" -> AttributesDSL.use(cx, scope);
                        case "enchantments" -> EnchantmentsDSL.use(cx, scope);
                    }
                }
                return Undefined.instance;
        }));
        return scope;
    }

    @FunctionalInterface
    public interface LambdaFunction
    {
        Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args);
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

        public void doImport(String importString)
        {
            try
            {
                if (importString.endsWith(".*"))
                {
                    importString = importString.substring(0, importString.length() - 2);

                    // TODO (Maybe): Import all classes in package if the string corresponds to a valid package.
                    var cls = Class.forName(importString);

                    for (var method : cls.getMethods())
                    {
                        var flags = method.getModifiers();
                        if (Modifier.isPublic(flags) && Modifier.isStatic(flags))
                        {
                            scope.put(method.getName(), scope, new NativeJavaMethod(method, method.getName()));
                        }
                    }
                }
                else
                {
                    int last = importString.lastIndexOf(".");
                    var className = (last >= 0) ? importString.substring(last + 1) : importString;

                    var cls = Class.forName(importString);
                    scope.put(className, scope, new NativeJavaClass(scope, cls, false));
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
