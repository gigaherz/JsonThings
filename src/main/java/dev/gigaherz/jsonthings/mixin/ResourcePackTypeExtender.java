package dev.gigaherz.jsonthings.mixin;

import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(PackType.class)
public class ResourcePackTypeExtender
{
    @Shadow
    @Final
    @Mutable
    private static PackType[] $VALUES;

    @Invoker(value="<init>")
    private static PackType create(String name, int ordinal, String directoryName, com.mojang.bridge.game.PackType packType)
    {
        throw new IllegalStateException("Unreachable");
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void classInit(CallbackInfo cb)
    {
        var entry = create("JSONTHINGS_THINGS", $VALUES.length, "things", com.mojang.bridge.game.PackType.DATA);

        CustomPackType.THINGS = entry;

        $VALUES = Arrays.copyOf($VALUES, $VALUES.length + 1);
        $VALUES[$VALUES.length-1] = entry;
    }
}
