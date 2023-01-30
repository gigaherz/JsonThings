package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

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
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pLargeHive)
    {
        return featureKey;
    }
}
