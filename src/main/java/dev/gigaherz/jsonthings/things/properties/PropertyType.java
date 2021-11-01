package dev.gigaherz.jsonthings.things.properties;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Function3;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.util.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class PropertyType
{
    public static Property<?> deserialize(String name, JsonObject data)
    {
        String key = JSONUtils.getAsString(data, "type");
        PropertyType prop = ThingRegistries.PROPERTY_TYPES.get(new ResourceLocation(key));
        if (prop == null)
            throw new IllegalStateException("Property type not found " + key);
        return prop.read(name, data);
    }

    public static JsonObject serialize(Property<?> property)
    {
        for (Map.Entry<RegistryKey<PropertyType>, PropertyType> entry : ThingRegistries.PROPERTY_TYPES.entrySet())
        {
            String key = entry.getKey().location().toString();
            PropertyType prop = entry.getValue();
            if (prop.handles(property))
            {
                JsonObject data = new JsonObject();
                prop.write(data, property);
                data.addProperty("type", key);
                return data;
            }
        }
        throw new IllegalStateException("No serializer can handle the given property " + property);
    }

    public abstract boolean handles(Property<?> property);

    public abstract Property<?> read(String name, JsonObject data);

    public abstract void write(JsonObject data, Property<?> property);

    public String toString()
    {
        return "PropertyType{" + ThingRegistries.PROPERTY_TYPES.getKey(this) + "}";
    }

    public static class BoolType extends PropertyType
    {
        @Override
        public boolean handles(Property<?> property)
        {
            return property instanceof BooleanProperty;
        }

        @Override
        public Property<?> read(String name, JsonObject data)
        {
            return BooleanProperty.create(name);
        }

        @Override
        public void write(JsonObject data, Property<?> property)
        {
            // Nothing to do
        }
    }

    public static class RangeType<T extends Comparable<T>, P extends Property<T>> extends PropertyType
    {
        private final Class<P> cls;
        private final Function3<String, T, T, P> factory;
        private final Function<JsonElement, T> parseBound;

        public RangeType(Class<P> cls, Function3<String, T, T, P> factory, Function<JsonElement, T> parseBound)
        {
            this.cls = cls;
            this.factory = factory;
            this.parseBound = parseBound;
        }

        @Override
        public boolean handles(Property<?> property)
        {
            return cls.isInstance(property);
        }

        @Override
        public Property<?> read(String name, JsonObject data)
        {
            if (!data.has("min"))
                throw new IllegalStateException("Requires a value 'min' of the right type.");
            if (!data.has("max"))
                throw new IllegalStateException("Requires a value 'max' of the right type.");
            T min = parseBound.apply(data.get("min"));
            T max = parseBound.apply(data.get("max"));
            return factory.apply(name, min, max);
        }

        @Override
        public void write(JsonObject data, Property<?> property)
        {
            property.getPossibleValues().stream().min(Comparable::compareTo)
                    .ifPresent(v -> data.addProperty("min", v.toString()));
            property.getPossibleValues().stream().max(Comparable::compareTo)
                    .ifPresent(v -> data.addProperty("max", v.toString()));
        }
    }

    public static class StringType extends PropertyType
    {
        @Override
        public boolean handles(Property<?> property)
        {
            return property instanceof CustomProperty;
        }

        @Override
        public Property<?> read(String name, JsonObject data)
        {
            List<String> valid_values = Lists.newArrayList();
            if (data.has("values"))
            {
                JsonArray values = data.get("values").getAsJsonArray();
                for (JsonElement e : values)
                {
                    String val = e.getAsJsonPrimitive().getAsString();
                    valid_values.add(val);
                }
                return CustomProperty.create(name, valid_values);
            }
            return CustomProperty.create(name);
        }

        @Override
        public void write(JsonObject data, Property<?> property)
        {
            Collection<String> valid_values = ((CustomProperty) property).getPossibleValues();
            JsonArray list = new JsonArray();
            valid_values.forEach(list::add);
            data.add("values", list);
        }
    }

    public static class DirectionType extends PropertyType
    {
        @Override
        public boolean handles(Property<?> property)
        {
            return property instanceof BooleanProperty;
        }

        @Override
        public Property<?> read(String name, JsonObject data)
        {
            List<Direction> valid_values = Lists.newArrayList();
            if (data.has("values"))
            {
                JsonArray values = data.get("values").getAsJsonArray();
                for (JsonElement e : values)
                {
                    String val = e.getAsJsonPrimitive().getAsString();
                    valid_values.add(Direction.byName(val));
                }
                return DirectionProperty.create(name, valid_values);
            }
            return DirectionProperty.create(name);
        }

        @Override
        public void write(JsonObject data, Property<?> property)
        {
            Collection<Direction> valid_values = ((DirectionProperty) property).getPossibleValues();
            Direction[] values = Direction.values();
            if (values.length > valid_values.size())
            {
                JsonArray list = new JsonArray();
                valid_values.stream().map(Direction::getSerializedName).forEach(list::add);
                data.add("values", list);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class EnumType extends PropertyType
    {
        @Override
        public boolean handles(Property<?> property)
        {
            return property instanceof EnumProperty;
        }

        @Override
        public Property<?> read(String name, JsonObject data)
        {
            String className = JSONUtils.getAsString(data, "class");

            Class cls;
            try
            {
                cls = Class.forName(className);
            }
            catch (ClassNotFoundException e)
            {
                throw new IllegalStateException("Error getting class " + className, e);
            }

            if (!cls.isEnum())
            {
                throw new IllegalStateException("Not an enum type " + className);
            }

            if (!IStringSerializable.class.isAssignableFrom(cls))
            {
                throw new IllegalStateException("Enum type " + className + " not IStringSerializable");
            }

            List valid_values = Lists.newArrayList();
            if (data.has("values"))
            {
                Object[] enum_values = cls.getEnumConstants();
                IStringSerializable[] serializables = Arrays.stream(enum_values).map(s -> (IStringSerializable) s).toArray(IStringSerializable[]::new);

                JsonArray values = data.get("values").getAsJsonArray();
                for (JsonElement e : values)
                {
                    String val = e.getAsJsonPrimitive().getAsString();
                    for (IStringSerializable s : serializables)
                    {
                        if (s.getSerializedName().equals(val))
                        {
                            valid_values.add(s);
                        }
                    }
                }
            }

            return EnumProperty.create(name, cls, valid_values);
        }

        @Override
        public void write(JsonObject data, Property<?> property)
        {
            Collection<?> valid_values = property.getPossibleValues();
            Class<?> cls = valid_values.stream().findFirst().get().getClass();
            Object[] enum_values = cls.getEnumConstants();
            if (enum_values.length > valid_values.size())
            {
                JsonArray list = new JsonArray();
                valid_values.stream().map(s -> ((IStringSerializable) s).getSerializedName()).forEach(list::add);
                data.add("values", list);
            }
            data.addProperty("class", cls.getName());
        }
    }
}
