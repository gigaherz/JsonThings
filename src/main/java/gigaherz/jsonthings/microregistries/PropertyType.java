package gigaherz.jsonthings.microregistries;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Function3;
import net.minecraft.state.*;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JSONUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class PropertyType<T extends Comparable<T>, P extends Property<T>>
{
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends Comparable<T>, P extends Property<T>> P deserialize(String name, JsonObject data)
    {
        String key = JSONUtils.getString(data, "type");
        PropertyType prop = ThingsByName.PROPERTY_TYPES.get(key);
        return (P)prop.read(name, data);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends Comparable<T>, P extends Property<T>> JsonObject serialize(P property)
    {
        for(Map.Entry<String, PropertyType> entry : ThingsByName.PROPERTY_TYPES.entrySet())
        {
            String key = entry.getKey();
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
    public abstract P read(String name, JsonObject data);
    public abstract void write(JsonObject data, P property);

    public static class BoolType extends PropertyType<Boolean, BooleanProperty>
    {
        @Override
        public boolean handles(Property<?> property)
        {
            return property instanceof BooleanProperty;
        }

        @Override
        public BooleanProperty read(String name, JsonObject data)
        {
            return BooleanProperty.create(name);
        }

        @Override
        public void write(JsonObject data, BooleanProperty property)
        {
            // Nothing to do
        }
    }

    public static class RangeType<T extends Comparable<T>, P extends Property<T>> extends PropertyType<T,P>
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
        public P read(String name, JsonObject data)
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
        public void write(JsonObject data, P property)
        {
            property.getAllowedValues().stream().min(Comparable::compareTo)
                    .ifPresent(v -> data.addProperty("min", v.toString()));
            property.getAllowedValues().stream().max(Comparable::compareTo)
                    .ifPresent(v -> data.addProperty("max", v.toString()));
        }
    }

    public static class DirectionType extends PropertyType<Direction, DirectionProperty>
    {
        @Override
        public boolean handles(Property<?> property)
        {
            return property instanceof BooleanProperty;
        }

        @Override
        public DirectionProperty read(String name, JsonObject data)
        {
            List<Direction> valid_values = Lists.newArrayList();
            if (data.has("values"))
            {
                JsonArray values = data.get("values").getAsJsonArray();
                for(JsonElement e : values)
                {
                    String val = e.getAsJsonPrimitive().getAsString();
                    valid_values.add(Direction.byName(val));
                }
                return DirectionProperty.create(name, valid_values);
            }
            return DirectionProperty.create(name);
        }

        @Override
        public void write(JsonObject data, DirectionProperty property)
        {
            Collection<Direction> valid_values = property.getAllowedValues();
            Direction[] values = Direction.values();
            if (values.length > valid_values.size())
            {
                JsonArray list = new JsonArray();
                valid_values.stream().map(Direction::getString).forEach(list::add);
                data.add("values", list);
            }
        }
    }

    public static class EnumType<T extends Enum<T> & IStringSerializable> extends PropertyType<T, EnumProperty<T>>
    {
        @Override
        public boolean handles(Property<?> property)
        {
            return property instanceof EnumProperty;
        }

        @Override
        public EnumProperty read(String name, JsonObject data)
        {
            String className = JSONUtils.getString(data, "class");

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
                IStringSerializable[] serializables = Arrays.stream(enum_values).map(s -> (IStringSerializable)s).toArray(IStringSerializable[]::new);

                JsonArray values = data.get("values").getAsJsonArray();
                for(JsonElement e : values)
                {
                    String val = e.getAsJsonPrimitive().getAsString();
                    for(IStringSerializable s : serializables)
                    {
                        if (s.getString().equals(val))
                        {
                            valid_values.add(s);
                        }
                    }
                }
            }

            return EnumProperty.create(name, cls, valid_values);
        }

        @Override
        public void write(JsonObject data, EnumProperty<T> property)
        {
            Collection<T> valid_values = property.getAllowedValues();
            Class<?> cls = valid_values.stream().findFirst().get().getClass();
            Object[] enum_values = cls.getEnumConstants();
            if (enum_values.length > valid_values.size())
            {
                JsonArray list = new JsonArray();
                valid_values.stream().map(s -> ((IStringSerializable)s).getString()).forEach(list::add);
                data.add("values", list);
            }
            data.addProperty("class", cls.getName());
        }
    }
}
