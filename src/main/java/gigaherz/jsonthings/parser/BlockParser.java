package gigaherz.jsonthings.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.block.builder.BlockBuilder;
import gigaherz.jsonthings.block.builder.DynamicShape;
import gigaherz.jsonthings.item.builder.ItemBuilder;
import gigaherz.jsonthings.microregistries.PropertyType;
import gigaherz.jsonthings.microregistries.ThingsByName;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockParser extends ThingParser<BlockBuilder>
{
    public BlockParser()
    {
        super(GSON, "block");
    }

    @Override
    public BlockBuilder processThing(ResourceLocation key, JsonObject data)
    {
        BlockBuilder builder = BlockBuilder.begin(key);

        if (data.has("item"))
        {
            JsonElement item = data.get("item");
            if (item.isJsonPrimitive())
            {
                if (item.getAsBoolean())
                {
                    builder = createStockItemBlock(builder);
                }
            }
            else if (item.isJsonObject())
            {
                builder = parseItemBlock(data.get("item").getAsJsonObject(), builder);
            }
            else
            {
                throw new RuntimeException("If present, 'item' must be a boolean or an object declaring the item values.");
            }
        }

        if (data.has("properties"))
        {
            JsonObject props = data.get("properties").getAsJsonObject();
            builder = parseProperties(props, builder);
        }

        if (data.has("default_state"))
        {
            JsonObject props = data.get("default_state").getAsJsonObject();
            builder = parseBlockState(props, builder);
        }

        Map<String, Property<?>> propertiesByName = builder.getPropertiesByName();
        Property<Direction> facingProperty = null;
        if (data.has("shape_rotation"))
        {
            String name = data.get("shape_rotation").getAsString();
            Property<?> prop = propertiesByName.get(name);
            if (prop == null)
                throw new IllegalStateException("No property with name '" + name + "' declared in block.");
            if (prop.getValueClass() != Direction.class)
                throw new IllegalStateException("The specified shape_rotation property is not a Direction property.");
            facingProperty = (Property<Direction>)prop;
        }

        if (data.has("shape"))
            builder = builder.withGeneralShape(parseShape(data.get("shape"), facingProperty, propertiesByName));

        if (data.has("collision_shape"))
            builder = builder.withCollisionShape(parseShape(data.get("collision_shape"), facingProperty, propertiesByName));

        if (data.has("raytrace_shape"))
            builder = builder.withRaytraceShape(parseShape(data.get("raytrace_shape"), facingProperty, propertiesByName));

        if (data.has("render_shape"))
            builder = builder.withRenderShape(parseShape(data.get("render_shape"), facingProperty, propertiesByName));

        return builder;
    }

    private DynamicShape parseShape(JsonElement element, @Nullable Property<Direction> facingProperty, Map<String, Property<?>> propertiesByName)
    {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
        {
            String name = element.getAsString();
            DynamicShape shape = ThingsByName.DYNAMIC_SHAPES.get(name);
            if (shape == null)
                throw new IllegalStateException("No shape known with name " + name);
            return shape;
        }
        else
        {
            return DynamicShape.fromJson(element, facingProperty, propertiesByName);
        }
    }

    private BlockBuilder parseBlockState(JsonObject props, BlockBuilder builder)
    {
        for(Map.Entry<String, JsonElement> entry : props.entrySet())
        {
            String name = entry.getKey();
            JsonElement value = entry.getValue();
            builder = builder.withDefaultState(name, value.getAsString());
        }
        return builder;
    }

    private BlockBuilder parseProperties(JsonObject props, BlockBuilder builder)
    {
        for(Map.Entry<String, JsonElement> entry : props.entrySet())
        {
            String name = entry.getKey();
            JsonElement value = entry.getValue();
            Property<?> property;
            if (value.isJsonPrimitive())
            {
                property = ThingsByName.PROPERTIES.get(value.getAsString());
                if (!property.getName().equals(name))
                {
                    throw new IllegalStateException("The stock property '" + value.getAsString() + "' does not have the expected name '" + name + "' != '" + property.getName() + "'");
                }
            }
            else
            {
                property = PropertyType.deserialize(name, value.getAsJsonObject());
            }
            builder = builder.withProperty(property);
        }
        return builder;
    }

    private BlockBuilder createStockItemBlock(BlockBuilder builder)
    {
        ItemBuilder itemBuilder = ThingResourceManager.INSTANCE.itemParser.parseFromElement(builder.getRegistryName(), new JsonObject()).makeBlock(builder.getRegistryName());
        return builder.withItem(itemBuilder);
    }

    private BlockBuilder parseItemBlock(JsonObject data, BlockBuilder builder)
    {
        ItemBuilder itemBuilder = ThingResourceManager.INSTANCE.itemParser.parseFromElement(builder.getRegistryName(), data).makeBlock(builder.getRegistryName());
        return builder.withItem(itemBuilder);
    }
}
