package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
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

    static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks)
    {
        return (packList, factory) -> serverPackFinder(modResourcePacks, packList, factory);
    }

    private static void serverPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> consumer, Pack.PackConstructor factory)
    {
        for (Map.Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet())
        {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();
            final Pack packInfo = Pack.create(name, false, e::getValue, factory, Pack.Position.BOTTOM, PackSource.DEFAULT);
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
