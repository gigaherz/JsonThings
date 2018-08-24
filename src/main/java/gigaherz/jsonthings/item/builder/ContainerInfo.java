package gigaherz.jsonthings.item.builder;

import net.minecraft.util.ResourceLocation;

public class ContainerInfo
{
    public ResourceLocation emptyItem;

    public ContainerInfo(ResourceLocation registryName, String emptyItem)
    {
        if (emptyItem.contains(":"))
            this.emptyItem = new ResourceLocation(emptyItem);
        else
            this.emptyItem = new ResourceLocation(registryName.getNamespace(), emptyItem);
    }
}
