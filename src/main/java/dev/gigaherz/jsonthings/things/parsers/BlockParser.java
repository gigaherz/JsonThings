package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.IFlexBlock;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BlockBuilder;
import dev.gigaherz.jsonthings.things.properties.PropertyType;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.things.serializers.MapColors;
import dev.gigaherz.jsonthings.things.shapes.DynamicShape;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import dev.gigaherz.jsonthings.util.parse.value.ObjValue;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BlockParser extends ThingParser<IFlexBlock, BlockBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public BlockParser(IEventBus bus)
    {
        super(GSON, "block");

        register(bus, Registries.BLOCK, IFlexBlock::self);
    }

    @Override
    public BlockBuilder processThing(ResourceLocation key, JsonObject data, Consumer<BlockBuilder> builderModification)
    {
        final BlockBuilder builder = BlockBuilder.begin(this, key);

        MutableObject<Map<String, Property<?>>> propertiesByName = new MutableObject<>(new HashMap<>());
        MutableObject<Property<Direction>> facingProperty = new MutableObject<>();

        JParse.begin(data)
                .ifKey("parent", val -> val.string().map(ResourceLocation::parse).handle(builder::setParent))
                .ifKey("type", val -> val.string().map(ResourceLocation::parse).handle(builder::setBlockType))
                .ifKey("map_color", val -> val
                        .ifString(str -> str.handle(name -> builder.setMaterialColor(MapColors.get(name))))
                        .ifInteger(num -> num.range(0, 64).handle(index -> builder.setMaterialColor(MapColor.MATERIAL_COLORS[index])))
                        .typeError()
                )
                .ifKey("requires_tool_for_drops", val -> val.bool().handle(builder::setRequiresToolForDrops))
                .ifKey("is_air", val -> val.bool().handle(builder::setIsAir))
                .ifKey("has_collision", val -> val.bool().handle(builder::setHasCollision))
                .ifKey("ticks_randomly", val -> val.bool().handle(builder::setTicksRandom))
                .ifKey("light_emission", val -> val.intValue().range(0, 16).handle(builder::setLightEmission))
                .ifKey("explosion_resistance", val -> val.floatValue().min(0).handle(builder::setExplosionResistance))
                .ifKey("destroy_time", val -> val.floatValue().min(0).handle(builder::setDestroyTime))
                .ifKey("friction", val -> val.floatValue().range(0, 1).handle(builder::setFriction))
                .ifKey("speed_factor", val -> val.floatValue().range(0, 1).handle(builder::setSpeedFactor))
                .ifKey("jump_factor", val -> val.floatValue().range(0, 1).handle(builder::setJumpFactor))
                .ifKey("sound_type", val -> val.string().map(ResourceLocation::parse).handle(builder::setSoundType))
                .ifKey("properties", val -> val.obj().map(this::parseProperties).handle(properties -> {
                    propertiesByName.setValue(properties);
                    builder.setProperties(properties);
                }))
                .ifKey("default_state", val -> val.obj().raw(obj -> parseBlockState(obj, builder)))
                .ifKey("shape_rotation", val -> val.string().handle(name -> {
                    Property<?> prop = getRotationProperty(propertiesByName, name);
                    //noinspection unchecked
                    facingProperty.setValue((Property<Direction>) prop);
                }))
                .ifKey("shape", val -> val.raw(obj -> builder.setGeneralShape(DynamicShape.parseShape(obj, facingProperty.getValue(), propertiesByName.getValue()))))
                .ifKey("collision_shape", val -> val.raw(obj -> builder.setCollisionShape(DynamicShape.parseShape(obj, facingProperty.getValue(), propertiesByName.getValue()))))
                .ifKey("raytrace_shape", val -> val.raw(obj -> builder.setRaytraceShape(DynamicShape.parseShape(obj, facingProperty.getValue(), propertiesByName.getValue()))))
                .ifKey("render_shape", val -> val.raw(obj -> builder.setRenderShape(DynamicShape.parseShape(obj, facingProperty.getValue(), propertiesByName.getValue()))))
                .ifKey("not_solid", val -> val.bool().handle(builder::setSeeThrough))
                .ifKey("color_handler", val -> val.string().handle(builder::setColorHandler))
                .ifKey("ignited_by_lava", val -> val.bool().handle(builder::setIgnitedByLava))
                .ifKey("force_solid", val -> val.bool().handle(builder::setForceSolid))
                .ifKey("replaceable", val -> val.bool().handle(builder::setReplaceable))
                .ifKey("blocks_motion", val -> val.bool().handle(builder::setBlocksMotion))
                .ifKey("push_reaction", val -> val.string().map(BlockParser::parsePushReaction).handle(builder::setPushReaction))
                .ifKey("item", val -> parseItemBlock(builder, val))
                .ifKey("events", val -> val.obj().map(this::parseEvents).handle(builder::setEventMap));

        builderModification.accept(builder);

        builder.setFactory(builder.getBlockType().getFactory(data));

        return builder;
    }

    private static PushReaction parsePushReaction(String s)
    {
        return switch (s)
                {
                    case "block" -> PushReaction.BLOCK;
                    case "destroy" -> PushReaction.DESTROY;
                    case "ignore" -> PushReaction.IGNORE;
                    case "push_only" -> PushReaction.PUSH_ONLY;
                    case "normal" -> PushReaction.NORMAL;
                    default -> throw new ThingParseException("'push_reaction' must be one of: \"block\", \"destroy\", \"ignore\", \"push_only\", \"normal\".");
                };
    }

    private Property<?> getRotationProperty(MutableObject<Map<String, Property<?>>> propertiesByName, String name)
    {
        Property<?> prop = propertiesByName.getValue().get(name);
        if (prop == null)
            throw new ThingParseException("No property with name '" + name + "' declared in block.");
        if (prop.getValueClass() != Direction.class)
            throw new ThingParseException("The specified shape_rotation property is not a Direction property.");
        return prop;
    }

    private void parseBlockState(JsonObject props, BlockBuilder builder)
    {
        for (Map.Entry<String, JsonElement> entry : props.entrySet())
        {
            String name = entry.getKey();
            JsonElement value = entry.getValue();
            builder.setPropertyDefaultValue(name, value.getAsString());
        }
    }

    private Map<String, Property<?>> parseProperties(ObjValue props)
    {
        Map<String, Property<?>> map = new HashMap<>();

        props.forEach((name, val) -> val
                .ifString(str -> str.handle(prop -> {
                    var property = ThingRegistries.PROPERTY.getOptional(ResourceLocation.parse(prop)).orElseThrow(() -> new ThingParseException("Property with name " + prop + " not found in ThingRegistries.PROPERTIES"));
                    if (!property.getName().equals(name))
                        throw new ThingParseException("The stock property '" + prop + "' does not have the expected name '" + name + "' != '" + property.getName() + "'");
                    map.put(name, property);
                }))
                .ifObj(obj -> obj.raw(rawObj -> map.put(name, PropertyType.deserialize(name, rawObj))))
                .typeError());
        return map;
    }

    public static void parseItemBlock(BlockBuilder builder, Any val)
    {
        val
                .ifBool(v -> v.handle(b -> {
                    if (b) createItemBlock(builder, new JsonObject());
                }))
                .ifObj(obj -> obj.raw((JsonObject item) -> createItemBlock(builder, item)))
                .typeError();
    }

    private static void createItemBlock(BlockBuilder builder, JsonObject obj)
    {
        try
        {
            var itemBuilder = JsonThings.itemParser.parseFromElement(builder.getRegistryName(), obj, b -> {
                if (!b.hasType())
                    b.setType(FlexItemType.BLOCK);
            });
            if (itemBuilder != null)
                builder.setItem(itemBuilder);
        }
        catch (Exception e)
        {
            throw new ThingParseException("Exception while parsing nested item in " + builder.getRegistryName(), e);
        }
    }
}
