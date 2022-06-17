package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.misc.FlexCreativeModeTab;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;

public class CreativeModeTabBuilder extends BaseBuilder<FlexCreativeModeTab, CreativeModeTabBuilder>
{
    public static CreativeModeTabBuilder begin(ThingParser<CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
    {
        return new CreativeModeTabBuilder(ownerParser, registryName);
    }

    private ResourceLocation iconItem;

    private CreativeModeTabBuilder(ThingParser<CreativeModeTabBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Creative Mode Tab";
    }

    public void setIcon(ResourceLocation iconItem)
    {
        this.iconItem = iconItem;
    }

    @Override
    protected FlexCreativeModeTab buildInternal()
    {
        ResourceLocation registryName = getRegistryName();
        return new FlexCreativeModeTab(registryName.getNamespace() + "." + registryName.getPath().replace("/", "."), () -> Utils.getItemOrCrash(iconItem));
    }
}
