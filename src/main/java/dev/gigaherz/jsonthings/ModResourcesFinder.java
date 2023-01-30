package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

class ModResourcesFinder
{
    public static final Logger LOGGER = LogUtils.getLogger();

    static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, PackType packType)
    {
        return (packList) -> serverPackFinder(modResourcePacks, packList, packType);
    }

    private static void serverPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> consumer, PackType packType)
    {
        for (Map.Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet())
        {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();
            final Pack packInfo = Pack.readMetaAndCreate(name, Component.literal(mod.getModId()), false, (str) -> new net.minecraft.server.packs.PathPackResources(str, e.getKey().getFilePath(), true), packType, Pack.Position.BOTTOM, PackSource.DEFAULT);
            if (packInfo == null)
            {
                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                continue;
            }
            LOGGER.debug("Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
            consumer.accept(packInfo);
        }
    }
}
