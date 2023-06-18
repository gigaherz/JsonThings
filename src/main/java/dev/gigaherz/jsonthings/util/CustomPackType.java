package dev.gigaherz.jsonthings.util;

import dev.gigaherz.jsonthings.JsonThings;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomPackType
{
    public static PackType THINGS;
}
