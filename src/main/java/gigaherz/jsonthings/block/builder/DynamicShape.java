package gigaherz.jsonthings.block.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import gigaherz.jsonthings.microregistries.ThingsByName;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import javax.annotation.Nullable;
import java.util.*;

public class DynamicShape
{
    private static final DynamicShape EMPTY = new DynamicShape((state, facing) -> Optional.empty(), null);

    public static DynamicShape empty()
    {
        return EMPTY;
    }

    @FunctionalInterface
    public interface IShapeProvider {
        Optional<VoxelShape> getShape(BlockState state, Direction facing);
    }

    public static class BasicShape implements IShapeProvider {
        public final double x1;
        public final double y1;
        public final double z1;
        public final double x2;
        public final double y2;
        public final double z2;

        public BasicShape(double x1, double y1, double z1, double x2, double y2, double z2)
        {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }

        @Override
        public Optional<VoxelShape> getShape(BlockState state, Direction facing)
        {
            return Optional.of(cuboidWithRotation(facing, x1, y1, z1, x2, y2, z2));
        }
    }

    public static class CombinedShape implements IShapeProvider {
        public final IBooleanFunction operator;
        public final List<IShapeProvider> boxes = Lists.newArrayList();

        public CombinedShape(IBooleanFunction operator, Collection<IShapeProvider> boxes)
        {
            this.operator = operator;
            this.boxes.addAll(boxes);
        }

        @Override
        public Optional<VoxelShape> getShape(BlockState state, Direction facing)
        {
            return boxes.stream()
                    .map(shape -> shape.getShape(state, facing))
                    .reduce(Optional.empty(), (a,b) -> a.map(aa -> b.map(bb -> VoxelShapes.combine(aa, bb, operator))).orElse(b))
                    .map(VoxelShape::simplify);
        }
    }

    public static class ConditionalShape implements IShapeProvider {
        public final List<List<Pair<Property<?>, Set<Comparable<?>>>>> conditions = Lists.newArrayList();
        public final IShapeProvider shape;

        public ConditionalShape(Collection<List<Pair<Property<?>, Set<Comparable<?>>>>> conditions, IShapeProvider shape)
        {
            this.conditions.addAll(conditions);
            this.shape = shape;
        }

