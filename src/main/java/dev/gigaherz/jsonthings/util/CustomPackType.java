package dev.gigaherz.jsonthings.util;

import net.minecraft.server.packs.PackType;

public class CustomPackType
{
    public static PackType THINGS;

    static { // make sure the field has been initialized.
        //noinspection ResultOfMethodCallIgnored
        PackType.values();
    }
}
