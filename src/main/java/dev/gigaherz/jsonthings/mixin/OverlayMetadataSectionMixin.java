package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OverlayMetadataSection.class)
public class OverlayMetadataSectionMixin
{
    @Inject(method="forPackType(Lnet/minecraft/server/packs/PackType;)Lnet/minecraft/server/packs/metadata/MetadataSectionType;", at = @At("HEAD"), cancellable = true)
    private static void forPackTypeHandler(PackType type, CallbackInfoReturnable<MetadataSectionType<OverlayMetadataSection>> rt)
    {
        if (type == CustomPackType.THINGS)
        {
            rt.setReturnValue(CustomPackType.THINGS_OVERLAY);
        }
    }

    @Inject(method="forPackTypeNeoForge(Lnet/minecraft/server/packs/PackType;)Lnet/minecraft/server/packs/metadata/MetadataSectionType;", at = @At("HEAD"), cancellable = true)
    private static void forPackTypeNeoForgeHandler(PackType type, CallbackInfoReturnable<MetadataSectionType<OverlayMetadataSection>> rt)
    {
        if (type == CustomPackType.THINGS)
        {
            rt.setReturnValue(CustomPackType.THINGS_OVERLAY);
        }
    }
}
