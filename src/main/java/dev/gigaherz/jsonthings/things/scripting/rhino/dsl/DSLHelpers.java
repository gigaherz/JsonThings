package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class DSLHelpers
{
    public static <T extends IForgeRegistryEntry<T>> T find(IForgeRegistry<T> reg, String n)
    {
        var rl = new ResourceLocation(n);

        if (!reg.containsKey(rl))
            throw new RuntimeException("Cannot find effect with name " + rl);

        return reg.getValue(rl);
    }

}
