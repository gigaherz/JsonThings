package dev.gigaherz.jsonthings.mixin;

import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PackType.class)
public enum PackTypeEnumExtension
{
    JSONTHINGS_THINGS("things");

    @Shadow
    PackTypeEnumExtension(String directory)
    {
    }
}
