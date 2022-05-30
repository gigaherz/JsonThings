package dev.gigaherz.jsonthings.things.serializers;

import dev.gigaherz.jsonthings.things.IFlexFluid;
import dev.gigaherz.jsonthings.things.builders.FluidBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.Collections;
import java.util.function.BiConsumer;

public interface IFluidFactory<T extends Fluid & IFlexFluid>
{
    T construct(FluidBuilder builder);

    default void register(FluidBuilder builder, BiConsumer<ResourceLocation, Fluid> register)
    {
        register.accept(builder.getRegistryName(), builder.get().self());
    }

    default Iterable<Fluid> getAllSiblings(FluidBuilder builder)
    {
        return Collections.singleton(builder.get().self());
    }
}
