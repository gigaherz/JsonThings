package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.events.IEventRunner;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.things.scripting.ScriptParser;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseBuilder<T, B extends BaseBuilder<T,B>> implements Supplier<T>
{
    private final ThingParser<B> ownerParser;
    private final ResourceLocation registryName;
    private ResourceLocation parentBuilderName;
    private B parentBuilder;
    private T builtThing;
    private Map<String, List<ResourceLocation>> eventMap;

    protected BaseBuilder(ThingParser<B> ownerParser, ResourceLocation registryName)
    {
        this.ownerParser = ownerParser;
        this.registryName = registryName;
    }

    public T build()
    {
        try
        {
            builtThing = buildInternal();
            return builtThing;
        }
        catch (Exception e)
        {
            CrashReport report = CrashReport.forThrowable(e, "Error while building " + getThingTypeDisplayName() + " from " + registryName);

            fillReport(report);

            throw new ReportedException(report);
        }
    }

    protected abstract String getThingTypeDisplayName();

    protected CrashReportCategory fillReport(CrashReport crashReport)
    {
        CrashReportCategory reportCategory = crashReport.addCategory("Thing", 1);
        reportCategory.setDetail("Resource name", registryName);
        return reportCategory;
    }

    protected abstract T buildInternal();

    public final T get()
    {
        if (builtThing == null)
            return build();
        return builtThing;
    }

    public final ResourceLocation getRegistryName()
    {
        return registryName;
    }

    protected final ThingParser<B> getParser()
    {
        return ownerParser;
    }

    public void setParent(ResourceLocation parentBuilder)
    {
        this.parentBuilderName = parentBuilder;
    }

    public B requireParent()
    {
        if (parentBuilder == null)
        {
            if (parentBuilderName == null)
                throw new IllegalStateException("Parent not set");
            parentBuilder = getParser().getBuildersMap().get(parentBuilderName);
            if (parentBuilder == null)
                throw new IllegalStateException("The specified parent " + parentBuilderName + " is not a Json Things defined Block");
        }
        return parentBuilder;
    }

    @Nullable
    public B getParent()
    {
        if (parentBuilderName == null) return null;
        if (parentBuilder == null)
        {
            parentBuilder = getParser().getBuildersMap().get(parentBuilderName);
            if (parentBuilder == null)
            {
                parentBuilderName = null;
                return null;
            }
        }
        return parentBuilder;
    }

    @Nullable
    protected <V> V getValue(@Nullable V thisValue, Function<B, V> parentGetter)
    {
        return getValueOrElse(thisValue, parentGetter, null);
    }

    @Contract("_, _, !null -> !null")
    @Nullable
    protected <V> V getValueOrElse(@Nullable V thisValue, Function<B, V> parentGetter, @Nullable V defaultValue)
    {
        if (thisValue != null) return thisValue;
        var parent = getParent();
        if (parent != null)
        {
            return parentGetter.apply(parent);
        }
        return defaultValue;
    }

    @Contract("_, _, !null -> !null")
    @Nullable
    protected <V> V getValueOrElseGet(@Nullable V thisValue, Function<B, V> parentGetter, @Nullable Supplier<V> defaultValue)
    {
        if (thisValue != null) return thisValue;
        var parent = getParent();
        if (parent != null)
        {
            return parentGetter.apply(parent);
        }
        return defaultValue.get();
    }

    @Nullable
    protected final Map<String, List<ResourceLocation>> getEventMap()
    {
        return eventMap;
    }

    public void setEventMap(Map<String, List<ResourceLocation>> eventMap)
    {
        this.eventMap = eventMap;
    }

    protected void forEachEvent(BiConsumer<String, List<ResourceLocation>> consumer)
    {
        var ev = getEventMap();
        if (ev != null)
            ev.forEach(consumer);
        var parent = getParent();
        if (parent != null)
        {
            parent.forEachEvent(consumer);
        }
    }

    protected void constructEventHandlers(IEventRunner eventRunner)
    {
        if (ScriptParser.isEnabled())
        {
            forEachEvent((key, list) -> {
                for (var ev : list)
                {
                    eventRunner.addEventHandler(key, ScriptParser.instance().getEvent(ev));
                }
            });
        }
    }
}
