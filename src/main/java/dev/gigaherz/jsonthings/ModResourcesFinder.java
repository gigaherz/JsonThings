package dev.gigaherz.jsonthings;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.minecraftforge.fml.Logging.CORE;

class ModResourcesFinder
{
    static ResourcePackLoader.IPackInfoFinder buildPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter)
    {
        return (packList, factory) -> serverPackFinder(modResourcePacks, packSetter, packList, factory);
    }

    private static void serverPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackInfo> packSetter, Consumer<ResourcePackInfo> consumer, ResourcePackInfo.IFactory factory)
    {
        for (Map.Entry<ModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet())
        {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();
            final ResourcePackInfo packInfo = ResourcePackInfo.create(name, false, e::getValue, factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.DEFAULT);
            if (packInfo == null)
            {
                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                continue;
            }
            packSetter.accept(e.getValue(), packInfo);
            JsonThings.LOGGER.debug(CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
            consumer.accept(packInfo);
        }
    }
}
