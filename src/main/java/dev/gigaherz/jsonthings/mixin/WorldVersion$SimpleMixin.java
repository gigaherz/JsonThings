package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.WorldVersion;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldVersion.Simple.class)
public class WorldVersion$SimpleMixin
{
    @Inject(method = "packVersion(Lnet/minecraft/server/packs/PackType;)I", at = @At("HEAD"), cancellable = true)
    public void packVersion(PackType p_415556_, CallbackInfoReturnable<Integer> rt)
    {
        if(p_415556_ == CustomPackType.THINGS)
        {
            rt.setReturnValue(CustomPackType.PACK_FORMAT_VERSION);
        }
    }
}
