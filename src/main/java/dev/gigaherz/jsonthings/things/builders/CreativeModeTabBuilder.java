package dev.gigaherz.jsonthings.things.builders;

import dev.gigaherz.jsonthings.things.misc.FlexCreativeModeTab;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;

public class CreativeModeTabBuilder extends BaseBuilder<FlexCreativeModeTab>
{
    private ResourceLocation iconItem;

    private CreativeModeTabBuilder(ResourceLocation registryName)
    {
        super(registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Creative Mode Tab";
    }

    public static CreativeModeTabBuilder begin(ResourceLocation registryName)
    {
        return new CreativeModeTabBuilder(registryName);
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
