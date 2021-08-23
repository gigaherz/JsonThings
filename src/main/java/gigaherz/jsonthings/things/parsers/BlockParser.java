package gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.JsonThings;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.builders.BlockBuilder;
import gigaherz.jsonthings.things.builders.ItemBuilder;
import gigaherz.jsonthings.things.properties.PropertyType;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class BlockParser extends ThingParser<BlockBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public BlockParser(IEventBus bus)
    {
        super(GSON, "block");

        bus.addGenericListener(Block.class, this::registerBlocks);
    }

    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        LOGGER.info("Started registering Block things, errors about unexpected registry domains are harmless...");
        IForgeRegistry<Block> registry = event.getRegistry();
        getBuilders().forEach(thing -> registry.register(thing.build().self().setRegistryName(thing.getRegistryName())));
        LOGGER.info("Done processing thingpack Blocks.");
    }

    @Override
    public BlockBuilder processThing(ResourceLocation key, JsonObject data)
    {
        BlockBuilder builder = BlockBuilder.begin(key, GsonHelper.getAsString(data, "type", "plain"), data);

        if (data.has("parent"))
            builder = parseParent(data.get("parent"), builder);

        if (data.has("material"))
            builder = builder.withMaterial(data.get("material").getAsString());

        if (data.has("map_color"))
            builder = builder.withMaterialColor(data.get("map_color").getAsString());

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
            //noinspection unchecked
            facingProperty = (Property<Direction>) prop;
        }

        if (data.has("shape"))
            builder = builder.withGeneralShape(parseShape(data.get("shape"), facingProperty, propertiesByName));

        if (data.has("collision_shape"))
            builder = builder.withCollisionShape(parseShape(data.get("collision_shape"), facingProperty, propertiesByName));

        if (data.has("raytrace_shape"))
            builder = builder.withRaytraceShape(parseShape(data.get("raytrace_shape"), facingProperty, propertiesByName));

        if (data.has("render_shape"))
            builder = builder.withRenderShape(parseShape(data.get("render_shape"), facingProperty, propertiesByName));

        if (data.has("render_layer"))
            builder = builder.withRenderLayers(parseRenderLayers(data.get("render_layer")));

        if (data.has("not_solid"))
            builder = builder.withSeeThrough(data.get("not_solid").getAsBoolean());

        if (data.has("color_handler"))
            builder = builder.withColorHandler(data.get("color_handler").getAsString());

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

        return builder;
    }

    private static final Set<String> validBlockLayers = Sets.newHashSet("solid", "cutout_mipped", "cutout", "translucent", "tripwire");

    private Set<String> parseRenderLayers(JsonElement data)
    {
        Set<String> types = Sets.newHashSet();
        if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isString())
        {
            types.add(verifyRenderLayer(data.getAsString()));
        }
        else
        {
            for (JsonElement e : data.getAsJsonArray())
            {
                types.add(verifyRenderLayer(e.getAsString()));
            }
        }
        return types;
    }

    private String verifyRenderLayer(String layerName)
    {
        if (!validBlockLayers.contains(layerName))
            throw new IllegalStateException("Render layer " + layerName + " is not a valid block chunk layer.");
        return layerName;
    }

    private BlockBuilder parseParent(JsonElement data, BlockBuilder builder)
    {
        if (data.isJsonObject())
        {
            JsonObject obj = data.getAsJsonObject();
            String id = GsonHelper.getAsString(obj, "id");
            boolean isBuilder = GsonHelper.getAsBoolean(obj, "is_builder", true);
            if (isBuilder)
                return builder.withParentBuilder(new ResourceLocation(id));
            else
                return builder.withParentBlock(new ResourceLocation(id));
        }
        return builder.withParentBuilder(new ResourceLocation(data.getAsString()));
    }

    private DynamicShape parseShape(JsonElement element, @Nullable Property<Direction> facingProperty, Map<String, Property<?>> propertiesByName)
    {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
        {
            String name = element.getAsString();
            DynamicShape shape = ThingRegistries.DYNAMIC_SHAPES.get(new ResourceLocation(name));
            if (shape == null)
                throw new IllegalStateException("No shape known with name " + name);
            return shape;
        }
        else
        {
            return DynamicShape.fromJson(element, facingProperty, propertiesByName::get);
        }
    }

    private BlockBuilder parseBlockState(JsonObject props, BlockBuilder builder)
    {
        for (Map.Entry<String, JsonElement> entry : props.entrySet())
        {
            String name = entry.getKey();
            JsonElement value = entry.getValue();
            builder = builder.withDefaultState(name, value.getAsString());
        }
        return builder;
    }

    private BlockBuilder parseProperties(JsonObject props, BlockBuilder builder)
    {
        for (Map.Entry<String, JsonElement> entry : props.entrySet())
        {
            String name = entry.getKey();
            JsonElement value = entry.getValue();
            Property<?> property;
            if (value.isJsonPrimitive())
            {
                property = ThingRegistries.PROPERTIES.get(new ResourceLocation(value.getAsString()));
                if (property == null)
                    throw new IllegalStateException("Property with name " + value + " not found in ThingRegistries.PROPERTIES");
                if (!property.getName().equals(name))
                    throw new IllegalStateException("The stock property '" + value.getAsString() + "' does not have the expected name '" + name + "' != '" + property.getName() + "'");
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
        ItemBuilder itemBuilder = JsonThings.itemParser.parseFromElement(builder.getRegistryName(), new JsonObject()).withType("block", new JsonObject());
        return builder.withItem(itemBuilder);
    }

    private BlockBuilder parseItemBlock(JsonObject data, BlockBuilder builder)
    {
        ItemBuilder itemBuilder = JsonThings.itemParser.parseFromElement(builder.getRegistryName(), data).withType("block", data);
        return builder.withItem(itemBuilder);
    }
}
