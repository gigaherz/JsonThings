package dev.gigaherz.jsonthings.util;

import net.minecraft.server.packs.PackType;

public class CustomPackType
{
    public static final int PACK_FORMAT_VERSION = 10; // TODO: Bump when porting!
    public static final PackType THINGS = Enum.valueOf(PackType.class, "JSONTHINGS_THINGS");
}
