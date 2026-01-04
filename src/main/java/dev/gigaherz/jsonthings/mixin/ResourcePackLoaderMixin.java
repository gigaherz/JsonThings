package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourcePackLoader.class)
public class ResourcePackLoaderMixin
{
    @Inject(method="metadataTypeForPackType(Lnet/minecraft/server/packs/PackType;)Lnet/minecraft/server/packs/metadata/MetadataSectionType;", at = @At("HEAD"), cancellable = true)
    private static void customPackTypeHandler(PackType type, CallbackInfoReturnable<MetadataSectionType<PackMetadataSection>> returnable)
    {
        if (type == CustomPackType.THINGS)
        {
            returnable.setReturnValue(CustomPackType.OPTIONAL_THINGS_METADATA);
        }
    }
}
