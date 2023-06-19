package dev.gigaherz.jsonthings.util;

import dev.gigaherz.jsonthings.JsonThings;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomPackType
{
    public static PackType THINGS;

    static { // make sure the field has been initialized.
        //noinspection ResultOfMethodCallIgnored
        PackType.values();
    }
}
