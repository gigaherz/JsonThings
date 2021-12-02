package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Random;

public class FlexTreeGrower extends AbstractTreeGrower
{
    private final ResourceKey<ConfiguredFeature<?, ?>> featureKey;

    public FlexTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> featureKey)
    {
        this.featureKey = featureKey;
    }

    @Nullable
    @Override
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random pRandom, boolean pLargeHive)
    {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return null;

        var feature = server.registryAccess().ownedRegistryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).getOrThrow(featureKey);

        return dirtyCast(feature);
    }

    private <R, T> R dirtyCast(T val)
    {
        //noinspection unchecked
        return (R) val;
    }
}
