package dev.gigaherz.jsonthings.things.serializers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.IFlexFluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public interface IFluidSerializer<T extends Fluid & IFlexFluid>
{
    IFluidFactory<T> createFactory(ResourceLocation name, JsonObject data);
}
