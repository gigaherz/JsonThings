package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public
interface ItemVariantProvider
{
    void provideVariants(ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.Output output, CreativeModeTab.ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit);
}
