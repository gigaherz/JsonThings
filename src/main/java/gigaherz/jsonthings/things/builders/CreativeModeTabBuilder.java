package gigaherz.jsonthings.things.builders;

import gigaherz.jsonthings.things.misc.FlexCreativeModeTab;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;

public class CreativeModeTabBuilder
{
    private FlexCreativeModeTab builtTab = null;

    private final ResourceLocation registryName;
    private ResourceLocation iconItem;

    private CreativeModeTabBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static CreativeModeTabBuilder begin(ResourceLocation registryName)
    {
        return new CreativeModeTabBuilder(registryName);
    }

    public CreativeModeTabBuilder withIcon(ResourceLocation iconItem)
    {
        this.iconItem = iconItem;
        return this;
    }

    public FlexCreativeModeTab build()
    {
        return builtTab = new FlexCreativeModeTab(registryName.getNamespace() + "." + registryName.getPath().replace("/", "."), () -> Utils.getItemOrCrash(iconItem));
    }

    public FlexCreativeModeTab getBuiltTab()
    {
        if (builtTab == null)
            return build();
        return builtTab;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }
}
