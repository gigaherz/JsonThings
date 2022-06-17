package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

public class FlexTreeGrower extends AbstractTreeGrower
{
    private final ResourceKey<ConfiguredFeature<?, ?>> featureKey;

    public FlexTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> featureKey)
    {
        this.featureKey = featureKey;
    }

    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive)
    {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return null;

        var feature = server.registryAccess().ownedRegistryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).getHolder(featureKey);

        return feature.orElse(null);
    }
}
