package dev.gigaherz.jsonthings.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class Utils
{
    public static TagKey<Item> itemTag(String pName)
    {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(pName));
    }

    public static TagKey<Block> blockTag(String pName)
    {
        return TagKey.create(Registries.BLOCK, ResourceLocation.parse(pName));
    }

    public static <T extends Comparable<T>> T getPropertyValue(Property<T> prop, String value)
    {
        Optional<T> propValue = prop.getValue(value);
        return propValue.orElseThrow(() -> new KeyNotFoundException("Value " + value + " for property " + prop.getName() + " not found in the allowed values."));
    }

    @Nonnull
    public static <T> T orElse(@Nullable T val, T def)
    {
        return val != null ? val : def;
    }

    public static <T> T orElseGet(@Nullable T val, Supplier<T> def)
    {
        return val != null ? val : def.get();
    }

    public static <T> T getOrCrash(Registry<T> registry, ResourceLocation name)
    {
        T t = registry.get(name);
        if (t == null)
            throw new KeyNotFoundException("No object with name " + name + " found in the registry " + registry);
        return t;
    }

    public static <T> Holder<T> getHolderOrCrash(Registry<T> registry, ResourceLocation name)
    {
        return registry.getHolder(name).orElseThrow(() -> new NoSuchElementException("Entry with name " + name + " not found in registry " + registry.key().location()));
    }

    public static <T> T getOrElse(Registry<T> registry, ResourceLocation name, T fallback)
    {
        if (!registry.containsKey(name))
            return fallback;
        return Objects.requireNonNull(registry.get(name));
    }


    private static final Map<String, ArmorItem.Type> BACKWARD_COMPAT = ImmutableMap.<String, ArmorItem.Type>builder()
            .put("head", ArmorItem.Type.HELMET)
            .put("chest", ArmorItem.Type.CHESTPLATE)
            .put("legs", ArmorItem.Type.LEGGINGS)
            .put("feet", ArmorItem.Type.BOOTS)
        .build();

    public static ArmorItem.Type armorTypeByEquipmentSlotName(String name) {
        ArmorItem.Type backwardCompat = BACKWARD_COMPAT.get(name);

        if (backwardCompat != null)
            return backwardCompat;

        throw new IllegalArgumentException("Invalid armor type '" + name + "'");
    }

    public static ArmorItem.Type armorTypeByName(String name) {

        for(ArmorItem.Type equipmentslot : ArmorItem.Type.values()) {
            if (equipmentslot.getName().equals(name)) {
                return equipmentslot;
            }
        }

        throw new IllegalArgumentException("Invalid armor type '" + name + "'");
    }
}
