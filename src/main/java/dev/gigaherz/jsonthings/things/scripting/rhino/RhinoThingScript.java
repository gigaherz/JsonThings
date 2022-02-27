package dev.gigaherz.jsonthings.things.scripting.rhino;

import com.google.common.collect.Sets;
import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventResult;
import dev.gigaherz.jsonthings.things.scripting.ThingScript;
import dev.latvian.mods.rhino.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.WillClose;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
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

                initDSL(scope, logger);

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

    private static void initDSL(Scriptable scope, Logger logger)
    {
        scope.put("FlexEventResult", scope, new NativeJavaClass(scope, FlexEventResult.class));
        scope.put("Log", scope, new NativeJavaObject(scope, logger, Logger.class));
        scope.put("Java", scope, new NativeJavaObject(scope, new JavaTypeAdapter(scope), JavaTypeAdapter.class));
        scope.put("import", scope, new BaseFunction(){
            final JavaTypeAdapter adapter = new JavaTypeAdapter(scope);

            @Override
            public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
            {
                adapter.doImport((String)args[0]);

                return Undefined.instance;
            }
        });
        scope.put("stack", scope, new LambdaBaseFunction((cx, _scope, thisObj, args) -> {
            var stack = new ItemStack((Item)args[0]);
            if (args.length >= 2)
            {
                stack.setCount((int)args[1]);
            }
            if (args.length >= 3)
            {
                stack.setTag((CompoundTag)args[2]);
            }
            return new NativeJavaObject(_scope, stack, ItemStack.class);
        }));
    }

    @FunctionalInterface
    public interface LambdaFunction
    {
        Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args);
    }

    public static class LambdaBaseFunction extends BaseFunction
    {
        private final LambdaFunction impl;

        public LambdaBaseFunction(LambdaFunction impl)
        {
            this.impl = impl;
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
        {
            return impl.call(cx, scope, thisObj, args);
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
