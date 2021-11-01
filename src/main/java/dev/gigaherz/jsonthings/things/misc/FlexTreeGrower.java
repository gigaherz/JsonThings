package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.block.trees.Tree;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Random;

public class FlexTreeGrower extends Tree
{
    private final RegistryKey<ConfiguredFeature<?, ?>> featureKey;

    public FlexTreeGrower(RegistryKey<ConfiguredFeature<?, ?>> featureKey)
    {
        this.featureKey = featureKey;
    }

    @Nullable
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random pRandom, boolean pLargeHive)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return null;

        ConfiguredFeature<?, ?> feature = server.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).getOrThrow(featureKey);

        return dirtyCast(feature);
    }

    private <R, T> R dirtyCast(T val)
    {
        //noinspection unchecked
        return (R) val;
    }
}
