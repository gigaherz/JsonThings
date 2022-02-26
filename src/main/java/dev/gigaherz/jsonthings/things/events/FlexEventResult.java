package dev.gigaherz.jsonthings.things.events;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public record FlexEventResult(InteractionResult result, @Nullable Object object)
{
    public static FlexEventResult success()
    {
        return new FlexEventResult(InteractionResult.SUCCESS, null);
    }
    public static FlexEventResult success(@Nonnull Object resultState)
    {
        return new FlexEventResult(InteractionResult.SUCCESS, resultState);
    }

    public static FlexEventResult consume()
    {
        return new FlexEventResult(InteractionResult.CONSUME, null);
    }
    public static FlexEventResult consume(@Nonnull Object resultState)
    {
        return new FlexEventResult(InteractionResult.CONSUME, resultState);
    }

    public static FlexEventResult consumePartial()
    {
        return new FlexEventResult(InteractionResult.CONSUME_PARTIAL, null);
    }
    public static FlexEventResult consumePartial(@Nonnull Object resultState)
    {
        return new FlexEventResult(InteractionResult.CONSUME_PARTIAL, resultState);
    }

    public static FlexEventResult pass()
    {
        return new FlexEventResult(InteractionResult.PASS, null);
    }
    public static FlexEventResult pass(@Nonnull Object resultState)
    {
        return new FlexEventResult(InteractionResult.PASS, resultState);
    }

    public static FlexEventResult fail()
    {
        return new FlexEventResult(InteractionResult.FAIL, null);
    }
    public static FlexEventResult fail(@Nonnull Object resultState)
    {
        return new FlexEventResult(InteractionResult.FAIL, resultState);
    }

    public static FlexEventResult of(InteractionResultHolder<?> holder)
    {
        return new FlexEventResult(holder.getResult(), holder.getObject());
    }

    public static FlexEventResult of(InteractionResult result)
    {
        return new FlexEventResult(result, null);
    }

    public ItemStack stack()
    {
        return (ItemStack) Objects.requireNonNull(object);
    }

    public InteractionResultHolder<ItemStack> holder()
    {
        return new InteractionResultHolder<>(result, stack());
    }
}
