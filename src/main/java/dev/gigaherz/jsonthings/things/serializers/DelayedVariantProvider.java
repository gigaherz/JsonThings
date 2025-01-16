package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import dev.gigaherz.jsonthings.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

public class DelayedVariantProvider implements ItemVariantProvider
{
    private final ResourceLocation itemName;
    private ItemVariantProvider actualVariant;

    public DelayedVariantProvider(ResourceLocation item)
    {
        itemName = item;
    }

    final
    @Override
    public void provideVariants(ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.Output output, CreativeModeTab.ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit)
    {
        if (actualVariant == null)
        {
            var builder = JsonThings.itemParser.getBuildersMap().get(itemName);
            if (builder != null)
            {
                actualVariant = builder;
            }
            else
            {
                var item = Utils.getOrCrash(BuiltInRegistries.ITEM, itemName);
                if (item instanceof ItemVariantProvider provider)
                {
                    actualVariant = provider;
                }
                else
                {
                    actualVariant = (tabKey1, output1, parameters1, context1, explicit1) -> {
                        output1.accept(item.getDefaultInstance());
                    };
                }
            }
        }

        actualVariant.provideVariants(tabKey, output, parameters, context, explicit);
    }
}
