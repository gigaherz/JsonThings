package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface IItemFactory<T extends Item> extends ItemVariantProvider
{
    T construct(Item.Properties properties, ItemBuilder builder);

    @Override
    default void provideVariants(ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.Output output, CreativeModeTab.ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit)
    {
        var item = Objects.requireNonNull(context).get();
        if (item instanceof ItemVariantProvider provider)
            provider.provideVariants(tabKey, output, parameters, context, explicit);
        else
            output.accept(item.getDefaultInstance());
    }

    class WithVariants<T extends Item> implements IItemFactory<T>
    {
        private final IItemFactory<T> innerFactory;
        private final ItemVariantProvider stackProvider;

        public WithVariants(IItemFactory<T> innerFactory, ItemVariantProvider stackProvider)
        {
            this.innerFactory = innerFactory;
            this.stackProvider = stackProvider;
        }

        @Override
        public T construct(Item.Properties properties, ItemBuilder builder)
        {
            return innerFactory.construct(properties, builder);
        }

        @Override
        public void provideVariants(ResourceKey<CreativeModeTab> tabKey,CreativeModeTab.Output output, CreativeModeTab.ItemDisplayParameters parameters, @Nullable ItemBuilder context, boolean explicit)
        {
            stackProvider.provideVariants(tabKey, output, parameters, context, explicit);
        }
    }
    }
