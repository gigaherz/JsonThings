package dev.gigaherz.jsonthings.mixin;

import net.minecraft.server.packs.PackType;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Arrays;

@Mixin(PackType.class)
public class ResourcePackTypeExtender
{
    @Shadow(remap = false)
    @Final
    private static PackType[] $VALUES;

    @SuppressWarnings("SameParameterValue")
    @Invoker(value="<init>")
    private static PackType create(String name, int ordinal, String directoryName)
    {
        throw new IllegalStateException("Unreachable");
    }

    static
    {
        var entry = create("JSONTHINGS_THINGS", $VALUES.length, "things");

        //noinspection ShadowFinalModification
        $VALUES = ArrayUtils.add($VALUES, entry);
    }
}
