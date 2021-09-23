package gigaherz.jsonthings.things.parsers;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.JsonThings;
import gigaherz.jsonthings.things.ThingRegistries;
import gigaherz.jsonthings.things.builders.BlockBuilder;
import gigaherz.jsonthings.things.builders.ItemBuilder;
import gigaherz.jsonthings.things.properties.PropertyType;
import gigaherz.jsonthings.things.serializers.MaterialColors;
import gigaherz.jsonthings.things.shapes.DynamicShape;
import gigaherz.jsonthings.util.parse.JParse;
import gigaherz.jsonthings.util.parse.value.Any;
import gigaherz.jsonthings.util.parse.value.ObjValue;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
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
        getBuilders().forEach(thing -> registry.register(thing.get().self().setRegistryName(thing.getRegistryName())));
        LOGGER.info("Done processing thingpack Blocks.");
    }

    @Override
    public BlockBuilder processThing(ResourceLocation key, JsonObject data)
    {
        final BlockBuilder builder = BlockBuilder.begin(key, data);

        MutableObject<Map<String, Property<?>>> propertiesByName = new MutableObject<>();
        MutableObject<Property<Direction>> facingProperty = new MutableObject<>();

        JParse.begin(data)
                .obj()
                .ifKey("parent", val -> val.string().map(ResourceLocation::new).handle(builder::setParentBlock))
                .ifKey("type", val -> val.string().map(ResourceLocation::new).handle(builder::setBlockType))
                .ifKey("material", val -> val.string().map(ResourceLocation::new).handle(builder::setMaterial))
                .ifKey("map_color", val -> val
                        .ifString(str -> builder.setColor(MaterialColors.get(str.getAsString())))
                        .ifInteger(str -> builder.setColor(MaterialColor.MATERIAL_COLORS[str.range(0,64).getAsInt()]))
                        .typeError()
                )
                .ifKey("requires_tool_for_drops", val -> val.bool().handle(builder::setRequiresToolForDrops))
                .ifKey("is_air", val -> val.bool().handle(builder::setIsAir))
                .ifKey("has_collision", val -> val.bool().handle(builder::setHasCollision))
                .ifKey("ticks_randomly", val -> val.bool().handle(builder::setTicksRandom))
                .ifKey("light_emission", val -> val.intValue().range(0,16).handle(builder::setLightEmission))
                .ifKey("explosion_resistance", val -> val.intValue().min(0).handle(builder::setExplosionResistance))
                .ifKey("destroy_time", val -> val.intValue().min(0).handle(builder::setDestroyTime))
                .ifKey("friction", val -> val.floatValue().range(0,1).handle(builder::setFriction))
                .ifKey("speed_factor", val -> val.floatValue().range(0,1).handle(builder::setSpeedFactor))
                .ifKey("jump_factor", val -> val.floatValue().range(0,1).handle(builder::setJumpFactor))
                .ifKey("sound_type", val -> val.string().map(ResourceLocation::new).handle(builder::setSoundType))
                .ifKey("properties", val -> val.obj().map(this::parseProperties).handle(builder::setProperties))
                .ifKey("default_state", val -> val.obj().raw(obj -> parseBlockState(obj, builder)))
                .ifKey("shape_rotation", val -> val.string().handle(name -> {
                    Property<?> prop = getRotationProperty(propertiesByName, name);
                    //noinspection unchecked
                    facingProperty.setValue((Property<Direction>) prop);
                }))
                .ifKey("shape", val -> val.raw(obj -> builder.setGeneralShape(parseShape(obj, facingProperty.getValue(), propertiesByName.getValue()))))
                .ifKey("collision_shape", val -> val.raw(obj -> builder.setCollisionShape(parseShape(obj, facingProperty.getValue(), propertiesByName.getValue()))))
                .ifKey("raytrace_shape", val -> val.raw(obj -> builder.setRaytraceShape(parseShape(obj, facingProperty.getValue(), propertiesByName.getValue()))))
                .ifKey("render_shape", val -> val.raw(obj -> builder.setRenderShape(parseShape(obj, facingProperty.getValue(), propertiesByName.getValue()))))
                .ifKey("render_layer", val -> val.map(this::parseRenderLayers).handle(builder::setRenderLayers))
                .ifKey("not_solid", val -> val.bool().handle(builder::setSeeThrough))
                .ifKey("color_handler", val -> val.string().handle(builder::setColorHandler))
                .ifKey("item", val -> val
                                .ifBool(v -> v.handle(b -> { if (b) createStockItemBlock(builder);}))
                                .ifObj(obj -> obj.map((JsonObject item) -> JsonThings.itemParser.parseFromElement(builder.getRegistryName(), item).withType("block", item)).handle(builder::withItem) )
                        );

        return builder;
    }

    private Property<?> getRotationProperty(MutableObject<Map<String, Property<?>>> propertiesByName, String name)
    {
        Property<?> prop = propertiesByName.getValue().get(name);
        if (prop == null)
            throw new IllegalStateException("No property with name '" + name + "' declared in block.");
        if (prop.getValueClass() != Direction.class)
            throw new IllegalStateException("The specified shape_rotation property is not a Direction property.");
        return prop;
    }

    private Set<String> parseRenderLayers(Any data)
    {
        Set<String> types = Sets.newHashSet();
        data    .ifString(str -> str.handle(name -> types.add(verifyRenderLayer(name))))
                .ifArray(arr -> arr.forEach((i,val) -> types.add(verifyRenderLayer(val.string().getAsString()))))
                .typeError();
        return types;
    }

    private static final Set<String> VALID_BLOCK_LAYERS = Sets.newHashSet("solid", "cutout_mipped", "cutout", "translucent", "tripwire");

    private String verifyRenderLayer(String layerName)
    {
        if (!VALID_BLOCK_LAYERS.contains(layerName))
            throw new IllegalStateException("Render layer " + layerName + " is not a valid block chunk layer.");
        return layerName;
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

    private void parseBlockState(JsonObject props, BlockBuilder builder)
    {
        for (Map.Entry<String, JsonElement> entry : props.entrySet())
        {
            String name = entry.getKey();
            JsonElement value = entry.getValue();
            builder.withDefaultState(name, value.getAsString());
        }
    }

    private Map<String, Property<?>> parseProperties(ObjValue props)
    {
        Map<String, Property<?>> map = new HashMap<>();

        props.forEach((name,val) -> val
                .ifString(str -> str.handle(prop -> {
                    var property = ThingRegistries.PROPERTIES.get(new ResourceLocation(prop));
                    if (property == null)
                        throw new IllegalStateException("Property with name " + prop + " not found in ThingRegistries.PROPERTIES");
                    if (!property.getName().equals(name))
                        throw new IllegalStateException("The stock property '" + prop + "' does not have the expected name '" + name + "' != '" + property.getName() + "'");
                    map.put(name, property);
                }))
                .ifObj(obj -> obj.raw(rawObj -> map.put(name, PropertyType.deserialize(name, rawObj))))
                .typeError());
        return map;
    }

    private void createStockItemBlock(BlockBuilder builder)
    {
        ItemBuilder itemBuilder = JsonThings.itemParser.parseFromElement(builder.getRegistryName(), new JsonObject()).withType("block", new JsonObject());
        builder.withItem(itemBuilder);
    }
}
