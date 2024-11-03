package dev.gigaherz.jsonthings.things.properties;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Function3;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class PropertyType
{
    public static Property<?> deserialize(String name, JsonObject data)
    {
        String key = GsonHelper.getAsString(data, "type");
        PropertyType prop = ThingRegistries.PROPERTY_TYPE.getOptional(ResourceLocation.parse(key))
                .orElseThrow(() -> new IllegalStateException("Property type not found " + key));
        return prop.read(name, data);
    }

    public static JsonObject serialize(Property<?> property)
    {
        for (Map.Entry<ResourceKey<PropertyType>, PropertyType> entry : ThingRegistries.PROPERTY_TYPE.entrySet())
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
        return "PropertyType{" + ThingRegistries.PROPERTY_TYPE.getKey(this) + "}";
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
                return EnumProperty.create(name, Direction.class, valid_values);
            }
            return EnumProperty.create(name, Direction.class);
        }

        @Override
        public void write(JsonObject data, Property<?> property)
        {
            //noinspection unchecked
            Collection<Direction> valid_values = ((EnumProperty<Direction>) property).getPossibleValues();
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
            String className = GsonHelper.getAsString(data, "class");

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

            if (!StringRepresentable.class.isAssignableFrom(cls))
            {
                throw new IllegalStateException("Enum type " + className + " not IStringSerializable");
            }

            List valid_values = Lists.newArrayList();
            if (data.has("values"))
            {
                Object[] enum_values = cls.getEnumConstants();
                StringRepresentable[] serializables = Arrays.stream(enum_values).map(s -> (StringRepresentable) s).toArray(StringRepresentable[]::new);

                JsonArray values = data.get("values").getAsJsonArray();
                for (JsonElement e : values)
                {
                    String val = e.getAsJsonPrimitive().getAsString();
                    for (StringRepresentable s : serializables)
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
                valid_values.stream().map(s -> ((StringRepresentable) s).getSerializedName()).forEach(list::add);
                data.add("values", list);
            }
            data.addProperty("class", cls.getName());
        }
    }
}
