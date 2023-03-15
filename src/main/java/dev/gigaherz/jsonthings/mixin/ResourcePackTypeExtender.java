package dev.gigaherz.jsonthings.mixin;

import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.IExtensibleEnum;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PackType.class)
public class ResourcePackTypeExtender implements IExtensibleEnum
{
    private static PackType create(String name, String directoryName)
    {
        throw new IllegalStateException("Enum not extended");
    }
}
