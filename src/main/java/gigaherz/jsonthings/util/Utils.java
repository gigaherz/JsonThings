package gigaherz.jsonthings.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static Item getItemOrCrash(ResourceLocation which)
    {
        if (!ForgeRegistries.ITEMS.containsKey(which))
            throw new RuntimeException(String.format("Attempted to make a block-placing item for '%s' without the associated block", which));
        //noinspection ConstantConditions
        return ForgeRegistries.ITEMS.getValue(which);
    }

    public static Block getBlockOrCrash(ResourceLocation which)
    {
        if (!ForgeRegistries.BLOCKS.containsKey(which))
            throw new RuntimeException(String.format("Attempted to make a block-placing item for '%s' without the associated block", which));
        //noinspection ConstantConditions
        return ForgeRegistries.BLOCKS.getValue(which);
    }

    public static <T> T orElse(@Nullable T val, T def)
    {
        return val != null ? val : def;
    }

    public static <T> T orElse(@Nullable T val, Supplier<T> def)
    {
        return val != null ? val : def.get();
    }

    public static <T> T getOrCrash(Registry<T> registry, String name)
    {
        T t = registry.get(new ResourceLocation(name));
        if (t == null)
            throw new IllegalStateException("No object with name " + name + " found in the registry " + registry);
        return t;
    }
}
