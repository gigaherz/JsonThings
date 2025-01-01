package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.things.builders.ItemBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface IItemFactory<T extends Item>
{
    T construct(Item.Properties properties, ItemBuilder builder);

    default void provideVariants(ItemBuilder builder, Consumer<ItemStack> stackConsumer)
    {
        stackConsumer.accept(builder.get().self().getDefaultInstance());
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
        public void provideVariants(ItemBuilder builder, Consumer<ItemStack> stackConsumer)
        {
            stackProvider.provideVariants(builder, stackConsumer);
        }
    }

    @FunctionalInterface
    interface VariantProvider {
        void provideVariants(ItemBuilder builder, Consumer<ItemStack> stackConsumer);
    }
}
