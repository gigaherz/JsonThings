package dev.gigaherz.jsonthings.things.builders;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public abstract class BaseBuilder<T> implements Supplier<T>
{
    private final ResourceLocation registryName;
    private T builtThing;

    protected BaseBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    private T build()
    {
        try
        {
            builtThing = buildInternal();
            return builtThing;
        }
        catch(Exception e)
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
}
