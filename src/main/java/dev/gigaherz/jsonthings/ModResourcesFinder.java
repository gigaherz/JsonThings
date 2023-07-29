/*
 * Based on net.minecraftforge.client.loading.ClientModLoader.java
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package dev.gigaherz.jsonthings;

import com.mojang.logging.LogUtils;
import dev.gigaherz.jsonthings.util.CustomPackType;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.DelegatingPackResources;
import net.minecraftforge.resource.PathPackResources;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

class ModResourcesFinder
{
    public static final Logger LOGGER = LogUtils.getLogger();

    static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks)
    {
        return (packList) -> serverPackFinder(modResourcePacks, packList);
    }

    private static void serverPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> consumer)
    {
        List<PathPackResources> hiddenPacks = new ArrayList<>();
        for (Map.Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet())
        {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();
            final Pack pack = Pack.readMetaAndCreate(name, Component.literal(e.getValue().packId()), false, id -> e.getValue(), CustomPackType.THINGS, Pack.Position.BOTTOM, PackSource.DEFAULT);
            if (pack == null)
            {
                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                continue;
            }
            LOGGER.debug("Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
            if (mod.getOwningFile().showAsResourcePack())
            {
                consumer.accept(pack);
            }
            else
            {
                hiddenPacks.add(e.getValue());
            }
        }

        // Create a resource pack merging all mod resources that should be hidden
        final Pack modResourcesPack = Pack.readMetaAndCreate("mod_resources", Component.literal("Mod Resources"), true,
                id -> new DelegatingPackResources(id, false, new PackMetadataSection(Component.translatable("fml.resources.modresources", hiddenPacks.size()),
                        SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)), hiddenPacks),
                PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.DEFAULT);
        consumer.accept(modResourcesPack);
    }
}
