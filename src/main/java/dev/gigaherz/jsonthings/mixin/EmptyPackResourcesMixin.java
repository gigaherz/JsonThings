package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.neoforge.resource.EmptyPackResources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmptyPackResources.class)
public abstract class EmptyPackResourcesMixin
{
    @Final
    @Shadow
    private PackMetadataSection packMeta;

    @Inject(method="getMetadataSection(Lnet/minecraft/server/packs/metadata/MetadataSectionType;)Ljava/lang/Object;", at = @At("HEAD"), cancellable = true)
    private <T> void forPackTypeNeoForgeHandler(MetadataSectionType<T> type, CallbackInfoReturnable<T> rt)
    {
        if (type == CustomPackType.THINGS_METADATA)
        {
            rt.setReturnValue((T) this.packMeta);
        }
    }
}
