package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.misc.FlexCreativeModeTab;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreativeModeTabBuilder extends BaseBuilder<FlexCreativeModeTab, CreativeModeTabBuilder>
{

    public static CreativeModeTabBuilder begin(ThingParser<CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
    {
        return new CreativeModeTabBuilder(ownerParser, registryName);
    }

    private StackContext iconItem;
    private final ArrayList<StackContext> items = new ArrayList<>();

    private CreativeModeTabBuilder(ThingParser<CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Creative Mode Tab";
    }

    public void setIcon(StackContext stackContext)
    {
        this.iconItem = stackContext;
    }

    @Override
    protected FlexCreativeModeTab buildInternal()
    {
        ResourceLocation registryName = getRegistryName();
        return new FlexCreativeModeTab(registryName.getNamespace() + "." + registryName.getPath().replace("/", "."), iconItem);
    }

    public void addItem(StackContext stackContext)
    {
        this.items.add(stackContext);
    }

    public List<StackContext> getItems()
    {
        return Collections.unmodifiableList(items);
    }
}
