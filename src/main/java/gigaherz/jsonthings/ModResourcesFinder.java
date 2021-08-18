package gigaherz.jsonthings;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fmllegacy.packs.ModFileResourcePack;
import net.minecraftforge.fmllegacy.packs.ResourcePackLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.minecraftforge.fml.Logging.CORE;

class ModResourcesFinder
{
    static ResourcePackLoader.IPackInfoFinder buildPackFinder(Map<IModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, Pack> packSetter)
    {
        return (packList, factory) -> serverPackFinder(modResourcePacks, packSetter, packList, factory);
    }

    private static void serverPackFinder(Map<IModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, Pack> packSetter, Consumer<Pack> consumer, Pack.PackConstructor factory)
    {
        for (Map.Entry<IModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet())
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
            packSetter.accept(e.getValue(), packInfo);
            JsonThings.LOGGER.debug(CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
            consumer.accept(packInfo);
        }
    }
}
