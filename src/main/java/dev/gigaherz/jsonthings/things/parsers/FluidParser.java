package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.JsonThings;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.FluidBuilder;
import dev.gigaherz.jsonthings.things.properties.PropertyType;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import dev.gigaherz.jsonthings.util.parse.value.ObjValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FluidParser extends ThingParser<FluidBuilder>
{
    public static final Logger LOGGER = LogManager.getLogger();

    public FluidParser(IEventBus bus)
    {
        super(GSON, "fluid");


        bus.addListener(this::register);
    }

    public void register(RegisterEvent event)
    {
        event.register(Registries.FLUID, helper -> {
            LOGGER.info("Started registering Fluid things, errors about unexpected registry domains are harmless...");
            processAndConsumeErrors(getThingType(), getBuilders(), thing ->
                    thing.register(helper::register),
                    BaseBuilder::getRegistryName);
            LOGGER.info("Done processing thingpack Fluids.");
        });
    }
    @Override
    public FluidBuilder processThing(ResourceLocation key, JsonObject data, Consumer<FluidBuilder> builderModification)
    {
        final FluidBuilder builder = FluidBuilder.begin(this, key);

        MutableObject<Map<String, Property<?>>> propertiesByName = new MutableObject<>(new HashMap<>());

        JParse.begin(data)
                .ifKey("fluid_type", val -> parseFluidType(builder, val))
                .ifKey("parent", val -> val.string().map(ResourceLocation::new).handle(builder::setParent))
                .ifKey("type", val -> val.string().map(ResourceLocation::new).handle(builder::setFluidType))
                .ifKey("properties", val -> val.obj().map(this::parseProperties).handle(properties -> {
                    propertiesByName.setValue(properties);
                    builder.setProperties(properties);
                }))
                .ifKey("default_state", val -> val.obj().raw(obj -> parseFluidState(obj, builder)))
                .ifKey("bucket", val -> {
                    var thisName = builder.getRegistryName();
                    var bucketName = new ResourceLocation(thisName.getNamespace(), thisName.getPath() + "_bucket");
                    val
                            .ifBool(v -> v.handle(b -> {
                                if (b) createStockBucketItem(bucketName, builder, new JsonObject());
                            }))
                            .ifObj(obj -> obj.raw((JsonObject item) -> {
                                createStockBucketItem(bucketName, builder, item);
                            }));
                })
                .ifKey("events", val -> val.obj().map(this::parseEvents).handle(builder::setEventMap));

        builderModification.accept(builder);

        builder.setFactory(builder.getFluidType().getFactory(key, data));

        return builder;
    }

    public static void parseFluidType(FluidBuilder builder, Any val)
    {
        val
                .ifString(v -> v.map(ResourceLocation::new).handle(rl -> {
                    builder.setAttributesType(RegistryObject.create(rl, ForgeRegistries.FLUID_TYPES.get()));
                }))
                .ifObj(obj -> obj.raw((JsonObject item) -> {
                    createFluidType(builder, item);
                }))
                .typeError();
    }

    private static void createFluidType(FluidBuilder builder, JsonObject obj)
    {
        try
        {
            var typeBuilder = JsonThings.fluidTypeParser.parseFromElement(builder.getRegistryName(), obj);
            builder.setAttributesType(typeBuilder::get);
        }
        catch(Exception e)
        {
            throw new ThingParseException("Exception while parsing nested fluid in " + builder.getRegistryName(), e);
        }
    }

    private void parseFluidState(JsonObject props, FluidBuilder builder)
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
                    var property = ThingRegistries.PROPERTIES.get(new ResourceLocation(prop));
                    if (property == null)
                        throw new ThingParseException("Property with name " + prop + " not found in ThingRegistries.PROPERTIES");
                    if (!property.getName().equals(name))
                        throw new ThingParseException("The stock property '" + prop + "' does not have the expected name '" + name + "' != '" + property.getName() + "'");
                    map.put(name, property);
                }))
                .ifObj(obj -> obj.raw(rawObj -> map.put(name, PropertyType.deserialize(name, rawObj))))
                .typeError());
        return map;
    }

    private void createStockBucketItem(ResourceLocation bucketName, FluidBuilder builder, JsonObject jsonObject)
    {
        try
        {
            if (jsonObject.has("fluid"))
            {
                throw new ThingParseException("Inline fluid bucket definition cannot contain a fluid entry.");
            }
            jsonObject.addProperty("fluid", builder.getRegistryName().toString());
            builder.setBucket(JsonThings.itemParser.parseFromElement(bucketName, jsonObject, b -> b.setType(FlexItemType.BUCKET)));
        }
        catch(Exception e)
        {
            throw new ThingParseException("Exception while parsing nested bucket in " + builder.getRegistryName(), e);
        }
    }
}
