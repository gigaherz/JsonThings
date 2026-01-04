package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PackFormat.class)
public class PackFormatMixin
{
    @Inject(method="lastPreMinorVersion(Lnet/minecraft/server/packs/PackType;)I", at = @At("HEAD"), cancellable = true)
    private static void lastPreMinorVersionHandler(PackType type, CallbackInfoReturnable<Integer> rt)
    {
        if (type == CustomPackType.THINGS)
        {
            rt.setReturnValue(CustomPackType.PACK_FORMAT_VERSION.major());
        }
    }
}
