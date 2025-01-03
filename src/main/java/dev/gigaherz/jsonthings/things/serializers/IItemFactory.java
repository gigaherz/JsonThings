package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public interface IItemFactory<T extends Item>
{
    T construct(Item.Properties properties, ItemBuilder builder);

    default void provideVariants(BuildCreativeModeTabContentsEvent event, ItemBuilder context)
    {
        event.accept(context.get().getDefaultInstance());
    }

    class WithVariants<T extends Item> implements IItemFactory<T>
    {
        private final IItemFactory<T> innerFactory;
        private final VariantProvider stackProvider;

        public WithVariants(IItemFactory<T> innerFactory, VariantProvider stackProvider)
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
        public void provideVariants(BuildCreativeModeTabContentsEvent event, ItemBuilder context)
        {
            stackProvider.provideVariants(event, context);
        }
    }

    @FunctionalInterface
    interface VariantProvider
    {
        void provideVariants(BuildCreativeModeTabContentsEvent event, ItemBuilder context);
    }
}
