package gigaherz.jsonthings.mixin;

import net.minecraft.resources.ResourcePackType;
import net.minecraftforge.common.IExtensibleEnum;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ResourcePackType.class)
public class ResourcePackTypeExtender implements IExtensibleEnum
{
    private static ResourcePackType create(String name, String directoryName)
    {
        throw new IllegalStateException("Enum not extended");
    }
}