        @Override
        public Optional<VoxelShape> getShape(BlockState state, Direction facing)
        {
            return conditions.stream().anyMatch(condition -> condition.stream().allMatch(p -> p.getSecond().contains(state.get(p.getFirst()))))
                    ? shape.getShape(state, facing)
                    : Optional.empty();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static VoxelShape cuboidWithRotation(Direction facing, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        switch (facing)
        {
            case NORTH:
                return VoxelShapes.create(x1, y1, z1, x2, y2, z2);
            case SOUTH:
                return VoxelShapes.create(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
            case WEST:
                return VoxelShapes.create(z1, y1, 1 - x2, z2, y2, 1 - x1);
            case EAST:
                return VoxelShapes.create(1 - z2, y1, x1, 1 - z1, y2, x2);
            case UP:
                return VoxelShapes.create(1 - y1,x1,z1, 1 - y2,x2,z2);
            case DOWN:
                return VoxelShapes.create(y1,1 - x1,z1, y2,1 - x2,z2);
        }
        return VoxelShapes.create(x1, y1, z1, x2, y2, z2);
    }

    private final Map<BlockState, VoxelShape> shapeCache = new IdentityHashMap<>();
    private final IShapeProvider shape;
    @Nullable
    private final Property<Direction> facing;

    public DynamicShape(IShapeProvider shape, @Nullable Property<Direction> facing)
    {
        this.shape = shape;
        this.facing = facing;
    }

    public VoxelShape getShape(BlockState blockstate)
    {
        return shapeCache.computeIfAbsent(blockstate, state -> {
            Direction d = facing != null ? state.get(facing) : Direction.NORTH;
            return shape.getShape(state, d).orElseGet(VoxelShapes::fullCube);
        });
    }

    // ------------- PARSER ------------------

    public static DynamicShape fromJson(JsonElement data, @Nullable Property<Direction> facingProperty, Map<String, Property<?>> properties)
    {
        IShapeProvider shape = deserializeShape(data, properties);
        return new DynamicShape(shape, facingProperty);
    }

    private static IShapeProvider deserializeShape(JsonElement data, Map<String, Property<?>> properties)
    {
        if (data.isJsonObject())
        {
            JsonObject obj = data.getAsJsonObject();
            if (obj.has("when"))
            {
                return parseConditionalShape(obj, properties);
            }
            else
            {
                return deserializeCombinedShape(obj, properties);
            }
        }
        else
        {
            JsonArray array = data.getAsJsonArray();
            boolean isBasic = array.size() == 0 || array.get(0).isJsonPrimitive() && array.get(0).getAsJsonPrimitive().isNumber();
            if (isBasic)
            {
                return deserializeBox(array);
            }
            else
            {
                return deserializeCombinedShape(IBooleanFunction.OR, array, properties);
            }
        }
    }

    private static IShapeProvider deserializeBox(JsonObject obj)
    {
        double x1 = obj.get("x1").getAsJsonPrimitive().getAsDouble()/ 16.0;
        double y1 = obj.get("y1").getAsJsonPrimitive().getAsDouble()/ 16.0;
        double z1 = obj.get("z1").getAsJsonPrimitive().getAsDouble()/ 16.0;
        double x2 = obj.get("x2").getAsJsonPrimitive().getAsDouble()/ 16.0;
        double y2 = obj.get("y2").getAsJsonPrimitive().getAsDouble()/ 16.0;
        double z2 = obj.get("z2").getAsJsonPrimitive().getAsDouble()/ 16.0;
        return new BasicShape(x1, y1, z1, x2, y2, z2);
    }

    private static IShapeProvider deserializeBox(JsonArray array)
    {
        double x1 = array.get(0).getAsJsonPrimitive().getAsDouble()/ 16.0;
        double y1 = array.get(1).getAsJsonPrimitive().getAsDouble()/ 16.0;
        double z1 = array.get(2).getAsJsonPrimitive().getAsDouble()/ 16.0;
        double x2 = array.get(3).getAsJsonPrimitive().getAsDouble()/ 16.0;
        double y2 = array.get(4).getAsJsonPrimitive().getAsDouble()/ 16.0;
        double z2 = array.get(5).getAsJsonPrimitive().getAsDouble()/ 16.0;
        return new BasicShape(x1, y1, z1, x2, y2, z2);
    }

    private static ConditionalShape parseConditionalShape(JsonObject obj, Map<String, Property<?>> properties)
    {
        List<List<Pair<Property<?>, Set<Comparable<?>>>>> conditions = deserializeConditions(obj.get("when"), properties);
        IShapeProvider shape;
        if (obj.has("op"))
        {
            shape = deserializeCombinedShape(obj, properties);
        }
        else
        {
            shape = deserializeShape(obj.get("shape"), properties);
        }
        return new ConditionalShape(conditions, shape);
    }

    private static List<List<Pair<Property<?>, Set<Comparable<?>>>>> deserializeConditions(JsonElement when, Map<String, Property<?>> properties)
    {
        if (when.isJsonArray())
        {
            List<List<Pair<Property<?>, Set<Comparable<?>>>>> list = Lists.newArrayList();
            JsonArray arr = when.getAsJsonArray();
            for(JsonElement element : arr)
            {
                list.add(deserializeSingleCondition(element.getAsJsonObject(), properties));
            }
            return list;
        } else {
            return Collections.singletonList(deserializeSingleCondition(when.getAsJsonObject(), properties));
        }
    }

    private static List<Pair<Property<?>, Set<Comparable<?>>>> deserializeSingleCondition(JsonObject obj, Map<String, Property<?>> properties)
    {
        List<Pair<Property<?>, Set<Comparable<?>>>> list = Lists.newArrayList();
        for(Map.Entry<String, JsonElement> entry : obj.entrySet())
        {
            String key = entry.getKey();
            Property<?> property = properties.get(key);
            if (property == null)
                throw new IllegalStateException("Property " + key + " not declared in the block.");
            JsonElement values = entry.getValue();
            if (values.isJsonArray())
            {
                Set<Comparable<?>> propertyValues = Sets.newHashSet();
                for(JsonElement e : values.getAsJsonArray())
                {
                    String value = e.getAsString();
                    propertyValues.add(parsePropertyValue(property, value));
                }
                list.add(Pair.of(property, propertyValues));
            }
            else
            {
                String value = values.getAsString();
                list.add(Pair.of(property, Collections.singleton(parsePropertyValue(property, value))));
            }
        }
        return list;
    }

    private static Comparable<?> parsePropertyValue(Property<?> property, String value)
    {
        return property.parseValue(value).orElseThrow(() -> new IllegalStateException("Value '" + value + "' is not valid for property " + property.getName()));
    }

    private static IShapeProvider deserializeCombinedShape(JsonObject obj, Map<String, Property<?>> properties)
    {
        if (!obj.has("op"))
        {
            return deserializeBox(obj);
        }

        String op = obj.get("op").getAsString();
        IBooleanFunction operator = ThingsByName.BOOLEAN_FUNCTIONS.get(op);

        if (!obj.has("shapes"))
            throw new IllegalStateException("Expected value with name 'shapes'.");

        JsonArray shapesArray = obj.get("shapes").getAsJsonArray();

        return deserializeCombinedShape(operator, shapesArray, properties);
    }

    private static CombinedShape deserializeCombinedShape(IBooleanFunction operator, JsonArray shapesArray, Map<String, Property<?>> properties)
    {
        List<IShapeProvider> shapes = Lists.newArrayList();
        for(JsonElement e : shapesArray)
        {
            shapes.add(deserializeShape(e, properties));
        }
        return new CombinedShape(operator, shapes);
    }
}
