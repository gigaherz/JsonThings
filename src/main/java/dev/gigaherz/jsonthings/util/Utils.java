package dev.gigaherz.jsonthings.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class Utils
{
    public static <T extends Comparable<T>> T getPropertyValue(Property<T> prop, String value)
    {
        Optional<T> propValue = prop.getValue(value);
        return propValue.orElseThrow(() -> new IllegalStateException("Value " + value + " for property " + prop.getName() + " not found in the allowed values."));
    }

    @Nonnull
    public static <T> T orElse(@Nullable T val, T def)
    {
        return val != null ? val : def;
    }

    public static <T> T orElse(@Nullable T val, Supplier<T> def)
    {
        return val != null ? val : def.get();
    }

    public static Item getItemOrCrash(ResourceLocation which)
    {
        return getOrCrash(ForgeRegistries.ITEMS, which);
    }

    public static Block getBlockOrCrash(ResourceLocation which)
    {
        return getOrCrash(ForgeRegistries.BLOCKS, which);
    }

    public static <T extends IForgeRegistryEntry<T>> T getOrCrash(IForgeRegistry<T> reg, ResourceLocation which)
    {
        if (!reg.containsKey(which))
            throw new RuntimeException(String.format("Could not find a %s with name %s in the regsitry.", reg.getRegistrySuperType().getSimpleName(), which));
        //noinspection ConstantConditions
        return reg.getValue(which);
    }

    public static <T> T getOrCrash(Registry<T> registry, ResourceLocation name)
    {
        T t = registry.get(name);
        if (t == null)
            throw new IllegalStateException("No object with name " + name + " found in the registry " + registry);
        return t;
    }

    public static <T> T getOrCrash(Registry<T> registry, String name)
    {
        return getOrCrash(registry, new ResourceLocation(name));
    }

    public IFormattableTextComponent withFont(IFormattableTextComponent component, ResourceLocation font)
    {
        return component.withStyle(style -> style.withFont(font));
    }
}
