package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PackMetadataSection.class)
public class PackMetadataSectionMixin
{
    @Inject(method="forPackType(Lnet/minecraft/server/packs/PackType;)Lnet/minecraft/server/packs/metadata/MetadataSectionType;", at = @At("HEAD"), cancellable = true)
    private static void lastPreMinorVersionHandler(PackType type, CallbackInfoReturnable<MetadataSectionType<PackMetadataSection>> rt)
    {
        if (type == CustomPackType.THINGS)
        {
            rt.setReturnValue(CustomPackType.THINGS_METADATA);
        }
    }
}
